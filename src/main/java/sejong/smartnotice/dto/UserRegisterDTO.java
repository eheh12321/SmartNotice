package sejong.smartnotice.dto;

import lombok.Data;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class UserRegisterDTO {

    @NotBlank
    private String loginId;

    @NotBlank
    private String loginPw;

    @NotBlank
    private String name;

    @NotBlank
    private String tel;

    @NotBlank
    private String address;

    @NotNull
    private Long townId;

    @NotNull
    @Range(min = 1, max = 150)
    private Integer age;
}
