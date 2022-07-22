package com.tz.spike_shop.utils;

import com.tz.spike_shop.config.KryoRedisSerializer;
import com.tz.spike_shop.exception.GlobalException;
import com.tz.spike_shop.vo.ResponseResultEnum;
import org.omg.CORBA.INTERNAL;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.RedisSerializer;
import org.springframework.stereotype.Component;

@Component
public class RedisUtil {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    /**
     * 判断key是否存在
     *
     * @param key 键
     * @return true 存在 false不存在
     */
    public boolean hasKey(String key) {
        try {
            return redisTemplate.hasKey(key);
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }
    }

    /**
     * 普通缓存获取
     *
     * @param key 键
     * @return 值
     */
    public Object get(String key) {
        try {
            RedisSerializer serializer = redisTemplate.getValueSerializer();
            return redisTemplate.opsForValue().get(key);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException("redis get exception", ResponseResultEnum.REDIS_ERROR);
        }
    }


    /**
     * 普通缓存放入
     *
     * @param key   键
     * @param value 值
     * @return true成功 false失败
     */
    public boolean set(String key, Integer value) {
        try {
            redisTemplate.opsForValue().set(key, value);
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException("redis set exception", ResponseResultEnum.REDIS_ERROR);
        }
    }

    /**
     * 自增
     * @param key
     * @param dealt
     */
    public synchronized void incr(String key, int dealt) {
        try {
            Integer number = (Integer) get(key);
            if (number == null) return ;
            number += dealt;
            set(key, number);
        }
        catch (Exception e) {
            e.printStackTrace();
            throw new GlobalException("redis incr exception", ResponseResultEnum.REDIS_ERROR);
        }
    }

}
