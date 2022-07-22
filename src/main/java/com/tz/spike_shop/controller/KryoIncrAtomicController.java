package com.tz.spike_shop.controller;

import com.tz.spike_shop.exception.GlobalException;
import com.tz.spike_shop.utils.RedisUtil;
import com.tz.spike_shop.vo.ResponseResult;
import com.tz.spike_shop.vo.ResponseResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

/**
 * 测试Kryo序列化操作
 */
@Controller
@RequestMapping("/kryo")
@Slf4j
public class KryoIncrAtomicController {

    @Autowired
    private RedisUtil redisUtil;

    @GetMapping("/hello")
    public String hello(Model model){
        log.info("hello");
        model.addAttribute("name", "ttt");
        return "hello";
    }

    @GetMapping("/set")
    @ResponseBody
    public ResponseResult setValue(String key, Integer val) {
        boolean res = redisUtil.set(key, val);
        return ResponseResult.success(res);
    }

    @GetMapping("/get")
    @ResponseBody
    public ResponseResult getValue(String key) {
        Object o = redisUtil.get(key);
        return ResponseResult.success(o);
    }

    /**
     * 使用 kryo 序列化后redis 中存二进制数据, redisTemplate.opsForValue().increment() 无法对二进制操作
     * 项目中需要进行自增操作（存储操作数）,序列化方式建议选用 GenericJackson2JsonRedisSerializer
     * 使用synchronized保证高并发下kryo序列化的自增操作数据一致性
     * 使用JMeter压测
     * @param key
     * @param dealt
     * @return
     */
    @PostMapping("/incr")
    @ResponseBody
    public ResponseResult incr(String key, Integer dealt) {
        log.info("incr step++");
        if (StringUtils.isEmpty(key) || dealt == null) {
            throw new GlobalException("incr null", ResponseResultEnum.REDIS_ERROR);
        }
        redisUtil.incr(key, dealt);
        Object o = redisUtil.get(key);
        return ResponseResult.success(o);
    }
}
