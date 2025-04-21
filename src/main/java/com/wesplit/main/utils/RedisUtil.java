package com.wesplit.main.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.wesplit.main.exceptions.TransactionFailedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
public class RedisUtil {
    private ObjectMapper objectMapper;
    private RedisTemplate redisTemplate;

    @Autowired
    RedisUtil(RedisTemplate redisTemplate,ObjectMapper objectMapper){
        this.redisTemplate=redisTemplate;
        this.objectMapper=objectMapper;
    }

    public void setValue(String key,Object o,Long ttl){
        try{
            if(o!=null){
                String jsonValue= objectMapper.writeValueAsString(o);
                redisTemplate.opsForValue().set(key,jsonValue,ttl, TimeUnit.SECONDS);
            }
        } catch (JsonProcessingException e) {
            log.error(e.getMessage());
            throw new TransactionFailedException("failed to save to redis");
        }

    }

    public <T> T getValue(String key,Class<T> T){
        try{
            String o=(String) redisTemplate.opsForValue().get(key);
            if(o!=null){
                return objectMapper.readValue(o,T);
            }
            return null;
        }
        catch (Exception e){
            throw new TransactionFailedException("failed to get value from redis");
        }
    }

    public <T> List<T> getListValue(String key, Class<T> T){
        try{
            String o=(String) redisTemplate.opsForValue().get(key);
            if(o!=null){
                return objectMapper.readValue(o, objectMapper.getTypeFactory().constructCollectionType(List.class,T));
            }
            else{
                return null;
            }
        }
        catch (Exception e){
            log.error(e.getMessage());
            throw new TransactionFailedException("failed to get value from redis");
        }
    }
}
