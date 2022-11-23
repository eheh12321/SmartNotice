package sejong.smartnotice.helper.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import sejong.smartnotice.helper.dto.request.AdminModifyRequest;
import sejong.smartnotice.repository.UserAccountRepository;

@RequiredArgsConstructor
@Component
public class AdminModifyValidator implements Validator {

    private final UserAccountRepository userAccountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return AdminModifyRequest.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AdminModifyRequest dto = (AdminModifyRequest) target;
        userAccountRepository.findByTel(dto.getTel())
                .ifPresent(findAdmin -> {
                    if (!findAdmin.getId().equals(dto.getId())) {
                        errors.rejectValue("tel", "전화번호 중복 오류", "이미 사용중인 전화번호입니다");
                    }
                });
    }
}
