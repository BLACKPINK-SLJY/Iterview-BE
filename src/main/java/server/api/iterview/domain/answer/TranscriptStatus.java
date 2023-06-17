package server.api.iterview.domain.answer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TranscriptStatus {
    Y("Y"),
    N("N"),
    ;

    private final String status;
}
