package com.fastcampuse.springkafka2.clip2.consumer;

import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;

@Service
public class Clip2Consumer {

    @KafkaListener(id = "clip2-listener", topics = "clip2")
    public void listener(String message) {
        System.out.println("message= " + message);
    }


}
