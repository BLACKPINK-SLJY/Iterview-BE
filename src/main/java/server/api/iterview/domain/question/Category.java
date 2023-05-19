package server.api.iterview.domain.question;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@Getter
@RequiredArgsConstructor
public enum Category {
    IOS("IOS"),
    AOS("AOS"),
    FRONTEND("FRONTEND"),
    BACKEND("BACKEND"),
    ;

    private final String category;
}
