package server.api.iterview.dto.answer;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import server.api.iterview.dto.transcription.TranscriptionResultDTO;

@Getter
@AllArgsConstructor
@Builder
public class AnswerResponseDto{
    private String category;
    private String date;
    private String url;
    private String length;
    private TranscriptionResultDTO results;
}
