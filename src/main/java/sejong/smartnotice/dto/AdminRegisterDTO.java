package sejong.smartnotice.dto;

import lombok.*;
import sejong.smartnotice.domain.member.AdminType;

import javax.validation.constraints.NotBlank;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminRegisterDTO {

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
