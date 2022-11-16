package sejong.smartnotice.helper.dto.request.register;

import lombok.Getter;
import lombok.Setter;
import sejong.smartnotice.helper.validator.Phone;

import javax.validation.constraints.NotBlank;

@Getter @Setter
public abstract class UserAccountRegisterDTO {

    @NotBlank
    private String name;

    @Phone
    private String tel;

    @NotBlank
    private String loginId;

    @NotBlank
    private String loginPw;

    public UserAccountRegisterDTO() {
    }

    public UserAccountRegisterDTO(String name, String tel, String loginId, String loginPw) {
        this.name = name;
        this.tel = tel;
        this.loginId = loginId;
        this.loginPw = loginPw;
    }
}
