package sejong.smartnotice.dto;

import lombok.*;
import org.hibernate.validator.constraints.Range;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterDTO {

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

    @NotBlank
    private String loginId;

    @NotBlank
    private String loginPw;
}
