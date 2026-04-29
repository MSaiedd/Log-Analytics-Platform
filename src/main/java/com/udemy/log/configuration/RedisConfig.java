package com.udemy.log.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.stream.MapRecord;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.stream.StreamMessageListenerContainer;
import java.time.Duration;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Configuration
public class RedisConfig {

    @Value("${app.redis.stream.workers}")
    private int workerCount;

    //template of elements will be pushed to redis (logs : strings)
    @Bean
    public StringRedisTemplate stringRedisTemplate(RedisConnectionFactory connectionFactory) {
        return new StringRedisTemplate(connectionFactory);
    }

    //thread pool execute which is needed by the listener
    @Bean
    public Executor redisStreamExecutor() {
        return Executors.newFixedThreadPool(workerCount);
    }

    //polling optionss
    @Bean
    public StreamMessageListenerContainer<String, MapRecord<String, String, String>> streamContainer(
            RedisConnectionFactory connectionFactory,
            Executor redisStreamExecutor) {

        StreamMessageListenerContainer.StreamMessageListenerContainerOptions<String, MapRecord<String, String, String>> options =
                StreamMessageListenerContainer.StreamMessageListenerContainerOptions
                        .builder()
                        .pollTimeout(Duration.ofSeconds(1))
                        .executor(redisStreamExecutor)
                        .build();

        return StreamMessageListenerContainer.create(connectionFactory, options);
    }
}