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
import server.api.iterview.dto.transcription.*;
import server.api.iterview.repository.AnswerRepository;
import server.api.iterview.repository.TranscriptionRepository;
import server.api.iterview.response.BizException;
import server.api.iterview.response.answer.AnswerResponseType;

import java.util.List;


@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AnswerService {
    private final AnswerRepository answerRepository;
    private final TranscriptionRepository transcriptionRepository;

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
            transcription += transcriptionTextDto.getTranscript();
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

    public void saveTranscription(TranscriptionResponseDTO transcriptionResponse, Answer answer) {
        transcriptionRepository.deleteAll(transcriptionRepository.findByAnswer(answer));

        TranscriptionResultDTO results = transcriptionResponse.getResults();
        saveTranscriptionOnAnswer(results, answer);
        saveTranscriptionFragments(results.getItems(), answer);

        answer.setTranscriptStatus(TranscriptStatus.Y);
    }
}
