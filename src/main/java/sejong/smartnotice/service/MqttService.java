package sejong.smartnotice.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.integration.core.MessageProducer;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageHandler;
import org.springframework.stereotype.Service;
import sejong.smartnotice.config.MqttConfig;

@Slf4j
@Service
@RequiredArgsConstructor
public class MqttService {

    private final MqttConfig.MyGateway myGateway;
    private final MessageHandler handler;
    private final MessageProducer producer;


    public void sendToMqtt(String content, String topic) {
        myGateway.sendToMqtt(content, topic);
    }
}
