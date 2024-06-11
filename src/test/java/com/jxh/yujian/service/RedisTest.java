package com.jxh.yujian.service;

import com.jxh.yujian.model.domain.User;
import jakarta.annotation.Resource;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;

@SpringBootTest
public class RedisTest {

    @Resource
    private RedisTemplate redisTemplate;


    @Test
    void test() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        // 增
        valueOperations.set("superString", "super");
        valueOperations.set("superInt", 1);
        valueOperations.set("superDouble", 2.0);
        User user = new User();
        user.setId(1L);
        user.setUsername("super");
        valueOperations.set("superUser", user);

        // 查
        Object super1 = valueOperations.get("superString");
        Assertions.assertTrue("super".equals(super1));
        super1 = valueOperations.get("superInt");
        Assertions.assertTrue(1 == (Integer) super1);
        super1 = valueOperations.get("superDouble");
        Assertions.assertTrue(2.0 == (Double) super1);
        System.out.println(valueOperations.get("superUser"));
        valueOperations.set("superString", "super");

        //删
//        redisTemplate.delete("superString");
    }

}
