package server.api.iterview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import server.api.iterview.domain.question.Category;
import server.api.iterview.domain.question.Question;

import java.util.List;

@Transactional
public interface QuestionRepository extends JpaRepository<Question, Long> {
    void deleteById(Long id);
    List<Question> findByCategory(Category category);

    @Query(nativeQuery = true,
            value = "select * from Question q " +
                    "inner join QUESTION_TAG qt " +
                    "on q.question_id = qt.question_id " +
                    "inner join Tag t " +
                    "on qt.tag_id = t.tag_id " +
                    "where q.content like concat('%', :word, '%') or t.name like concat('%', :word, '%') " +
                    "order by q.question_id"
            )
    List<Question> getSearchResults(@Param("word") String word);

    @Query("select q from Question q order by q.level")
    List<Question> getAllQuestionsOrderByLevel();

    List<Question> getQuestionByCategoryOrderByLevel(Category category);
}
