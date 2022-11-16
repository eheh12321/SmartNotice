package sejong.smartnotice.helper.validator;

import javax.validation.Constraint;
import javax.validation.Payload;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.FIELD) // 필드에 적용 가능
@Retention(RetentionPolicy.RUNTIME) // 런타임 시점까지 계속 유지
@Constraint(validatedBy = {PhoneValidator.class}) // 어디서 값을 검증하는지
public @interface Phone {
    String message() default "전화번호 형식이 잘못되었습니다";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
