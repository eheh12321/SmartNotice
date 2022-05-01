package sejong.smartnotice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MqttInboundDTO {
    private String topic;
    private String message;
    private String title;
    private String content;
    private LocalDateTime inboundDateTime;
}
