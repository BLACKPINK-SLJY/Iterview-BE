package server.api.iterview.response.answer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import server.api.iterview.response.BaseResponseType;

@Getter
@AllArgsConstructor
public enum AnswerResponseType implements BaseResponseType {
    SYNC_DB_AFTER_UPLOAD_VIDEO_OK(20501, "답변 내역 DB 동기화 성공", HttpStatus.OK),


    ;


    private final Integer code;
    private final String message;
    private final HttpStatus httpStatus;
}
