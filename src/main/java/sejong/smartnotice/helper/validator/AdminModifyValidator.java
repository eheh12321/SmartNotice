package sejong.smartnotice.helper.validator;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import sejong.smartnotice.helper.dto.request.AdminRequest.AdminModifyRequest;
import sejong.smartnotice.repository.UserAccountRepository;

@RequiredArgsConstructor
@Component
public class AdminModifyValidator implements Validator {

    private final UserAccountRepository userAccountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return AdminModifyValidateDto.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        AdminModifyValidateDto dto = (AdminModifyValidateDto) target;
        AdminModifyRequest request = dto.getRequest();
        userAccountRepository.findByTel(request.getTel())
                .ifPresent(findAdmin -> {
                    if (!findAdmin.getId().equals(dto.getId())) {
                        errors.rejectValue("tel", "전화번호 중복 오류", "이미 사용중인 전화번호입니다");
                    }
                });
    }

    @Getter
    @AllArgsConstructor
    public static class AdminModifyValidateDto {
        private Long id;
        private AdminModifyRequest request;
    }
}
