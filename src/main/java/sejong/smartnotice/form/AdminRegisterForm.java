package sejong.smartnotice.form;

import lombok.Data;
import sejong.smartnotice.domain.member.AdminType;

import javax.validation.constraints.NotBlank;

@Data
public class AdminRegisterForm {

    @NotBlank
    private String name;

    @NotBlank
    private String tel;

    @NotBlank
    private String loginId;

    @NotBlank
    private String loginPw;

    private AdminType type;
}
