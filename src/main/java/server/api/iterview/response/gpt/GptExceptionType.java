package server.api.iterview.response.gpt;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import server.api.iterview.response.BaseResponseType;

@Getter
@AllArgsConstructor
public enum GptExceptionType implements BaseResponseType {
    GPT_OK(20501, "GPT 답변 요청 성공", HttpStatus.OK),
    GPT_ING(20502, "GPT 답변 생성 중", HttpStatus.OK),

    GPT_FAIL(40501, "OPEN-AI 오류", HttpStatus.INTERNAL_SERVER_ERROR),
    ;


    private final Integer code;
    private final String message;
    private final HttpStatus httpStatus;
}
