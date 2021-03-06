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
     * publish??? Subscribe cilentId??? ???????????? ???????????? publish ??? subscribe ????????? client??? ??????????????? ????????? ???????????? ???
     * ?????? clientId ?????? _pub, _sub??? ????????? ?????? ?????????. (username, password??? ?????????)
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
     * Inbound ??????
     * url: MQTT Broker ?????? (localhost)
     * Subscribe Topic + Qos ??????
     */
    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter(clientId+ "_sub", defaultMqttPahoClientFactory());
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        
        // Subscribe Topic ??? QOS ??????
        adapter.addTopic("init", 1);
        adapter.addTopic("sensor", 1);
        adapter.addTopic("emergency", 2);

        // Subscribe Topic ?????? ??? MessageHandler ?????? ??????
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }


    /**
     * MQTT ?????? (Subscribe)
     * mosquitto_sub
     */
    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return new MessageHandler() {

            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                log.info("Headers: {}, message: {}", message.getHeaders(), message.getPayload());

                // ????????? ??????
                String receivedTopic = message.getHeaders().get("mqtt_receivedTopic").toString();
                
                // ????????? ????????? ?????? JSON ??????
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    switch (receivedTopic) {
                        case "init": {
                            log.info("init ?????? ??????");
                            MqttInitJson json = objectMapper.readValue(message.getPayload().toString(), MqttInitJson.class);
                            log.info("client: {}, MAC: {}", json.getClient(), json.getMac());
                            break;
                        }
                        case "sensor": {
                            log.info("sensor ?????? ??????");
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
                            log.info("emergency ?????? ??????");
                            // JSON ??????
                            MqttAlertJson json = objectMapper.readValue(message.getPayload().toString(), MqttAlertJson.class);
                            log.info("client: {}, emergency: {}", json.getClient(), json.getEmergency());

                            // ?????? ?????? (?????? ????????? ??????)
                            User user = userService.findByTel(json.getClient());

                            // ?????? ?????? ??????
                            alertService.createAlert(user, AlertType.USER);
                            break;
                        }
                    }
                } catch (JsonProcessingException e) {
                    e.printStackTrace();
                    log.error("JSON ?????? ??????!");
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
     * Option ??????
     * URI: MQTT Broker ?????? (localhost)
     * UserName == ClientId
     * mosquitto_passwd ?????? ????????? id/pw??? ???????????? ?????? ??????
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
     * MQTT ?????? (Publish)
     * Data: String ????????? json ?????? ?????????
     * publish??? topic, qos ??????
     */
    @MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
    public interface MyGateway {
        void sendToMqtt(String data, @Header(MqttHeaders.TOPIC) String topic, @Header(MqttHeaders.QOS) int qos);
    }
}
