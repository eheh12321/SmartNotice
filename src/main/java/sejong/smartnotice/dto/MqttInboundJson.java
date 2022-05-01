package sejong.smartnotice.dto;

import lombok.Getter;

@Getter
public class MqttInboundJson {
    private String title;
    private String content;
}
