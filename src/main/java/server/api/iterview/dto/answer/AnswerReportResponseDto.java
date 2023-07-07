package server.api.iterview.dto.answer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import server.api.iterview.domain.answer.TranscriptStatus;
import server.api.iterview.domain.bookmark.BookmarkStatus;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class AnswerReportResponseDto {
    private Long questionId;
    private String question;
    private Integer level;
    private List<String> tags;
    private BookmarkStatus bookmarked;

    private String transcription;

    private Integer score;
    private String feedback;
    private String bestAnswer;

    private TranscriptStatus creating;
}
