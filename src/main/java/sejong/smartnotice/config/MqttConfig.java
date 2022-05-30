package sejong.smartnotice.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.MessagingGateway;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.core.DefaultMqttPahoClientFactory;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.outbound.MqttPahoMessageHandler;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.integration.mqtt.support.MqttHeaders;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
import org.springframework.messaging.handler.annotation.Header;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.dto.MqttAlertJson;
import sejong.smartnotice.dto.MqttAnnounceJson;
import sejong.smartnotice.dto.MqttInboundDTO;
import sejong.smartnotice.dto.MqttInitJson;
import sejong.smartnotice.service.EmergencyAlertService;
import sejong.smartnotice.service.UserService;

import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MqttConfig {

    @Value("${mosquitto.clientId}")
    private String clientId;

    @Value("${mosquitto.password}")
    private String password;

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter("tcp://localhost:1883", defaultMqttPahoClientFactory(), "init", "announce", "emergency");
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(1);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    private final EmergencyAlertService alertService;
    private final UserService userService;

    /**
     * MQTT 수신 (Subscribe)
     * mosquitto_sub
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return new MessageHandler() {

            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                log.info("Headers: {}, message: {}", message.getHeaders(), message.getPayload());

                // 수신한 토픽
                String receivedTopic = message.getHeaders().get("mqtt_receivedTopic").toString();
                
                // 수신한 토픽에 맞는 JSON 파싱
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    switch (receivedTopic) {
                        case "announce": {
                            log.info("announce 토픽 처리");
                            MqttAnnounceJson json = objectMapper.readValue(message.getPayload().toString(), MqttAnnounceJson.class);

                            MqttInboundDTO inboundDTO = new MqttInboundDTO(receivedTopic, json.getProducer(), json.getTitle()
                                    , json.getTextData(), json.getVoiceData(), json.getType(), json.getStatus(), json.getAnnounceTime());
                            mqttInboundDTOList().add(inboundDTO);
                            break;
                        }
                        case "init": {
                            log.info("init 토픽 처리");
                            MqttInitJson json = objectMapper.readValue(message.getPayload().toString(), MqttInitJson.class);
                            log.info("client: {}, MAC: {}", json.getClient(), json.getMac());
                            break;
                        }
                        case "emergency": {
                            log.info("emergency 토픽 처리");
                            // JSON 파싱
                            MqttAlertJson json = objectMapper.readValue(message.getPayload().toString(), MqttAlertJson.class);
                            log.info("client: {}, emergency: {}", json.getClient(), json.getEmergency());

                            // 주민 조회 (주민 연락처 이용)
                            User user = userService.findByTel(json.getClient());

                            // 긴급 호출 생성
                            alertService.createAlert(user);
                            break;
                        }
                    }
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    log.error("JSON 파싱 실패!");
                }
            }
        };
    }

    @Bean
    public DefaultMqttPahoClientFactory defaultMqttPahoClientFactory() {
        DefaultMqttPahoClientFactory clientFactory = new DefaultMqttPahoClientFactory();
        clientFactory.setConnectionOptions(connectOptions());
        return clientFactory;
    }

    private MqttConnectOptions connectOptions() {
        MqttConnectOptions options = new MqttConnectOptions();
        options.setCleanSession(true);
        options.setServerURIs(new String[] {"tcp://localhost:1883"});
        options.setUserName(clientId);
        options.setPassword(password.toCharArray());
        return options;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound(DefaultMqttPahoClientFactory clientFactory) {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(clientId, clientFactory);
        messageHandler.setAsync(true);
        messageHandler.setDefaultQos(1);
        return messageHandler;
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    /**
     * MQTT 발신 (Publish)
     * mosquitto_pub
     */
    @MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
    public interface MyGateway {
        void sendToMqtt(String data, @Header(MqttHeaders.TOPIC) String topic);
    }

    @Bean
    public List<MqttInboundDTO> mqttInboundDTOList() {
        return new ArrayList<>();
    }
}
