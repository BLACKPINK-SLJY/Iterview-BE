package server.api.iterview.service.answer;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import server.api.iterview.domain.answer.Answer;
import server.api.iterview.domain.member.Member;
import server.api.iterview.domain.question.Question;
import server.api.iterview.repository.AnswerRepository;

@Service
@Transactional
@RequiredArgsConstructor
@Slf4j
public class AnswerService {
    private final AnswerRepository answerRepository;

    public void update(Member member, Question question) {
        Answer answer = answerRepository.findByMemberAndQuestion(member, question)
                .orElse(null);

        if(answer == null){
            answerRepository.save(
                    Answer.builder().member(member).question(question).build()
            );
            return;
        }

        answer.updateModifiedDate();
    }
}
