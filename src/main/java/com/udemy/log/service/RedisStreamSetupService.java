package com.udemy.log.service;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.NestedExceptionUtils;
import org.springframework.data.redis.RedisSystemException;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.ReadOffset;
import org.springframework.stereotype.Service;


//create consumer group if not exists to handle the logs
@Service
public class RedisStreamSetupService {

    private final RedisConnectionFactory connectionFactory;

    @Value("${app.redis.stream.key}")
    private String streamKey;

    @Value("${app.redis.stream.group}")
    private String groupName;

    public RedisStreamSetupService(RedisConnectionFactory connectionFactory) {
        this.connectionFactory = connectionFactory;
    }

    public void createConsumerGroupIfNotExists() {
        try (RedisConnection connection = connectionFactory.getConnection()) {

            connection.streamCommands().xGroupCreate(
                    streamKey.getBytes(),
                    groupName,
                    ReadOffset.from("0-0"),
                    true
            );

            System.out.println("Consumer group created successfully");

        } catch (RedisSystemException e) {

            Throwable rootCause = NestedExceptionUtils.getRootCause(e);
            String rootMessage = rootCause != null ? rootCause.getMessage() : e.getMessage();

            if (rootMessage != null && rootMessage.contains("BUSYGROUP")) {
                System.out.println("Consumer group already exists, continuing...");
                return;
            }

            throw e;
        }
    }
}
