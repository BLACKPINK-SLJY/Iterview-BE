package server.api.iterview.response.answer;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import server.api.iterview.response.BaseResponseType;

@Getter
@AllArgsConstructor
public enum AnswerResponseType implements BaseResponseType {
    SYNC_DB_AFTER_UPLOAD_VIDEO_OK(20501, "답변 내역 DB 동기화 성공", HttpStatus.OK),
    MY_ANSWER_SUCCESS(20502, "내 답변 보기 응답 성공", HttpStatus.OK),

    NO_ANSWER_RESULT(40501, "해당 유저에 대한 답변 데이타가 없음", HttpStatus.NOT_FOUND),

    ;


    private final Integer code;
    private final String message;
    private final HttpStatus httpStatus;
}
