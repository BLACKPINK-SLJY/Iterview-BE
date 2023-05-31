package server.api.iterview.response;

import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class ExceptionHandler {

    @org.springframework.web.bind.annotation.ExceptionHandler(BizException.class)
    public ApiResponse<Object> bizException(BizException e){
        log.error("[{}] - {}", e.getStackTrace()[0], e.getMessage());
        return ApiResponse.of(e.getBaseExceptionType());
    }

    @org.springframework.web.bind.annotation.ExceptionHandler(Exception.class)
    public ApiResponse<Object> notResolvedException(Exception e){
        log.error("[{}] - {}", e.getStackTrace()[0], e.getMessage());
        return ApiResponse.of(InternalServerExceptionType.INTERNAL_SERVER_ERROR);
    }
}
