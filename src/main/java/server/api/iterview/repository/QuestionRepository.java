package server.api.iterview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.transaction.annotation.Transactional;
import server.api.iterview.domain.question.Question;

@Transactional
public interface QuestionRepository extends JpaRepository<Question, Long> {
    void deleteById(Long id);
}
