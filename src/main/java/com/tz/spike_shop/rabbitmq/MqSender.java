package com.tz.spike_shop.rabbitmq;

import lombok.extern.slf4j.Slf4j;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
@Slf4j
public class MqSender {


    @Autowired
    private RabbitTemplate rabbitTemplate;


    public void sender(Object data) {
        log.info(data.toString());
        rabbitTemplate.convertAndSend("mq", data);
    }

    /**
     * 广播模式无需路由键(routingKey) 设为空字符串即可
     * @param data
     */
    public void sender01(Object data) {
        log.info(data.toString());
        rabbitTemplate.convertAndSend("fanout_exchange", "", data);
    }

    /**
     * 路由模式发送消息
     * @param data
     */
    public void sender02(Object data, String key) {
        log.info(data.toString());
        rabbitTemplate.convertAndSend("direct_exchange", key, data);
    }

    // topic_exchange 通配符模式
    public void topicExchangeSender(String data, String key) {
        log.info("topic info : " + data);
        rabbitTemplate.convertAndSend("topic_exchange", key, data);
    }
}
