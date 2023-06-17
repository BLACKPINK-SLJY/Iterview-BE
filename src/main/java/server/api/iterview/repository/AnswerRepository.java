package server.api.iterview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import server.api.iterview.domain.answer.Answer;
import server.api.iterview.domain.member.Member;
import server.api.iterview.domain.question.Question;

import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Optional<Answer> findByMemberAndQuestion(Member member, Question question);
}
