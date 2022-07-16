package com.tz.spike_shop.config;

import org.springframework.amqp.core.*;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class RabbitMQConfig {

    /**
     * 广播模式
     * 通过交换机发给绑定的所有队列
     */
    private final String FANOUT_EXCHANGE = "fanout_exchange";

    /**
     * 路由模式
     */
    private final String DIRECT_EXCHANGE = "direct_exchange";

    /**
     * 通配符模式
     */
    private final String TOPIC_EXCHANGE = "topic_exchange";

    private final String ROUTING_KEY1 = "key_red";

    private final String ROUTING_KEY2 = "key_blue";

    private final String ROUTING_KEY3 = "key_yellow";

    private final String MQ_QUEUE01 = "mq_queue01";

    private final String MQ_QUEUE02 = "mq_queue02";

    private final String MQ_QUEUE03 = "mq_queue03";


//    @Bean
//    public Queue queue() {
//        return new Queue("mq", true);
//    }

    @Bean
    public Queue mq_queue01() {
        return new Queue(MQ_QUEUE01, true);
    }

    @Bean
    public Queue mq_queue02() {
        return new Queue(MQ_QUEUE02, true);
    }

    @Bean
    public Queue mq_queue03() {
        return new Queue(MQ_QUEUE03, true);
    }

//    /**
//     * 广播交换机
//     * @return
//     */
//    @Bean
//    public FanoutExchange fanoutExchange() {
//        return new FanoutExchange(FANOUT_EXCHANGE);
//    }
//
//    @Bean
//    public Binding binding01() {
//        return BindingBuilder.bind(mq_queue01()).to(fanoutExchange());
//    }
//
//    @Bean
//    public Binding binding02() {
//        return BindingBuilder.bind(mq_queue02()).to(fanoutExchange());
//    }


//    /**
//     * 路由模式及绑定(其绑定需要路由键), 一个队列可以绑定多个routing, 一个routing 也可以由多个队列绑定
//     * @return
//     */
//    @Bean
//    public DirectExchange directExchange() {
//        return new DirectExchange(DIRECT_EXCHANGE);
//    }
//
//    @Bean
//    public Binding binding03() {
//        return BindingBuilder.bind(mq_queue01()).to(directExchange()).with(ROUTING_KEY1);
//    }
//
//    @Bean
//    public Binding binding031() {
//        return BindingBuilder.bind(mq_queue01()).to(directExchange()).with(ROUTING_KEY3);
//    }
//
//    @Bean
//    public Binding binding04() {
//        return BindingBuilder.bind(mq_queue02()).to(directExchange()).with(ROUTING_KEY2);
//    }
//
//    @Bean
//    public Binding binding041() {
//        return BindingBuilder.bind(mq_queue02()).to(directExchange()).with(ROUTING_KEY3);
//    }


    /**
     * Topics 统配符模式, 当 routing_key越来越多时，会难以管理,此时使用通配符模式管理
     * 通配符为 x.x.x.x形式
     * 两个符号： * : 匹配一个x, #: 匹配所有
     */
    @Bean
    public TopicExchange topicExchange() {
        return new TopicExchange(TOPIC_EXCHANGE);
    }

//    @Bean
//    public Binding binding05() {
//        return BindingBuilder.bind(mq_queue01()).to(topicExchange()).with("spike.123.4");
//    }
//
//    @Bean
//    public Binding binding06() {
//        return BindingBuilder.bind(mq_queue02()).to(topicExchange()).with("spike.*");
//    }

    @Bean
    public Binding binding07() {
        return BindingBuilder.bind(mq_queue01()).to(topicExchange()).with("spike.#");
    }


}
