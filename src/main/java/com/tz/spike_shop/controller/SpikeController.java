package com.tz.spike_shop.controller;


import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.tz.spike_shop.exception.GlobalException;
import com.tz.spike_shop.pojo.*;
import com.tz.spike_shop.rabbitmq.MqSender;
import com.tz.spike_shop.service.IGoodsService;
import com.tz.spike_shop.service.IOrderService;
import com.tz.spike_shop.service.ISpikeOrderService;
import com.tz.spike_shop.utils.JsonUtil;
import com.tz.spike_shop.vo.GoodsVo;
import com.tz.spike_shop.vo.ResponseResult;
import com.tz.spike_shop.vo.ResponseResultEnum;
import com.wf.captcha.ArithmeticCaptcha;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.data.redis.core.script.RedisScript;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.CollectionUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author tz
 * @since 2022-07-11
 */
@Controller
@RequestMapping("/spike")
@Slf4j
public class SpikeController implements InitializingBean {

    @Autowired
    IGoodsService goodsService;

    @Autowired
    ISpikeOrderService spikeOrderService;

    @Autowired
    IOrderService orderService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private MqSender mqSender;

    @Autowired
    private RedisScript<Long> redisScript;

    private Map<Long, Boolean> emptyStoreMap = new HashMap<>();


    /**
     * 拿到秒杀结果
     * @param user
     * @param goodsId
     * @return
     */
    @GetMapping("/getSpikeResult")
    @ResponseBody
    public ResponseResult getSpikeResult(User user, Long goodsId) {
        if (user == null) return ResponseResult.error(ResponseResultEnum.USER_NOT_LOGIN);

        Long orderId = spikeOrderService.getResult(user, goodsId);
        return ResponseResult.success(orderId);
    }


    @GetMapping("/getSpikePath")
    @ResponseBody
    public ResponseResult getSpikePath(User user, Long goodsId, String captcha) {
        if (user == null) return ResponseResult.error(ResponseResultEnum.USER_NOT_LOGIN);

        Boolean captchaValid = orderService.validCaptcha(user, goodsId, captcha);
        if (!captchaValid) {
            return ResponseResult.error(ResponseResultEnum.CAPTCHA_VALID_ERROR);
        }
        String path = orderService.createSpikePath(user, goodsId);
        return ResponseResult.success(path);
    }

    @GetMapping("/captcha")
    public void captcha(User user, Long goodsId, HttpServletResponse response) {
        if (user == null) throw new GlobalException("user not login", ResponseResultEnum.USER_NOT_LOGIN);

        response.setContentType("image/gif");
        response.setHeader("Pragma", "No-cache");
        response.setHeader("Cache-Control", "no-cache");
        response.setDateHeader("Expires", 0);

        ArithmeticCaptcha captcha = new ArithmeticCaptcha(130, 45, 3);
        // 存放验证码答案
        redisTemplate.opsForValue().set("captcha:" + user.getId() + ":" + goodsId, captcha.text(), 5, TimeUnit.MINUTES);

        try {
            captcha.out(response.getOutputStream());
        } catch (IOException e) {
            System.out.println("验证码生成失败: " + e.getMessage());
        }
    }

    /**
     * 核实库存数量，验证一个用户只能秒杀成功一次
     * 又优化前qbs : 1787
     * @param model
     * @param user
     * @param id
     * @return
     */
    @PostMapping("/doSpikeTest")
    public String doSpikeTest(Model model, User user, Long id) {
        if (user == null) return "login";
        model.addAttribute("user", user);

        // 没货了
        GoodsVo good = goodsService.findGoodById(id);
        if (good == null || good.getSpikeCount() <= 0) {
            model.addAttribute("message", ResponseResultEnum.EMPTY_STOCK.getMessage());
            return "spike_fail";
        }

        // 是否已经买过
        SpikeOrder spikeOrder = spikeOrderService.getOne(new QueryWrapper<SpikeOrder>().eq("user_id", user.getId()).eq("goods_id", id));
        if (spikeOrder != null) {
            model.addAttribute("message", ResponseResultEnum.REPEAT_SPIKE.getMessage());
            return "spike_fail";
        }

        Order order = orderService.spike(user, good);

        model.addAttribute("order", order);
        model.addAttribute("goods", good);
        return "orderDetail";
    }


