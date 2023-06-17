package server.api.iterview.domain.answer;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum AnsweredStatus {
    Y("Y"),
    N("N"),
    ;

    private final String status;
}
