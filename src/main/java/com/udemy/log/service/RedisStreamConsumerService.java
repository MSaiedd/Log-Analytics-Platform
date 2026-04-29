package com.udemy.log.service;

import com.udemy.log.entity.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Service
public class RedisStreamConsumerService {

    private final StringRedisTemplate stringRedisTemplate;
    private final ElasticLogService elasticLogService;

    @Value("${app.redis.stream.group}")
    private String groupName;

    public RedisStreamConsumerService(StringRedisTemplate stringRedisTemplate,
                                      ElasticLogService elasticLogService) {
        this.stringRedisTemplate = stringRedisTemplate;
        this.elasticLogService = elasticLogService;
    }

    public void processMessage(String workerName, MapRecord<String, String, String> message) {
        try {
            System.out.println(">>> CONSUMER HIT by " + workerName);
            System.out.println(">>> RAW MESSAGE: " + message.getValue());

            String service = message.getValue().get("service");
            String level = message.getValue().get("level");
            String logMessage = message.getValue().get("message");
            String timestamp = message.getValue().get("timestamp");

            Log log = new Log();
            log.setId(message.getId().getValue());
            log.setService(service);
            log.setLevel(level);
            log.setMessage(logMessage);
            log.setTimestamp(elasticLogService.parseTimestamp(timestamp));

            elasticLogService.save(log);

            stringRedisTemplate.opsForStream().acknowledge(groupName, message);

            System.out.println("Saved to Elasticsearch and acknowledged by "
                    + workerName + ": " + message.getId().getValue());

        } catch (Exception e) {
            System.out.println("Error in " + workerName + ": " + e.getMessage());
            e.printStackTrace();
        }
    }
}