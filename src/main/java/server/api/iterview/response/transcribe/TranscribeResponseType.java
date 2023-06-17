package server.api.iterview.response.transcribe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import server.api.iterview.response.BaseResponseType;

@Getter
@AllArgsConstructor
public enum TranscribeResponseType implements BaseResponseType {
    TRANSCRIBE_OK(20401, "transcribe 성공",HttpStatus.OK),

    TRANSCRIBE_FAIL(40401, "transcribe 실패", HttpStatus.INTERNAL_SERVER_ERROR),
    ;


    private final Integer code;
    private final String message;
    private final HttpStatus httpStatus;
}
