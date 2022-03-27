package com.fastcampuse.springkafka2.clip1;

import com.fastcampuse.springkafka2.clip1.service.ClipConsumer;
import com.fastcampuse.springkafka2.clip1.service.KafkaManager;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.core.KafkaTemplate;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

//    @Bean
    public ApplicationRunner runner(KafkaManager kafkaManager) {
        return args -> {
            kafkaManager.describeTopicConfigs();
            kafkaManager.changeConfig();
            kafkaManager.deleteRecords();
            kafkaManager.findAllConsumerGroups();

            try {
                kafkaManager.deleteConsumerGroup();
            } catch (Exception e) {
                e.printStackTrace();
            }

            Thread.sleep(2000);
            System.out.println("------- after delete consumer group -----  ");
            kafkaManager.findAllConsumerGroups();
            kafkaManager.findAllOffsets();
        };
    }

    // Message 발행을 위해 KafkaTemplate 추가
    @Bean
    public ApplicationRunner runnerWithTemplate(KafkaManager kafkaManager,
                                                KafkaTemplate<String, String> kafkaTemplate,
                                                ClipConsumer clipConsumer) {
        return args -> {
            kafkaTemplate.send("clip1-listener", "최종");
            Thread.sleep(2000);
            clipConsumer.seek();
        };
    }
}
