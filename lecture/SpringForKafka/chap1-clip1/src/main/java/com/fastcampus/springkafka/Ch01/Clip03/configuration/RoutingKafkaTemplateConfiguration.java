package com.fastcampus.springkafka.Ch01.Clip03.configuration;

import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.ByteArraySerializer;
import org.apache.kafka.common.serialization.StringSerializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.core.RoutingKafkaTemplate;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.regex.Pattern;

@Configuration
public class RoutingKafkaTemplateConfiguration {
// 다른 것과 차이점 : producer factory 가 두 개 이상 필요. 하나를 만들 경우에는 kafkaTemplate 과 거의 똑같이 동작

    @Bean
    public RoutingKafkaTemplate routingKafkaTemplate() {
        return new RoutingKafkaTemplate(getFatories());
    }

    // <Object, Object> type 으로 들어가는 이유 : routing 은 어떤 type 으로 직렬화가 될지 알 수 없기 때문
    private Map<Pattern, ProducerFactory<Object, Object>> getFatories() {
        Map<Pattern, ProducerFactory<Object, Object>> factories = new LinkedHashMap<>();
        // clip3-bytes : topic name -> clip3-bytes 라는 토픽 사용시 byteProducerFactory() 를 이용하게 됨
        factories.put(Pattern.compile("clip3-bytes"), byteProducerFactory());
        factories.put(Pattern.compile(".*"), defaultProducerFactory()); // Pattern.compile() 안에 regularExpression

        return factories;
    }

    private ProducerFactory<Object, Object> byteProducerFactory() {
        Map<String, Object> props = producerProps();
        // value 를 byte 로 받는다는 설정
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, ByteArraySerializer.class);

        return new DefaultKafkaProducerFactory<>(props);
    }

    private ProducerFactory<Object, Object> defaultProducerFactory() {
        return new DefaultKafkaProducerFactory<>(producerProps());
    }

    private Map<String, Object> producerProps() {
        Map<String, Object> props = new HashMap<>();

        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092"); // Kafka 주소
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, StringSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, StringSerializer.class);

        return props;
    }


}
