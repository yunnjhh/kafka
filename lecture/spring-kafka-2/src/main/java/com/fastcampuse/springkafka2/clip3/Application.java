package com.fastcampuse.springkafka2.clip3;

import com.fastcampuse.springkafka2.clip3.model.Animal;
import com.fastcampuse.springkafka2.clip3.producer.ClipProducer;
import org.springframework.boot.ApplicationRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

@SpringBootApplication
public class Application {

    public static void main(String[] args) {
        SpringApplication.run(Application.class, args);
    }

    @Bean
    public ApplicationRunner runner(ClipProducer clipProducer) {
        return args -> {
            clipProducer.async("clip3-animal", new Animal("puppy", 15));
        };
    }

}
