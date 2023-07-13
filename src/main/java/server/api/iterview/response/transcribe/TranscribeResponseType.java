package server.api.iterview.response.transcribe;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;
import server.api.iterview.response.BaseResponseType;

@Getter
@AllArgsConstructor
public enum TranscribeResponseType implements BaseResponseType {
    TRANSCRIBE_OK(20401, "transcribe 성공",HttpStatus.OK),
    TRANSCRIBE_ING(20402, "Speech Text 추출 진행 시작", HttpStatus.OK),

    TRANSCRIBE_FAIL(40401, "transcribe 실패", HttpStatus.INTERNAL_SERVER_ERROR),
    ALREADY_TRANSCRIBED(40402, "이미 텍스트 추출된 영상입니다.", HttpStatus.OK),
    ING_TRANSCRIBE(40403, "텍스트 추출이 아직 진행중입니다.", HttpStatus.OK),
    ;


    private final Integer code;
    private final String message;
    private final HttpStatus httpStatus;
}
