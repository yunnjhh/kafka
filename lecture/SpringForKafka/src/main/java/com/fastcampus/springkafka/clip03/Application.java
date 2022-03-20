package com.fastcampus.springkafka.clip03;

import com.fastcampus.springkafka.clip03.producer.ClipProducer;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

import java.nio.charset.StandardCharsets;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ApplicationRunner runnerKafkaTemplate(KafkaTemplate<String, String> kafkaTemplate) {
        return args -> {
            kafkaTemplate.send("clip3", "Goodnight, clip3");
        };
    }

    @Bean
    public ApplicationRunner runnerKafkaTemplateAsync(ClipProducer producer) {
        return args -> {
            producer.async("clip3", "async - test");
            producer.sync("clip3", "sync - test");
            producer.routingSend("clip3", "routingSend");
            producer.routingSend("clip3-bytes", "routingSend - bytes".getBytes(StandardCharsets.UTF_8));
            producer.replyingSend("clip3-request", "ping clip3, clip3-request");

//            Thread.sleep(1000L);
        };
    }

}
