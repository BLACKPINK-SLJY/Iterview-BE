package server.api.iterview.domain.question;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.persistence.*;

import java.util.ArrayList;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Tag {
    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "TAG_ID")
    private Long id;

    private String name;

    @ManyToMany(mappedBy = "tags")
    private List<Question> questions = new ArrayList<>();
}
