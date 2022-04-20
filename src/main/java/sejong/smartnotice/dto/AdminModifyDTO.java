package sejong.smartnotice.dto;

import lombok.*;
import sejong.smartnotice.domain.member.AdminType;

import javax.validation.constraints.NotBlank;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AdminModifyDTO {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String tel;
}
