package server.api.iterview.service.answer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.api.iterview.domain.answer.Answer;
import server.api.iterview.domain.answer.TranscriptStatus;
import server.api.iterview.domain.member.Member;
import server.api.iterview.domain.question.Question;
import server.api.iterview.domain.transcription.Transcription;
import server.api.iterview.dto.answer.AnswerResponseDto;
import server.api.iterview.dto.transcription.*;
import server.api.iterview.repository.AnswerRepository;
import server.api.iterview.repository.TranscriptionRepository;
import server.api.iterview.response.BizException;
import server.api.iterview.response.answer.AnswerResponseType;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final TranscriptionRepository transcriptionRepository;

    @Transactional(readOnly = true)
    public Answer findAnswerByMemberAndQuestionId(Member member, Long questionId){
        return answerRepository.findByMemberAndQuestionId(member, questionId)
                .orElseThrow(() -> new BizException(AnswerResponseType.NO_ANSWER_RESULT));
    }

    public void syncDB(Member member, Question question) {
        Answer answer = answerRepository.findByMemberAndQuestion(member, question)
                .orElse(null);

        if(answer == null){
            answerRepository.save(
                    Answer.builder().member(member).question(question).build()
            );
            return;
        }

        transcriptionRepository.deleteAll(transcriptionRepository.findByAnswer(answer));
        answer.updateModifiedDate();
        answer.setTranscriptStatus(TranscriptStatus.N);
        answer.setContent(null);
    }

    public void saveTranscriptionOnAnswer(TranscriptionResultDTO results, Answer answer){
        String transcription = "";
        for(TranscriptionTextDTO transcriptionTextDto : results.getTranscripts()){
            transcription = transcription.concat(transcriptionTextDto.getTranscript());
        }

        answer.setContent(transcription);
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
    public void saveFragmentsBySentence(List<TranscriptionItemDTO> items, Answer answer){
        Boolean endFlag = true;
        String sentence = "";
        String startTime = "";
        String endTime = "";
        for(TranscriptionItemDTO item : items){
            String content = "";
            for(TranscriptionItemAlternativesDTO alternativesDTO : item.getAlternatives()){
                content += alternativesDTO.getContent();
            }
            sentence += content + " ";

            if(endFlag){
                startTime = item.getStart_time();
                endFlag = false;
            }

            if(content.equals(".")){
                sentence = sentence.replace(" . ", ".");
                endFlag = true;

                transcriptionRepository.save(Transcription.builder()
                                .startTime(startTime)
                                .endTime(endTime)
                                .content(sentence)
                                .answer(answer)
                                .build());

                sentence = "";
            }

            endTime = item.getEnd_time();
        }
    }

    public void saveTranscription(TranscriptionResponseDTO transcriptionResponse, Answer answer) {
        transcriptionRepository.deleteAll(transcriptionRepository.findByAnswer(answer));

        TranscriptionResultDTO results = transcriptionResponse.getResults();
        saveTranscriptionOnAnswer(results, answer);
        saveFragmentsBySentence(results.getItems(), answer);

        answer.setTranscriptStatus(TranscriptStatus.Y);
    }

    @Transactional(readOnly = true)
    public AnswerResponseDto getAnswerResponse(Question question, Answer answer, String preSignedUrl) {

        return AnswerResponseDto.builder()
                .category(question.getCategory().name())
                .date(getFormattedAnswerDate(answer.getModifiedDate()))
                .url(preSignedUrl)
                .results(getTranscriptionResult(answer))
                .build();
    }

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
