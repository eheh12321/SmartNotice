package sejong.smartnotice.config;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.integration.annotation.ServiceActivator;
import org.springframework.integration.channel.DirectChannel;
import org.springframework.integration.core.MessageProducer;
import org.springframework.integration.mqtt.inbound.MqttPahoMessageDrivenChannelAdapter;
import org.springframework.integration.mqtt.support.DefaultPahoMessageConverter;
import org.springframework.messaging.Message;
import org.springframework.messaging.MessageChannel;
import org.springframework.messaging.MessageHandler;
import org.springframework.messaging.MessagingException;
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
                new MqttPahoMessageDrivenChannelAdapter("tcp://localhost:1883", "testClient", "topic1", "topic2", "topic3");
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
                }


            }
        };
    }

    @Bean
    public List<MqttInboundDTO> mqttInboundDTOList() {
        return new ArrayList<>();
    }
}
