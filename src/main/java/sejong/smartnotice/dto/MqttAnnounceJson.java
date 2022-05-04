package sejong.smartnotice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MqttAnnounceJson {
    private String producer;
    private String message;
    private String type;
    private String status;
    private String data;
}
