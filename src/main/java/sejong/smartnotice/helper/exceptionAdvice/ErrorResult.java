package sejong.smartnotice.helper.exceptionAdvice;

import lombok.Getter;

import java.util.List;

@Getter
public class ErrorResult {
    private final String message; // 에러 메세지
    private final List<ErrorDetail> errors; // 각 필드 에러 정보

    public ErrorResult(String message) {
        this.message = message;
        this.errors = List.of();
    }

    public ErrorResult(String message, List<ErrorDetail> errors) {
        this.message = message;
        this.errors = errors;
    }
}

