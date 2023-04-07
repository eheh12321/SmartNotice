package sejong.smartnotice.helper.dto;

import lombok.*;
import sejong.smartnotice.domain.device.Sensor;

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

    public static SensorDataDTO from(Sensor sensor) {
        return new SensorDataDTO(
                sensor.getId(),
                sensor.getMeasureTime(),
                sensor.getTemp(),
                sensor.getCo2(),
                sensor.getOxy(),
                sensor.getLumnc(),
                sensor.getAction()
        );
    }
}
