package sejong.smartnotice.helper.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TownModifyDTO {

    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private Long regionCode;
}
