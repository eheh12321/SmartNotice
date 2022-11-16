package sejong.smartnotice.helper.exceptionAdvice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@Slf4j
@RestControllerAdvice(basePackages = "sejong.smartnotice.controller.restController")
public class ExceptionRestControllerAdvice {

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult handleIllegalStateException(IllegalStateException e) {
        // 메소드가 요구된 처리를 하기에 적합한 상태에 있지 않을때
        log.error("[IllegalStateException e]", e);
        return new ErrorResult("IllegalStateException", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult handleNullPointerException(IllegalArgumentException e) {
        // 부정한 인수, 또는 부적절한 인수를 메서드에 전달했을 때
        log.error("[IllegalArgumentException e]", e);
        return new ErrorResult("IllegalArgumentException", e.getMessage());
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult handleNullPointerException(NullPointerException e) {
        log.error("[NullPointerException e]", e);
        return new ErrorResult("NullPointerException", e.getMessage());
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler
    public ErrorResult handleSecurityException(SecurityException e) {
        // 리소스 접근 권한이 없는 경우
        log.error("[SecurityException e]", e);
        return new ErrorResult("SecurityException", e.getMessage());
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult handleException(Exception e) {
        log.error("[Exception e]", e);
        return new ErrorResult("Exception", e.getMessage());
    }
}
