package sejong.smartnotice.dto;

import lombok.*;

import java.time.LocalDateTime;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class SensorDataDTO {
    private LocalDateTime receiveTime;
    private Double temp;
    private Double motion;
    private Double lumi;
    private Double oxygen;
    private Double co2;
}
