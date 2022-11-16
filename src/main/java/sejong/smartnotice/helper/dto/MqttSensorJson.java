package sejong.smartnotice.helper.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class MqttSensorJson {
    private String client;
    private LocalDateTime measureTime;
    private double temp;
    private double co2;
    private double oxy;
    private double lumnc;
    private double action;
}
