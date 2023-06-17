package server.api.iterview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import server.api.iterview.domain.answer.Answer;
import server.api.iterview.domain.member.Member;
import server.api.iterview.domain.question.Question;

import java.util.Optional;

public interface AnswerRepository extends JpaRepository<Answer, Long> {
    Optional<Answer> findByMemberAndQuestion(Member member, Question question);

    @Query("select a from Answer a where a.member = :member and a.question.id = :questionId")
    Optional<Answer> findByMemberAndQuestionId(@Param("member") Member member, @Param("questionId") Long questionId);
}
