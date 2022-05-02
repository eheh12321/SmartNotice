package sejong.smartnotice.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
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
import sejong.smartnotice.dto.MqttInboundDTO;
import sejong.smartnotice.dto.MqttInboundJson;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Slf4j
@Configuration
public class MqttConfig {

    @Bean
    public MessageChannel mqttInputChannel() {
        return new DirectChannel();
    }

    @Bean
    public MessageProducer inbound() {
        MqttPahoMessageDrivenChannelAdapter adapter =
                new MqttPahoMessageDrivenChannelAdapter("tcp://localhost:1883", "testClient", "test", "announce");
        adapter.setCompletionTimeout(5000);
        adapter.setConverter(new DefaultPahoMessageConverter());
        adapter.setQos(2);
        adapter.setOutputChannel(mqttInputChannel());
        return adapter;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttInputChannel")
    public MessageHandler handler() {
        return new MessageHandler() {

            @Override
            public void handleMessage(Message<?> message) throws MessagingException {
                log.info("Headers: {}, message: {}, receiveTime: {}",
                        message.getHeaders(), message.getPayload(), LocalDateTime.now());

                // JSON Parsing
                ObjectMapper objectMapper = new ObjectMapper();
                try {
                    MqttInboundJson json = objectMapper.readValue(message.getPayload().toString(), MqttInboundJson.class);
                    log.info("title: {}, content: {}", json.getTitle(), json.getContent());

                    MqttInboundDTO inboundDTO = new MqttInboundDTO(
                            message.getHeaders().get("mqtt_receivedTopic").toString(), message.getPayload().toString(),
                            json.getTitle(), json.getContent(), LocalDateTime.now());
                    mqttInboundDTOList().add(inboundDTO);

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
        options.setUserName("username");
        options.setPassword("password".toCharArray());
        return options;
    }

    @Bean
    @ServiceActivator(inputChannel = "mqttOutboundChannel")
    public MessageHandler mqttOutbound(DefaultMqttPahoClientFactory clientFactory) {
        MqttPahoMessageHandler messageHandler = new MqttPahoMessageHandler(MqttClient.generateClientId(), clientFactory);
        messageHandler.setAsync(true);
        messageHandler.setDefaultQos(2);
        return messageHandler;
    }

    @Bean
    public MessageChannel mqttOutboundChannel() {
        return new DirectChannel();
    }

    // MyGateway 인터페이스의 구현체를 런타임 시에 생성해라
    // 메소드 호출로 생성된 메시지가 defaultRequestChannel 채널으로 전송된다.
    @MessagingGateway(defaultRequestChannel = "mqttOutboundChannel")
    public interface MyGateway {
        void sendToMqtt(String data, @Header(MqttHeaders.TOPIC) String topic);
    }

    @Bean
    public List<MqttInboundDTO> mqttInboundDTOList() {
        return new ArrayList<>();
    }
}
