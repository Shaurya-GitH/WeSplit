package com.wesplit.main.services;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.ActiveProfiles;

@ActiveProfiles("dev")
@SpringBootTest
public class RedisTests {
    @Autowired
    private RedisTemplate redisTemplate;
    @Test
    void test(){
        redisTemplate.opsForValue().set("salary","10k");
        Object name= redisTemplate.opsForValue().get("salary");
        int a=9;
    }
}
