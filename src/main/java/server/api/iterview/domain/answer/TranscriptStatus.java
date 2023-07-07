package server.api.iterview.domain.answer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum TranscriptStatus {
    Y("YES"),
    N("NO"),
    ;

    private final String status;
}
