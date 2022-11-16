package sejong.smartnotice.helper.dto;

import lombok.*;
import sejong.smartnotice.domain.member.User;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SupporterModifyDTO {

    private Long id;

    @NotBlank
    private String name;

    @NotBlank
    private String tel;
}