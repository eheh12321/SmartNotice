package sejong.smartnotice.helper.dto.request;

import lombok.*;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Getter
@AllArgsConstructor
public class TownModifyRequest {

    @Setter
    private Long id;

    @NotBlank
    private String name;

    @NotNull
    private Long regionCode;
}
