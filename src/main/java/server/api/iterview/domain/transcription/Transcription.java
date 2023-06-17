package server.api.iterview.domain.transcription;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.api.iterview.domain.answer.Answer;

import javax.persistence.*;

@Entity
@Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Transcription {
    @Id @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "TRANSCRIPTION_ID")
    private Long id;

    private String startTime;
    private String endTime;
    private String confidence;
    private String content;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "ANSWER_ID")
    private Answer answer;
}
