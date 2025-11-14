package net.engineeringdigest.journalApp.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import net.engineeringdigest.journalApp.api.response.WeatherResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
public class RedisService {
    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisConnectionFactory factory;

    @PostConstruct
    public void testConnection() {
        System.out.println("✅ Redis Connection Test: " + factory.getConnection().ping());
    }

    public <T> T getData(String key, Class<T> entityClass)  {
        try {
            Object value = redisTemplate.opsForValue().get(key);
            ObjectMapper objectMapper = new ObjectMapper();
            return objectMapper.readValue(value.toString(), entityClass);
        }
        catch (Exception e) {
            log.error("Exception ", e);
            return null;
        }
    }

    public void setData(String key, Object value, Long ttl)  {
        try {
            ObjectMapper objectMapper = new ObjectMapper();
            String jsonValue = objectMapper.writeValueAsString(value);
            redisTemplate.opsForValue().set(key, jsonValue, ttl, TimeUnit.SECONDS);
        }
        catch (Exception e) {
            log.error("Exception ", e);
        }
    }

}
