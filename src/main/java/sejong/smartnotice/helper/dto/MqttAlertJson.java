package sejong.smartnotice.helper.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MqttAlertJson {
    private String client;
    private String emergency;
}
