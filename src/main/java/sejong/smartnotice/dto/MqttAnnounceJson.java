package sejong.smartnotice.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@NoArgsConstructor
@AllArgsConstructor
public class MqttAnnounceJson {
    private String title;
    private String producer;
    private String type;
    private String status;
    private String data;
}
