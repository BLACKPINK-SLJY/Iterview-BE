package server.api.iterview.domain.question;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.api.iterview.domain.bookmark.Bookmark;
import server.api.iterview.domain.folder.Folder;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;


@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Question {
    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "QUESTION_ID")
    private Long id;

    @ManyToMany(mappedBy = "questions")
    private List<Folder> folders = new ArrayList<>();

    @ManyToMany
    @JoinTable(name = "QUESTION_TAG",
            joinColumns = @JoinColumn(name = "QUESTION_ID"),
            inverseJoinColumns = @JoinColumn(name = "TAG_ID"))
    private List<Tag> tags = new ArrayList<>();

    private String content;

    private String keywords;

    @Enumerated(EnumType.STRING)
    private Category category;

    private Integer level;

    @OneToMany(mappedBy = "question", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<Bookmark> bookmarks;

}
