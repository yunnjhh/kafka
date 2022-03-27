package com.fastcampuse.springkafka2.clip1.service;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.listener.AbstractConsumerSeekAware;
import org.springframework.stereotype.Service;

@Service
public class ClipConsumer extends AbstractConsumerSeekAware {

    @KafkaListener(id = "clip1-listener-id", topics = "clip1-listener")
    public void listen(String message) {
        System.out.println("message= " + message);
    }

    public void seek() {
        System.out.println("seek()");
        getSeekCallbacks().forEach(((topicPartition, consumerSeekCallback) ->
                consumerSeekCallback.seek(topicPartition.topic(), topicPartition.partition(), 0)));

    }
}
