package server.api.iterview.dto.answer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import server.api.iterview.domain.answer.TranscriptStatus;
import server.api.iterview.domain.bookmark.BookmarkStatus;
import server.api.iterview.dto.transcription.TranscriptionResultDTO;

import java.util.List;

@Getter
@AllArgsConstructor
@Builder
public class AnswerVideoResponseDto {
    private Long questionId;
    private String question;
    private Integer level;
    private List<String> tags;
    private BookmarkStatus bookmarked;

    private String category;
    private String date;

    private String url;
    private String length;
    private TranscriptionResultDTO results;

    private TranscriptStatus creating;
}
