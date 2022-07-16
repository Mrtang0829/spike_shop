package com.tz.spike_shop;

import com.tz.spike_shop.utils.UUIDUtil;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;

import java.util.Collection;
import java.util.Collections;
import java.util.concurrent.TimeUnit;

@SpringBootTest
class SpikeShopApplicationTests {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private RedisScript redisScript;

    @Test
    void contextLoads() {
    }

    @Test
    public void test01() {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String uuid = UUIDUtil.uuid();
        Boolean isLock = valueOperations.setIfAbsent("k1", uuid, 120, TimeUnit.SECONDS);
        if (isLock) {
            valueOperations.set("name", "tz");
            String name = (String) valueOperations.get("name");
            System.out.println("name : " + name + " uuid : " + valueOperations.get("k1"));
            Boolean res = (Boolean) redisTemplate.execute(redisScript, Collections.singletonList("k1"), uuid);
            System.out.println(res);
        }
        else {
            System.out.println("try last time");
        }

    }

}
