package server.api.iterview.domain.bookmark;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.api.iterview.domain.member.Member;
import server.api.iterview.domain.question.Question;

import javax.persistence.*;
import java.time.LocalDateTime;

@Entity
@Getter
@NoArgsConstructor
@IdClass(BookmarkId.class)
@Builder
@AllArgsConstructor
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
    @Builder.Default
    private BookmarkStatus status = BookmarkStatus.N;

    @Builder.Default
    private LocalDateTime modifiedTime = LocalDateTime.now();

    public void setStatus(BookmarkStatus status){
        this.status = status;
        this.modifiedTime = LocalDateTime.now();
    }
}
