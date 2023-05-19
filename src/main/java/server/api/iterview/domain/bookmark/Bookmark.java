package server.api.iterview.domain.bookmark;

import lombok.Getter;
import lombok.NoArgsConstructor;
import server.api.iterview.domain.member.Member;
import server.api.iterview.domain.question.Question;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@IdClass(BookmarkId.class)
public class Bookmark {
    @Id
    @ManyToOne
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @Id
    @ManyToOne
    @JoinColumn(name = "QUESTION_ID")
    private Question question;

    @Enumerated(EnumType.STRING)
    private BookmarkStatus status;
}
