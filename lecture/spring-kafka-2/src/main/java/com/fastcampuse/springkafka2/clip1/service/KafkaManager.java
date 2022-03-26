package com.fastcampuse.springkafka2.clip1.service;


import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AlterConfigOp;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.DescribeConfigsResult;
import org.apache.kafka.common.config.ConfigResource;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutionException;

@Service
public class KafkaManager {

    private final KafkaAdmin kafkaAdmin;
    private final AdminClient adminClient;

    public KafkaManager(KafkaAdmin kafkaAdmin) {
        this.kafkaAdmin = kafkaAdmin;
        // AdminClient 는 kafkaAdmin 의 property 를 이용하여 생
        this.adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties());
    }

    // 토픽 정보 조회
    public void describeTopicConfigs() throws ExecutionException, InterruptedException {
        Collection<ConfigResource> resources = List.of(
                // ConfigResource : ConfigResource.Type, name 이 필요
                // ConfigResource.Type 은 Enum 으로 정의가 되어있음. BROKER, TOPIC 을 가장 많이 사용
                new ConfigResource(ConfigResource.Type.TOPIC, "clip4-listener")
                // broker 정보는 ConfigResource.Type.BROKER, broker-id 입력하면 됨!
        );

        DescribeConfigsResult result = adminClient.describeConfigs(resources);
        System.out.println(result.all().get());
    }


}
