package sejong.smartnotice.helper.dto;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TownRegisterDTO {

    @NotBlank
    private String name;

    @NotNull
    private Long regionCode;
}
