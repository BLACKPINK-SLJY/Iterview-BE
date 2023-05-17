package server.api.iterview.response.jwt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import server.api.iterview.response.BaseResponseType;

@Getter
@AllArgsConstructor
public enum JwtResponseType implements BaseResponseType {
    INVALID_REFRESH_TOKEN(4001, "유효하지 않은 리프레시 토큰입니다.", HttpStatus.BAD_REQUEST),
    INVALID_ACCESS_TOKEN(4002, "유효하지 않은 엑세스 토큰입니다.", HttpStatus.BAD_REQUEST),
    ACCESS_TOKEN_EXPIRED(4003, "엑세스 토큰의 유효기간이 만료되었습니다.", HttpStatus.BAD_REQUEST),
    REFRESH_TOKEN_EXPIRED(4004, "리프레시 토큰의 유효기간이 만료되었습니다.", HttpStatus.BAD_REQUEST),
    BAD_TOKEN(4005, "잘못된 토큰 값입니다.", HttpStatus.BAD_REQUEST),
    EMPTY_TOKEN(4006, "토큰 값이 비어있습니다.", HttpStatus.BAD_REQUEST),
    MALFORMED_JWT(4007, "잘못된 JWT 서명입니다.", HttpStatus.BAD_REQUEST),
    UNSUPPORTED_JWT(4008, "지원되지 않는 JWT 토큰입니다.", HttpStatus.BAD_REQUEST),
    ;

    private final Integer code;
    private final String message;
    private final HttpStatus httpStatus;
}
