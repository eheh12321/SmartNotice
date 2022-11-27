package sejong.smartnotice.helper.exceptionAdvice;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import javax.servlet.http.HttpServletRequest;
import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@RequiredArgsConstructor
@RestControllerAdvice(basePackages = "sejong.smartnotice.controller.restController")
public class ExceptionRestControllerAdvice {

    private final MessageSource messageSource;

    /**
     * BindException 처리
     * 입력 폼 데이터 검증에 실패한 경우에 대한 처리
     */
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler(BindException.class)
    public ErrorResult handleBindException(BindException e,
                                           HttpServletRequest request) {
        log.warn("[BindException] handle -> {}", e.getObjectName());

        // 필드 에러 정보 모음
        List<ErrorDetail> fieldErrorList = e.getFieldErrors().stream()
                .map(error -> ErrorDetail.from(error, messageSource, request.getLocale()))
                .collect(Collectors.toList());

        return new ErrorResult(messageSource.getMessage(
                "BindException", null, request.getLocale()), fieldErrorList);
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult handleIllegalStateException(IllegalStateException e,
                                                   HttpServletRequest request) {
        // 메소드가 요구된 처리를 하기에 적합한 상태에 있지 않을때
        log.error("[IllegalStateException] handle");
        return new ErrorResult(messageSource.getMessage(
                "IllegalStateException", null, request.getLocale()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult handleNullPointerException(IllegalArgumentException e,
                                                  HttpServletRequest request) {
        // 부정한 인수, 또는 부적절한 인수를 메서드에 전달했을 때
        log.error("[IllegalArgumentException] handle");
        return new ErrorResult(messageSource.getMessage(
                "IllegalArgumentException", null, request.getLocale()));
    }

    @ResponseStatus(HttpStatus.BAD_REQUEST)
    @ExceptionHandler
    public ErrorResult handleNullPointerException(NullPointerException e,
                                                  HttpServletRequest request) {
        log.error("[NullPointerException] handle");
        return new ErrorResult(messageSource.getMessage(
                "NullPointerException", null, request.getLocale()));
    }

    @ResponseStatus(HttpStatus.FORBIDDEN)
    @ExceptionHandler
    public ErrorResult handleSecurityException(SecurityException e
            ,HttpServletRequest request) {
        // 리소스 접근 권한이 없는 경우
        log.error("[SecurityException] handle");
        return new ErrorResult(messageSource.getMessage(
                "SecurityException", null, request.getLocale()));
    }

    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ExceptionHandler
    public ErrorResult handleException(Exception e,
                                       HttpServletRequest request) {
        log.error("[Exception] handle");
        return new ErrorResult(messageSource.getMessage(
                "Exception", null, request.getLocale()));
    }
}
