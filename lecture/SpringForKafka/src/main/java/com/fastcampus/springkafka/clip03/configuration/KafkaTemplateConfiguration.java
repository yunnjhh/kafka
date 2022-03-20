package com.fastcampus.springkafka.clip03.configuration;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.core.ProducerFactory;

import java.util.HashMap;
import java.util.Map;

@Configuration
public class KafkaTemplateConfiguration {

    @Bean
    public KafkaTemplate<String, String> kafkaTemplate() {
        // KafkaTemplate은 KafkaTemplate 객체만 만들어주면 바로 생성. ProducerFactory 가 필요함
        return new KafkaTemplate<>(producerFactory());
    }

    private ProducerFactory<String, String> producerFactory() {
        return new DefaultKafkaProducerFactory<>(producerProps());
    }

    private Map<String, Object> producerProps() {
        // 설정할 부분들은 ProducerConfig 에 모두 정의가 되어있어서 활용하면 편리함
        Map<String, Object> props = new HashMap<>();
        // server (kafka cluster), key serializer, value serializer 3가지 설정
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Kafka 주소
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return props;
    }

}
