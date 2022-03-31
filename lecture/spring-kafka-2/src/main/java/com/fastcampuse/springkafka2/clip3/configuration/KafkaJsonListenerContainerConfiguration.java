package com.fastcampuse.springkafka2.clip3.configuration;

import com.fastcampuse.springkafka2.clip3.model.Animal;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.KafkaListenerConfigurer;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistrar;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConcurrentMessageListenerContainer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.kafka.listener.SeekToCurrentErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.retry.RetryPolicy;
import org.springframework.retry.policy.SimpleRetryPolicy;
import org.springframework.retry.support.RetryTemplate;
import org.springframework.retry.support.RetryTemplateBuilder;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

@Configuration
public class KafkaJsonListenerContainerConfiguration implements KafkaListenerConfigurer {

    private final LocalValidatorFactoryBean validator;

    public KafkaJsonListenerContainerConfiguration(LocalValidatorFactoryBean validator) {
        this.validator = validator;
    }

    // Error Handler 를 이용하여, DeadLetter topic 에 넣고, Consume
    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Animal>> kafkaJsonContainerFactory(
            KafkaTemplate<String, Animal> kafkaJsonTemplate
    ) {
        ConcurrentKafkaListenerContainerFactory<String, Animal> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(animalConsumerFactory());
        factory.setErrorHandler(new SeekToCurrentErrorHandler(new DeadLetterPublishingRecoverer(kafkaJsonTemplate)));

        return factory;
    }

    @Bean
    public KafkaListenerContainerFactory<ConcurrentMessageListenerContainer<String, Animal>> kafkaJsonContainerFactory2() {
        ConcurrentKafkaListenerContainerFactory<String, Animal> factory = new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(animalConsumerFactory());
        factory.setRetryTemplate(customizedRetryTemplate());

        factory.setRecoveryCallback(context -> {
//            context.attributeNames();

            ConsumerRecord record = (ConsumerRecord)context.getAttribute("record");
            System.out.println("Recovery Callback. message= "+record.value());

            // Error Handler 를 위해 일부러 에러 발생시킴
            throw new RuntimeException("Runtime Exception");

//            return Optional.empty();
        });

        factory.setErrorHandler((thrownException, data) -> System.out.println("Error handler. exception= " + thrownException.getMessage()));

       return factory;
    }

    private RetryTemplate customizedRetryTemplate() {
        return new RetryTemplateBuilder()
                .fixedBackoff(1_000) // interval
                .customPolicy(retryPolicy())
                .build();
    }

    private RetryPolicy retryPolicy() {                                         // 4
        Map<Class<? extends Throwable>, Boolean> exceptions = new HashMap<>();  // 6
        exceptions.put(ListenerExecutionFailedException.class, true);           // true : 재시도 O, false : 재시도 X

        return new SimpleRetryPolicy(3, exceptions);                // 5
    }

    private ConsumerFactory<String, Animal> animalConsumerFactory() {
        return new DefaultKafkaConsumerFactory<>(
                props(),
                new StringDeserializer(),
                new JsonDeserializer<>(Animal.class));
    }

    private Map<String, Object> props() {
        Map<String, Object> props = new HashMap<>();
        props.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, "localhost:9092");
        props.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        props.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);

        return props;
    }

    @Override
    public void configureKafkaListeners(KafkaListenerEndpointRegistrar registrar) {
        registrar.setValidator(validator);
    }
}
