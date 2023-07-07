package server.api.iterview.domain.answer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import server.api.iterview.domain.BaseTimeEntity;
import server.api.iterview.domain.member.Member;
import server.api.iterview.domain.question.Question;
import server.api.iterview.domain.transcription.Transcription;

import javax.persistence.*;

import java.time.LocalDateTime;
import java.util.List;

import static javax.persistence.GenerationType.IDENTITY;

@Entity
@Getter
@NoArgsConstructor
@Builder
@AllArgsConstructor
public class Answer extends BaseTimeEntity {
    @Id @GeneratedValue(strategy = IDENTITY)
    @Column(name = "ANSWER_ID")
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "MEMBER_ID")
    private Member member;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "QUESTION_ID")
    private Question question;

    @OneToMany(mappedBy = "answer", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Transcription> transcriptions;

    private String content;
    private Integer score;

    @Column(length = 1000)
    private String feedback;

    @Column(length = 1000)
    private String bestAnswer;

    @Enumerated(EnumType.STRING)
    @Builder.Default
    private TranscriptStatus transcriptStatus = TranscriptStatus.N;

    public void updateModifiedDate(){
        super.setModifiedDate(LocalDateTime.now());
    }

    public void setContent(String content){
        this.content = content;
    }

    public void setTranscriptStatus(TranscriptStatus status){
        this.transcriptStatus = status;
    }
}
