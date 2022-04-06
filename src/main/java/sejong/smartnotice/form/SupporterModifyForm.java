package sejong.smartnotice.form;

import lombok.Data;
import sejong.smartnotice.domain.member.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class SupporterModifyForm {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String tel;

    @NotNull
    private User user;
}