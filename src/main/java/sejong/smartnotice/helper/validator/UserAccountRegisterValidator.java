package sejong.smartnotice.helper.validator;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.springframework.validation.Errors;
import org.springframework.validation.Validator;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.helper.dto.request.register.SupporterRegisterDTO;
import sejong.smartnotice.helper.dto.request.register.UserAccountRegisterDTO;
import sejong.smartnotice.repository.UserAccountRepository;

@RequiredArgsConstructor
@Component
public class UserAccountRegisterValidator implements Validator {

    private final UserAccountRepository userAccountRepository;

    @Override
    public boolean supports(Class<?> clazz) {
        return UserAccountRegisterDTO.class.isAssignableFrom(clazz);
    }

    @Override
    public void validate(Object target, Errors errors) {
        UserAccountRegisterDTO dto = (UserAccountRegisterDTO) target;
        if (userAccountRepository.findByAccount_LoginId(dto.getLoginId()).isPresent()) {
            errors.rejectValue("loginId", "아이디 중복 오류", "이미 사용중인 아이디입니다");
        }
        if (userAccountRepository.findByTel(dto.getTel()).isPresent()) {
            errors.rejectValue("tel", "전화번호 중복 오류", "이미 사용중인 전화번호입니다");
        }

        // 보호자 추가 로직
        if(dto instanceof SupporterRegisterDTO) {
            SupporterRegisterDTO supporterRegisterDTO = (SupporterRegisterDTO) dto;
            if(userAccountRepository.findByTel(supporterRegisterDTO.getUserTel())
                    .filter(userAccount -> userAccount instanceof User)
                    .isEmpty()) {
                errors.rejectValue("userTel", "마을주민 없음 오류", "마을 주민이 존재하지 않습니다");
            }
        }
    }
}

