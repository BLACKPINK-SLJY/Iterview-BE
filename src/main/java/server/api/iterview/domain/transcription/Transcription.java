package server.api.iterview.domain.transcription;

import lombok.Getter;
import lombok.NoArgsConstructor;
import server.api.iterview.domain.answer.Answer;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
public class Transcription {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRANSCRIPTION_ID")
    private Long id;

    private String timestamp;

    private String sentence;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ANSWER_ID")
    private Answer answer;
}
