package server.api.iterview.service.answer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.api.iterview.domain.answer.Answer;
import server.api.iterview.domain.answer.TranscriptStatus;
import server.api.iterview.domain.bookmark.Bookmark;
import server.api.iterview.domain.bookmark.BookmarkStatus;
import server.api.iterview.domain.member.Member;
import server.api.iterview.domain.question.Question;
import server.api.iterview.domain.question.Tag;
import server.api.iterview.domain.transcription.Transcription;
import server.api.iterview.dto.answer.AnswerReportResponseDto;
import server.api.iterview.dto.answer.AnswerVideoResponseDto;
import server.api.iterview.dto.transcription.*;
import server.api.iterview.repository.AnswerRepository;
import server.api.iterview.repository.BookmarkRepository;
import server.api.iterview.repository.TranscriptionRepository;
import server.api.iterview.response.BizException;
import server.api.iterview.response.answer.AnswerResponseType;
import server.api.iterview.service.gpt.GptService;
import server.api.iterview.service.transcribe.TranscriptionService;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
@Slf4j
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final TranscriptionRepository transcriptionRepository;
    private final BookmarkRepository bookmarkRepository;
    private final TranscriptionService transcriptionService;
    private final GptService gptService;

    @Transactional(readOnly = true)
    public Answer findAnswerByMemberAndQuestionId(Member member, Long questionId){
        return answerRepository.findByMemberAndQuestionId(member, questionId)
                .orElseThrow(() -> new BizException(AnswerResponseType.NO_ANSWER_RESULT));
    }

    @Transactional
    public void syncDB(Member member, Question question) {
        Answer answer = answerRepository.findByMemberAndQuestion(member, question)
                .orElse(null);

        if (answer == null) {
            answerRepository.save(
                    Answer.builder().member(member).question(question).build()
            );
            return;
        }

        transcriptionRepository.deleteAll(transcriptionRepository.findByAnswer(answer));
        answer.deleteContents();
    }

    public void saveTranscriptionFragments(List<TranscriptionItemDTO> items, Answer answer){
        for(TranscriptionItemDTO item : items){
            String startTime = item.getStart_time();
            String endTime = item.getEnd_time();

            for(TranscriptionItemAlternativesDTO alternative : item.getAlternatives()){
                String confidence = alternative.getConfidence();
                String content = alternative.getContent();

                transcriptionRepository.save(Transcription.builder()
                        .startTime(startTime)
                        .endTime(endTime)
                        .confidence(confidence)
                        .content(content)
                        .answer(answer)
                        .build());
            }
        }
    }

    /**
     * Amazon Transcribe API로부터, 토큰별로 나뉘어서 온 아이템들을 문장으로 묶어 DB에 저장.
     */
    public String saveFragmentsBySentence(List<TranscriptionItemDTO> items, Answer answer){
        Boolean wrong = false;
        Boolean endFlag = true;
        StringBuilder sentence = new StringBuilder();
        String startTime = "";
        String endTime = "";
        StringBuilder entireContent = new StringBuilder();
        for(TranscriptionItemDTO item : items){
            StringBuilder content = new StringBuilder();
            for(TranscriptionItemAlternativesDTO alternativesDTO : item.getAlternatives()){
                Double confidence = Double.parseDouble(alternativesDTO.getConfidence());
                if(confidence <= 0.75 && !alternativesDTO.getContent().equals(".") && !alternativesDTO.getContent().equals(",")){
                    wrong = true;
                }

                if(!wrong){
                    content.append(alternativesDTO.getContent());
                }else{
                    content.append("@@").append(alternativesDTO.getContent()).append("@@");
                    wrong = false;
                }

            }
            sentence.append(content).append(" ");

            if(endFlag){
                startTime = item.getStart_time();
                endFlag = false;
            }

            if(content.toString().equals(".")){
                String sentenceString = sentence.toString().replace(" . ", ". ");
                endFlag = true;

                transcriptionRepository.save(Transcription.builder()
                                .startTime(startTime)
                                .endTime(endTime)
                                .content(sentenceString)
                                .answer(answer)
                                .build());

                entireContent.append(sentenceString);
                sentence.setLength(0);
            }

            endTime = item.getEnd_time();
        }

        if(!endFlag){
            transcriptionRepository.save(Transcription.builder()
                            .startTime(startTime)
                            .endTime(endTime)
                            .content(sentence.toString())
                            .answer(answer)
                    .build());

            entireContent.append(sentence);
        }

        return entireContent.toString();
    }

    public Answer saveTranscription(TranscriptionResponseDTO transcriptionResponse, Answer answer) {
        transcriptionRepository.deleteAll(transcriptionRepository.findByAnswer(answer));

//        TranscriptionResultDTO results = transcriptionResponse.getResults();

//        String transcription = "";
//        for(TranscriptionTextDTO transcriptionTextDto : results.getTranscripts()){
//            transcription = transcription.concat(transcriptionTextDto.getTranscript());
//        }
//
//        answer.setContent(transcription);
        answer.setContent(saveFragmentsBySentence(transcriptionResponse.getResults().getItems(), answer));

        return answerRepository.save(answer);
    }

    @Transactional(readOnly = true)
    public AnswerReportResponseDto getAnswerResponse(Member member, Question question, Answer answer) {
        Bookmark bookmark = bookmarkRepository.findByMemberAndQuestion(member, question)
                        .orElse(null);

        return AnswerReportResponseDto.builder()
                .questionId(question.getId())
                .question(question.getContent())
                .level(question.getLevel())
                .tags(question.getTags().stream().map(Tag::getName).collect(Collectors.toList()))
                .bookmarked(bookmark != null ? bookmark.getStatus() : BookmarkStatus.N)

                .transcription(answer.getContent())

                .score(answer.getScore())
                .feedback(answer.getFeedback())
                .bestAnswer(answer.getBestAnswer())

                .created(answer.getTranscriptStatus())
                .build();
    }

    /**
     * S3에 저장된 비디오를 가지고 STT 작업 수행.
     * 응답 받은 데이터를 데이터베이스에 저장.
     * 데이터베이스에 추출된 텍스트가 저장되었으면 다시 Open-AI에 요청
     * STT 작업과 Open-AI API요청이 오래 걸리기 때문에 비동기 처리
     */
    @Async
    public void extractTextAndSaveAndRequestOpenAI(Member member, Long questionId, Answer answer) {
        try {
            TranscriptionResponseDTO responseDTO = transcriptionService.extractSpeechTextFromVideo(member, questionId);
            Answer newAnswer = saveTranscription(responseDTO, answer);

            // 저장 되었으니 GPT에 평가 요청
            gptService.evaluateByGPT(newAnswer);
        }catch (Exception e){
            // 텍스트 추출 or GPT 평가 작업 중 에러가 발생하였을 경우 Transcription Status를 N으로 저장
            answer.setTranscriptStatus(TranscriptStatus.N);
            answerRepository.save(answer);
        }
    }

    /**
     * Transcription Status를 ING로 변경
     * Transaction 때문에 분리
     */
    @Transactional
    public void updateTranscriptStatusToING(Answer answer){
        answer.setTranscriptStatus(TranscriptStatus.ING);
    }

    /**
     * 내 녹화 영상 보러가기
     */
    @Transactional(readOnly = true)
    public AnswerVideoResponseDto getReplayAnswerResponse(Member member, Question question, Answer answer, String preSignedUrl) {
        Bookmark bookmark = bookmarkRepository.findByMemberAndQuestion(member, question)
                .orElse(null);

        return AnswerVideoResponseDto.builder()
                .questionId(question.getId())
                .question(question.getContent())
                .level(question.getLevel())
                .tags(question.getTags().stream().map(Tag::getName).collect(Collectors.toList()))
                .bookmarked(bookmark != null ? bookmark.getStatus() : BookmarkStatus.N)

                .category(question.getCategory().getCategory())
                .date(getFormattedAnswerDate(answer.getModifiedDate()))

                .url(preSignedUrl)
                .results(getTranscriptionResult(answer))

                .created(answer.getTranscriptStatus())
                .build();
    }

    @Transactional
    public TranscriptionResultDTO getTranscriptionResult(Answer answer){
        List<TranscriptionTextDTO> transcripts = List.of(new TranscriptionTextDTO(answer.getContent()));

        List<TranscriptionItemDTO> items = new ArrayList<>();
        for(Transcription transcription : answer.getTranscriptions()){
            List<TranscriptionItemAlternativesDTO> alternatives = List.of(TranscriptionItemAlternativesDTO.builder()
                    .confidence(transcription.getConfidence())
                    .content(transcription.getContent())
                    .build());

            items.add(TranscriptionItemDTO.builder()
                    .start_time(transcription.getStartTime())
                    .end_time(transcription.getEndTime())
                    .alternatives(alternatives)
                    .build());
        }

        return TranscriptionResultDTO.builder()
                .transcripts(transcripts)
                .items(items)
                .build();
    }

    public String getFormattedAnswerDate(LocalDateTime date){
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy.MM.dd ");

        return date.format(formatter).concat(getDayOfWeek(date.getDayOfWeek().getValue()));
    }

    private String getDayOfWeek(Integer day){
        switch (day){
            case 1:
                return "월요일";
            case 2:
                return "화요일";
            case 3:
                return "수요일";
            case 4:
                return "목요일";
            case 5:
                return "금요일";
            case 6:
                return "토요일";
            case 7:
                return "일요일";
            default:
                return "null";
        }
    }
}
