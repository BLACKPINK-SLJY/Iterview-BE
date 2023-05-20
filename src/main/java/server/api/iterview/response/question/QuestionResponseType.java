package server.api.iterview.response.question;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import server.api.iterview.response.BaseResponseType;

@Getter
@AllArgsConstructor
public enum QuestionResponseType implements BaseResponseType {
    INSERT_SUCCESS(20200, "질문 저장 성공", HttpStatus.OK),
    DELETE_SUCCESS(20201, "질문 삭제 성공", HttpStatus.OK),

    INVALID_CATEGORY(40200, "카테고리는 ios, aos, fe, be 중 하나이어야 합니다.", HttpStatus.BAD_REQUEST),
    NOT_EXIST(40201, "해당하는 질문이 존재하지 않습니다.", HttpStatus.NOT_FOUND),
    ;

    private final Integer code;
    private final String message;
    private final HttpStatus httpStatus;
}
