package sejong.smartnotice.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupporterRegisterDTO {

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
