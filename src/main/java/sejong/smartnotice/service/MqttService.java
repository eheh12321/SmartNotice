package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import sejong.smartnotice.config.MqttConfig;

@Slf4j
@Service
@RequiredArgsConstructor
public class MqttService {
    private final MqttConfig.MyGateway myGateway;

    public void sendToMqtt(String content, String topic) {
        myGateway.sendToMqtt(content, topic);
    }
}
