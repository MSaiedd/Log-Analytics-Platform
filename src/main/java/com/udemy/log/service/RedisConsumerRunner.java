package com.udemy.log.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.data.redis.connection.stream.Consumer;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.data.redis.connection.stream.StreamOffset;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import org.springframework.stereotype.Component;

@Component

public class RedisConsumerRunner implements ApplicationRunner {

    private final RedisStreamSetupService setupService;
    private final RedisStreamConsumerService consumerService;
    private final StreamMessageListenerContainer<String, MapRecord<String, String, String>> container;

    @Value("${app.redis.stream.key}")
    private String streamKey;

    @Value("${app.redis.stream.group}")
    private String groupName;

    @Value("${app.redis.stream.workers}")
    private int workerCount;

    public RedisConsumerRunner(RedisStreamSetupService setupService,
                               RedisStreamConsumerService consumerService,
                               StreamMessageListenerContainer<String, MapRecord<String, String, String>> container) {
        this.setupService = setupService;
        this.consumerService = consumerService;
        this.container = container;
    }

    @Override
    public void run(ApplicationArguments args) {
        System.out.println("Runner started");

        setupService.createConsumerGroupIfNotExists();
        System.out.println("Group setup done");

        for (int i = 1; i <= workerCount; i++) {
            String workerName = "worker-" + i;

            container.receive(
                    Consumer.from(groupName, workerName),
                    StreamOffset.create(streamKey, ReadOffset.lastConsumed()),
                    message -> consumerService.processMessage(workerName, message)
            );

            System.out.println("Registered " + workerName);
        }

        container.start();
        System.out.println("Container started");
    }
}