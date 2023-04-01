package com.ithima.test;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.test.context.junit4.SpringRunner;

@SpringBootTest
@RunWith(SpringRunner.class)
public class SpringDataRedisTest {




    private RedisTemplate redisTemplate;


    @Test
    public void testString(){
        ValueOperations valueOperations = redisTemplate.opsForValue();
        valueOperations.set("city","beijing"); 
    }

}
