package sejong.smartnotice.dto;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.time.LocalDateTime;

@Data
@AllArgsConstructor
public class MqttInboundDTO {
    private String topic;

    private String producer;
    private String message; // 문자 내용
    private String data; // base64 데이터

    private String type; // 문자 / 음성
    private String status; // 일반 / 재난

    private LocalDateTime inboundDateTime;
}
