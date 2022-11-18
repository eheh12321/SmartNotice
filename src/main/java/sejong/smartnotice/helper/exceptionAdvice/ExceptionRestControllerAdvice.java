package sejong.smartnotice.helper.exceptionAdvice;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice(basePackages = "sejong.smartnotice.controller.restController")
public class ExceptionRestControllerAdvice {

    /**
     * BindException 처리
     * 입력 폼 데이터 검증에 실패한 경우에 대한 처리
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ErrorResult handleBindException(BindException e) {
        log.warn("[BindException] handle -> {}", e.getObjectName());

        // 필드 에러 정보 모음
        List<ErrorDetail> fieldErrorList = e.getFieldErrors().stream()
                .map(ErrorDetail::from)
                .collect(Collectors.toList());

        return new ErrorResult("입력 폼 데이터 검증 실패", fieldErrorList);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult handleIllegalStateException(IllegalStateException e) {
        // 메소드가 요구된 처리를 하기에 적합한 상태에 있지 않을때
        log.error("[IllegalStateException] handle");
        return new ErrorResult("IllegalStateException");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult handleNullPointerException(IllegalArgumentException e) {
        // 부정한 인수, 또는 부적절한 인수를 메서드에 전달했을 때
        log.error("[IllegalArgumentException] handle");
        return new ErrorResult("IllegalArgumentException");
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult handleNullPointerException(NullPointerException e) {
        log.error("[NullPointerException] handle");
        return new ErrorResult("NullPointerException");
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler
    public ErrorResult handleSecurityException(SecurityException e) {
        // 리소스 접근 권한이 없는 경우
        log.error("[SecurityException] handle");
        return new ErrorResult("SecurityException");
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult handleException(Exception e) {
        log.error("[Exception] handle");
        return new ErrorResult("Exception");
    }
}
