package com.tz.spike_shop.rabbitmq;

import com.tz.spike_shop.pojo.SpikeMessage;
import com.tz.spike_shop.pojo.SpikeOrder;
import com.tz.spike_shop.pojo.User;
import com.tz.spike_shop.service.IGoodsService;
import com.tz.spike_shop.service.IOrderService;
import com.tz.spike_shop.utils.JsonUtil;
import com.tz.spike_shop.vo.GoodsVo;
import com.tz.spike_shop.vo.ResponseResult;
import com.tz.spike_shop.vo.ResponseResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Slf4j
public class MqReceiver {

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private IOrderService orderService;

    @RabbitListener(queues = "mq")
    public void listener(Object data) {
        log.info("接收消息" + data.toString());
    }

    /**
     * 接收topic——exchange消息
     * @param data
     */
    @Transactional
    @RabbitListener(queues = "mq_queue01")
    public void listener01(String data) {
        log.info("mq_queue01" + data);
        SpikeMessage message = JsonUtil.jsonStr2Object(data, SpikeMessage.class);
        Long goodsId = message.getGoodsId();
        User user = message.getUser();

        GoodsVo goodsVo = goodsService.findGoodById(goodsId);
        if (goodsVo.getSpikeCount() < 1) return;

        // 在此检查单个用户是否重复购买
        SpikeOrder spikeOrder = (SpikeOrder)redisTemplate.opsForValue().get("user_" + user.getId() + ":goods_" + goodsId);
        if (spikeOrder != null) return;

        orderService.spike(user, goodsVo);
    }

    @RabbitListener(queues = "mq_queue02")
    public void listener02(Object data) {
        log.info("mq_queue02" + data.toString());
    }

    @RabbitListener(queues = "mq_queue03")
    public void listener03(Object data) {
        log.info("mq_queue03" + data.toString());
    }
}
