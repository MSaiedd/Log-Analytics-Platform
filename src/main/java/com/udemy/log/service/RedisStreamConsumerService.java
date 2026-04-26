package com.udemy.log.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisStreamConsumerService {

    private final StringRedisTemplate stringRedisTemplate;

    @Value("${app.redis.stream.group}")
    private String groupName;

    public RedisStreamConsumerService(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    public void processMessage(String workerName, MapRecord<String, String, String> message) {
        try {
            System.out.println(">>> CONSUMER HIT by " + workerName);
            System.out.println(">>> RAW MESSAGE: " + message.getValue());

            String service = message.getValue().get("service");
            String level = message.getValue().get("level");
            String logMessage = message.getValue().get("message");
            String timestamp = message.getValue().get("timestamp");

            System.out.println("Processed -> service=" + service
                    + ", level=" + level
                    + ", message=" + logMessage
                    + ", timestamp=" + timestamp);

            stringRedisTemplate.opsForStream().acknowledge(groupName, message);

            System.out.println("Acknowledged by " + workerName + ": " + message.getId().getValue());

        } catch (Exception e) {
            System.out.println("Error in " + workerName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}