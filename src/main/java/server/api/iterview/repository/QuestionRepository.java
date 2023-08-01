package server.api.iterview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import server.api.iterview.domain.bookmark.BookmarkStatus;
import server.api.iterview.domain.member.Member;
import server.api.iterview.domain.question.Category;
import server.api.iterview.domain.question.Question;

import java.util.List;

@Transactional
public interface QuestionRepository extends JpaRepository<Question, Long> {
    void deleteById(Long id);
    List<Question> findByCategory(Category category);

    @Query(nativeQuery = true,
            value = "select * from question q " +
                    "inner join question_tag qt " +
                    "on q.question_id = qt.question_id " +
                    "left join tag t " +
                    "on qt.tag_id = t.tag_id " +
                    "where LOWER(q.content) like LOWER(concat('%', :word, '%')) or LOWER(t.name) like LOWER(concat('%', :word, '%')) " +
                    "order by q.question_id"
            )
    List<Question> getSearchResults(@Param("word") String word);

    @Query("select q from Question q order by q.level")
    List<Question> getAllQuestionsOrderByLevel();

    List<Question> getQuestionByCategoryOrderByLevel(Category category);

    @Query("select q from Question q " +
            "inner join Answer a " +
            "on q.id = a.question.id " +
            "where a.member = :member")
    List<Question> getMyAnsweredQuestions(@Param("member")Member member);

    @Query("select q from Question q " +
            "inner join Answer a " +
            "on q.id = a.question.id " +
            "where a.member = :member and q.category = :category")
    List<Question> getMyAnsweredQuestionsByCategory(@Param("member")Member member, @Param("category")Category category);

    @Query("select q from Question q " +
            "inner join Bookmark b " +
            "on q.id = b.question.id " +
            "where b.member = :member and b.status = :status " +
            "order by b.modifiedTime")
    List<Question> getMyBookmarkedQuestions(@Param("member") Member member, @Param("status")BookmarkStatus status);

    @Query("select q from Question q " +
            "inner join Bookmark b " +
            "on q.id = b.question.id " +
            "where b.member = :member and b.status = :status and q.category = :category " +
            "order by b.modifiedTime")
    List<Question> getMyBookmarkedQuestionsByCategory(@Param("member") Member member, @Param("status") BookmarkStatus status, @Param("category") Category category);

    @Query(nativeQuery = true, value = "select * from question q where category = :category order by RAND() limit 10")
    List<Question> getRandom10QuestionsByCategory(@Param("category") String category);
}
