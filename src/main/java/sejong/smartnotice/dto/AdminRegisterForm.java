package sejong.smartnotice.dto;

import lombok.Data;
import sejong.smartnotice.domain.member.AdminRole;

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

    private AdminRole role;
}
