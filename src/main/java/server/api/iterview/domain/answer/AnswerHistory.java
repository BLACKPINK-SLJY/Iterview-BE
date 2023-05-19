package server.api.iterview.domain.answer;

import lombok.Getter;
import lombok.NoArgsConstructor;
import server.api.iterview.domain.BaseTimeEntity;
import server.api.iterview.domain.question.Question;

import javax.persistence.*;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
public class AnswerHistory extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ANSWERHISTORY_ID")
    private Long id;

    private String date;

    private Integer count;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUESTION_ID")
    private Question question;
}
