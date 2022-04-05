package sejong.smartnotice.form;

import lombok.Data;
import sejong.smartnotice.domain.member.AdminRole;

import javax.validation.constraints.NotBlank;

@Data
public class AdminModifyForm {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String tel;

    private AdminRole role;
}
