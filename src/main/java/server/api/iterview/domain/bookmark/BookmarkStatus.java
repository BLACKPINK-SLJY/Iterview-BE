package server.api.iterview.domain.bookmark;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum BookmarkStatus {
    Y("Y"),
    N("N"),
    ;

    private final String status;
}
