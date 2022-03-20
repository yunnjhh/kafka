package com.fastcampus.springkafka.clip04;

import com.fastcampus.springkafka.clip04.model.Animal;
import com.fastcampus.springkafka.clip04.producer.ClipProducer;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.kafka.listener.KafkaMessageListenerContainer;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

//    @Bean
    public ApplicationRunner runnerMessageListenerContainer(ClipProducer clipProducer,
                                    KafkaMessageListenerContainer<String, String> kafkaMessageListenerContainer) {
        return args -> {
            clipProducer.async("clip4","Hello, Clip4 Container.");

            kafkaMessageListenerContainer.start();
            Thread.sleep(1_000L);

            System.out.println("-- 일시정지  --");
            kafkaMessageListenerContainer.pause();
            Thread.sleep(5_000L);

            clipProducer.async("clip4","Hello, Secondly Clip4 Container.");

            System.out.println("-- 재시작 --");
            kafkaMessageListenerContainer.resume();
            Thread.sleep(5_000L);

            System.out.println("-- 중단 --");
            kafkaMessageListenerContainer.stop();

        };
    }



    @Bean
    public ApplicationRunner runner(ClipProducer clipProducer) {
        return args -> {
//            clipProducer.async("clip4-listener-header", "Hello, Clip4 Listener Header 2 ");
            clipProducer.async("clip4-animal", new Animal("puppy", 11));
        };
    }

}
