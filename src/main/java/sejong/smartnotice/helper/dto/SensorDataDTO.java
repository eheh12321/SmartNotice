package sejong.smartnotice.helper.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorDataDTO {
    private Long deviceId;
    private LocalDateTime measureTime;
    private double temp;
    private double co2;
    private double oxy;
    private double lumnc;
    private double action_;
}
