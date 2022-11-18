package sejong.smartnotice.helper.exceptionAdvice;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.Getter;
import org.springframework.context.MessageSource;
import org.springframework.validation.FieldError;

import java.util.Locale;

@Getter
@AllArgsConstructor
public class ErrorDetail {
    private final String field; // 에러 발생 필드 이름
    private final String message; // 에러 메세지

    public static ErrorDetail from(FieldError e) {
        return new ErrorDetail(e.getField(), e.getDefaultMessage());
    }

    public static ErrorDetail from(FieldError e, MessageSource messageSource, Locale locale) {
        return new ErrorDetail(e.getField(),
                messageSource.getMessage(e, locale));
    }
}
