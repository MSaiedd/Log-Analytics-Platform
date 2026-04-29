package com.udemy.log.service;

import com.udemy.log.entity.Log;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.connection.stream.RecordId;
import org.springframework.data.redis.connection.stream.StreamRecords;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class LogServiceImpl implements LogService {

    private final StringRedisTemplate stringRedisTemplate;

    @Value("${app.redis.stream.key}")
    private String streamKey;

    public LogServiceImpl(StringRedisTemplate stringRedisTemplate) {
        this.stringRedisTemplate = stringRedisTemplate;
    }

    @Override
    public boolean fetchLog(Log log) {
        if (publish(log)) {
            System.out.println("LOG SENT TO REDIS SUCCESSFULLY");
            return true;
        }

        System.out.println("LOG HAS NOT BEEN SENT TO REDIS SUCCESSFULLY");
        return false;
    }

    private boolean publish(Log log) {
        Map<String, String> map = new HashMap<>();
        map.put("service", log.getService());
        map.put("level", log.getLevel());
        map.put("message", log.getMessage());
        map.put("timestamp", log.getTimestamp().toString());
        RecordId recordId = stringRedisTemplate.opsForStream()
                .add(StreamRecords.mapBacked(map).withStreamKey(streamKey));

        return recordId != null;
    }
}