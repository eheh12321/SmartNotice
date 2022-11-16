package sejong.smartnotice.helper.dto;

import lombok.*;
import sejong.smartnotice.domain.Weather;

import javax.validation.constraints.NotBlank;

@Getter @Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class WeatherResponseDTO {

    private Weather weather;

    @NotBlank
    private String message;
}
