package sejong.smartnotice.helper.dto.request.register;

import lombok.*;
import sejong.smartnotice.helper.validator.Phone;

@Getter @Setter
public class SupporterRegisterDTO extends UserAccountRegisterDTO {

    @Phone
    private String userTel;

    public SupporterRegisterDTO() {
    }

    public SupporterRegisterDTO(String name, String tel, String loginId, String loginPw, String userTel) {
        super(name, tel, loginId, loginPw);
        this.userTel = userTel;
    }
}
