package server.api.iterview.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import server.api.iterview.domain.bookmark.Bookmark;
import server.api.iterview.domain.bookmark.BookmarkStatus;
import server.api.iterview.domain.member.Member;
import server.api.iterview.domain.question.Question;

import java.util.Optional;

public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    Optional<Bookmark> findByMemberAndQuestion(Member member, Question question);

    Long countByQuestionAndStatus(Question question, BookmarkStatus status);
}