    /**
     * 使用前后端分离形式优化
     * @param model
     * @param user
     * @param id
     * @return
     */
    @PostMapping("/doSpikeTest2")
    @ResponseBody
    public ResponseResult doSpikeTest2(Model model, User user, Long id) {
        if (user == null) return ResponseResult.error(ResponseResultEnum.USER_NOT_EXIST_ERROR);
        // 没货了
        GoodsVo good = goodsService.findGoodById(id);
        if (good == null || good.getSpikeCount() <= 0) {
            return ResponseResult.error(ResponseResultEnum.EMPTY_STOCK);
        }

        // 是否已经买过
        SpikeOrder spikeOrder = (SpikeOrder)redisTemplate.opsForValue().get("user_" + user.getId() + ":goods_" + id);
        if (spikeOrder != null) {
            return ResponseResult.error(ResponseResultEnum.REPEAT_SPIKE);
        }

        Order order = orderService.spike(user, good);
        return ResponseResult.success(order);
    }


    /**
     * 使用redis缓存库存，减少数据库访问
     * 使用rabbitMq 进行下单操作
     * 加 mq 实现异步后 qbs:3386
     * @param user
     * @param goodsId
     * @return
     */
    @PostMapping("/doSpike")
    @ResponseBody
    public ResponseResult doSpike(String path, User user, Long goodsId) {
        if (user == null) return ResponseResult.error(ResponseResultEnum.USER_NOT_LOGIN);

        ValueOperations valueOperations = redisTemplate.opsForValue();

        Boolean success = orderService.validSpikePath(user, goodsId, path);
        if (!success) {
            return ResponseResult.error(ResponseResultEnum.SPIKE_PATH_ERROR);
        }

        // 重复购买
        SpikeOrder spikeOrder = (SpikeOrder)valueOperations.get("user_" + user.getId() + ":goods_" + goodsId);
        if (spikeOrder != null) {
            return ResponseResult.error(ResponseResultEnum.REPEAT_SPIKE);
        }

        // 使用内存标记,减少redis的无用访问
        if (emptyStoreMap.getOrDefault(goodsId, true)) {
            return ResponseResult.error(ResponseResultEnum.EMPTY_STOCK);
        }

        // 单体项目使用此标注进行redis预减库存
        Long goodsNum = valueOperations.decrement("spikeGoods:" + goodsId);
        if (goodsNum < 0) {
            emptyStoreMap.put(goodsId, true);
            valueOperations.increment("spikeGoods:" + goodsId);
            return ResponseResult.error(ResponseResultEnum.EMPTY_STOCK);
        }

        // 分布式环境使用此标注进行redis预减库存
//        Long store = (Long) redisTemplate.execute(redisScript, Collections.singletonList("spikeGoods:" + goodsId));
//        if (store < 0) {
//            emptyStoreMap.put(goodsId, true);
//            return ResponseResult.error(ResponseResultEnum.EMPTY_STOCK);
//        }


        SpikeMessage message = new SpikeMessage(user, goodsId);
        mqSender.topicExchangeSender(JsonUtil.object2JsonStr(message), "spike.message");

        return ResponseResult.success(0L);
    }


    /**
     * 系统初始化执行方法, 在redis中初始化商品库存，避免大量访问数据库
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        List<GoodsVo> list = goodsService.findAllGoodsVo();
        if (CollectionUtils.isEmpty(list)) {
            return ;
        }
        list.forEach(goodsVo ->{
            redisTemplate.opsForValue().set("spikeGoods:" + goodsVo.getId(), goodsVo.getSpikeCount());
            emptyStoreMap.put(goodsVo.getId(), false);
        });
//        for (Long id : emptyStoreMap.keySet()) {
//            log.info("id : " + id + " isEmpty : " + emptyStoreMap.get(id));
//        }
    }
}
