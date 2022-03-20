package com.fastcampus.springkafka.clip03.configuration;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ProducerFactory;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.requestreply.ReplyingKafkaTemplate;

@Configuration
public class ReplyingKafkaTemplateConfiguration {

    // key, value, replying(응답 객체 타입) 세 개를 지정.
    @Bean
    public ReplyingKafkaTemplate<String, String, String> replyingKafkaTemplate(ProducerFactory<String, String> producerFactory,
                                                                               ConcurrentMessageListenerContainer<String, String> repliesContainer) {
        // 생성자 안에 보면, 그 전에는(KafkaTemplate, ReplyingKafkaTemplate) ProducerFactory 만 필요했는데,
        // ReplyingKafkaTemplate 은 MessageListenerContainer 가 필요 (발행 후 읽는 과정이 있기 때문)
        return new ReplyingKafkaTemplate<>(producerFactory, repliesContainer);
    }

    // Factory 를 이용해서 만들어야함. ConcurrentKafkaListenerContainerFactory 은 spring boot 가 미리 만들어 놓는다.
    @Bean
    public ConcurrentMessageListenerContainer<String, String> repliesContainer(ConcurrentKafkaListenerContainerFactory<String, String> containerFactory) {
        // container 만들 때에는 topic 명만 넣어주면 됨
        ConcurrentMessageListenerContainer<String, String> container = containerFactory.createContainer("clip3-replies");
        container.getContainerProperties().setGroupId("clip3-replies-container-id");
        return container;
    }

}
