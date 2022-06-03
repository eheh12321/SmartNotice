package sejong.smartnotice.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
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
import sejong.smartnotice.domain.AlertType;
import sejong.smartnotice.domain.device.Device;
import sejong.smartnotice.domain.member.User;
import sejong.smartnotice.dto.MqttAlertJson;
import sejong.smartnotice.dto.MqttInitJson;
import sejong.smartnotice.dto.MqttSensorJson;
import sejong.smartnotice.service.DeviceService;
import sejong.smartnotice.service.EmergencyAlertService;
import sejong.smartnotice.service.UserService;

@Slf4j
@Configuration
@RequiredArgsConstructor
public class MqttConfig {

    private final EmergencyAlertService alertService;
    private final UserService userService;
    private final DeviceService deviceService;

    /**
     * publish와 Subscribe cilentId가 동일하게 사용하면 publish 시 subscribe 중이던 client가 일시적으로 연결이 끊어지게 됨
     * 이에 clientId 뒤에 _pub, _sub를 별도로 붙여 구분함. (username, password는 동일함)
     */
    @Value("${mosquitto.clientId}")
    private String clientId;

    @Value("${mosquitto.password}")
    private String password;

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    /**
     * Inbound 설정
     * url: MQTT Broker 주소 (localhost)
     * Subscribe Topic + Qos 설정
     */
    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(clientId+ "_sub", defaultMqttPahoClientFactory());
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        
        // Subscribe Topic 및 QOS 설정
        adapter.addTopic("init", 1);
        adapter.addTopic("sensor", 1);
        adapter.addTopic("emergency", 2);

        // Subscribe Topic 수신 시 MessageHandler 통해 처리
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }


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
                        case "init": {
                            log.info("init 토픽 처리");
                            MqttInitJson json = objectMapper.readValue(message.getPayload().toString(), MqttInitJson.class);
                            log.info("client: {}, MAC: {}", json.getClient(), json.getMac());
                            break;
                        }
                        case "sensor": {
                            log.info("sensor 토픽 처리");
                            MqttSensorJson json = objectMapper
                                    .registerModule(new JavaTimeModule())
                                    .readValue(message.getPayload().toString(), MqttSensorJson.class);
                            log.info("client: {}, measureTime: {}", json.getClient(), json.getMeasureTime().toString());

                            User user = userService.findByTel(json.getClient());
                            Device device = user.getDevice();
                            deviceService.addSensorData(device.getId(), json);
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
                            alertService.createAlert(user, AlertType.USER);
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

    /**
     * Option 설정
     * URI: MQTT Broker 주소 (localhost)
     * UserName == ClientId
     * mosquitto_passwd 통해 설정한 id/pw와 일치해야 연결 가능
     */
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
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(clientId + "_pub", clientFactory);
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
     * Data: String 변환된 json 타입 데이터
     * publish할 topic, qos 설정
     */
    @MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
    public interface MyGateway {
        void sendToMqtt(String data, @Header(MqttHeaders.TOPIC) String topic, @Header(MqttHeaders.QOS) int qos);
    }
}
