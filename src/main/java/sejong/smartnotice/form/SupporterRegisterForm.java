package sejong.smartnotice.form;

import lombok.Data;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SupporterRegisterForm {

    @NotBlank
    private String name;

    @NotBlank
    private String tel;

    @NotBlank
    private String loginId;

    @NotBlank
    private String loginPw;

    @NotNull
    private Long userId;
}
