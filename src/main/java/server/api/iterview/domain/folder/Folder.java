package server.api.iterview.domain.folder;

import lombok.Getter;
import lombok.NoArgsConstructor;
import server.api.iterview.domain.member.Member;
import server.api.iterview.domain.question.Question;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class Folder {
    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "FOLDER_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ManyToMany
    @JoinTable(name = "FOLDER_QUESTION",
            joinColumns = @JoinColumn(name = "FORLDER_ID"),
            inverseJoinColumns = @JoinColumn(name = "QUESTION_ID"))
    private List<Question> questions = new ArrayList<>();

    private String title;

}
