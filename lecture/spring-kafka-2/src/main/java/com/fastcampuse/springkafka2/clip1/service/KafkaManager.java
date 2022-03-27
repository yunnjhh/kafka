package com.fastcampuse.springkafka2.clip1.service;


import org.apache.kafka.clients.admin.AdminClient;
import org.apache.kafka.clients.admin.AlterConfigOp;
import org.apache.kafka.clients.admin.ConfigEntry;
import org.apache.kafka.clients.admin.ConsumerGroupListing;
import org.apache.kafka.clients.admin.DeleteRecordsResult;
import org.apache.kafka.clients.admin.DeletedRecords;
import org.apache.kafka.clients.admin.DescribeConfigsResult;
import org.apache.kafka.clients.admin.ListConsumerGroupsResult;
import org.apache.kafka.clients.admin.ListOffsetsResult;
import org.apache.kafka.clients.admin.OffsetSpec;
import org.apache.kafka.clients.admin.RecordsToDelete;
import org.apache.kafka.common.KafkaFuture;
import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.config.ConfigResource;
import org.apache.kafka.common.config.TopicConfig;
import org.springframework.kafka.core.KafkaAdmin;
import org.springframework.stereotype.Service;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ExecutionException;

@Service
public class KafkaManager {

    private final KafkaAdmin kafkaAdmin;
    private final AdminClient adminClient;

    public KafkaManager(KafkaAdmin kafkaAdmin) {
        this.kafkaAdmin = kafkaAdmin;
        this.adminClient = AdminClient.create(kafkaAdmin.getConfigurationProperties());
    }

    public void describeTopicConfigs() throws ExecutionException, InterruptedException {
        Collection<ConfigResource> resources = List.of(
                new ConfigResource(ConfigResource.Type.TOPIC, "clip4-listener")
        );

        DescribeConfigsResult result = adminClient.describeConfigs(resources);  // 1
        System.out.println(result.all().get());
    }

    // 토픽 정보 변경
    public void changeConfig() throws ExecutionException, InterruptedException {
        ConfigResource resource = new ConfigResource(ConfigResource.Type.TOPIC, "clip4-listener");
        Map<ConfigResource, Collection<AlterConfigOp>> ops = new HashMap<>();
        ops.put(resource, List.of(new AlterConfigOp(new ConfigEntry(TopicConfig.RETENTION_MS_CONFIG, "6000"), AlterConfigOp.OpType.SET)));
        ops.put(resource, List.of(new AlterConfigOp(new ConfigEntry(TopicConfig.RETENTION_MS_CONFIG, null), AlterConfigOp.OpType.DELETE)));

        adminClient.incrementalAlterConfigs(ops);   // 1
        describeTopicConfigs();
    }

    // 레코드 삭제
    public void deleteRecords() throws ExecutionException, InterruptedException {
        TopicPartition tp = new TopicPartition("clip4-listener-header", 0);
        Map<TopicPartition, RecordsToDelete> target = new HashMap<>();
        target.put(tp, RecordsToDelete.beforeOffset(5));

        DeleteRecordsResult deleteRecordsResult = adminClient.deleteRecords(target);
        Map<TopicPartition, KafkaFuture<DeletedRecords>> result = deleteRecordsResult.lowWatermarks();

        Set<Map.Entry<TopicPartition, KafkaFuture<DeletedRecords>>> entries = result.entrySet();
        for (Map.Entry<TopicPartition, KafkaFuture<DeletedRecords>> entry : entries) {
            System.out.println("topic= " + entry.getKey().topic() +
                    ", partition= " + entry.getKey().partition() +
                    ", " + entry.getValue().get().lowWatermark());
        }
    }

    // 컨슈머 그룹 조회
    public void findAllConsumerGroups() throws ExecutionException, InterruptedException {
        ListConsumerGroupsResult result = adminClient.listConsumerGroups();
        Collection<ConsumerGroupListing> groups = result.valid().get();

        for (ConsumerGroupListing group : groups) {
            System.out.println(group);
        }
    }

    public void deleteConsumerGroup() throws ExecutionException, InterruptedException {
        adminClient.deleteConsumerGroups(List.of("clip4-animal-listener-id", "clip4-listener-id")).all().get();
    }

    // offset 확인
    public void findAllOffsets() throws ExecutionException, InterruptedException {
        Map<TopicPartition, OffsetSpec> target = new HashMap<>();
        target.put(new TopicPartition("clip4-listener-header", 0), OffsetSpec.latest());

        ListOffsetsResult result = adminClient.listOffsets(target); // 1
        for (TopicPartition tp : target.keySet()) {
            System.out.println("topic= " + tp.topic() + ", partition= " + tp.partition() + ", offsets=" + result.partitionResult(tp).get());
        }
    }
}
