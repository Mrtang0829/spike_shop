package com.tz.spike_shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tz.spike_shop.config.UserContext;
import com.tz.spike_shop.exception.GlobalException;
import com.tz.spike_shop.mapper.OrderMapper;
import com.tz.spike_shop.mapper.SpikeOrderMapper;
import com.tz.spike_shop.pojo.*;
import com.tz.spike_shop.service.IGoodsService;
import com.tz.spike_shop.service.IOrderService;
import com.tz.spike_shop.service.ISpikeGoodsService;
import com.tz.spike_shop.service.ISpikeOrderService;
import com.tz.spike_shop.utils.AsyncUtil;
import com.tz.spike_shop.utils.MD5Util;
import com.tz.spike_shop.utils.UUIDUtil;
import com.tz.spike_shop.vo.GoodsVo;
import com.tz.spike_shop.vo.OrderVo;
import com.tz.spike_shop.vo.ResponseResult;
import com.tz.spike_shop.vo.ResponseResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tz
 * @since 2022-07-11
 */
@Service
@Slf4j
public class OrderServiceImpl extends ServiceImpl<OrderMapper, Order> implements IOrderService {

    @Autowired
    IGoodsService goodsService;

    @Autowired
    ISpikeGoodsService spikeGoodsService;

    @Autowired
    OrderMapper orderMapper;

    @Autowired
    SpikeOrderMapper spikeOrderMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private AsyncUtil asyncUtil;

    @Transactional
    @Override
    public Order spike(GoodsVo good) {
        User user = UserContext.getUser();
        /**
         * 修改秒杀库存
         */
        SpikeGoods spikeGoods = spikeGoodsService.getOne(new QueryWrapper<SpikeGoods>().eq("goods_id", good.getId()));
        if (spikeGoods.getStoreCount() < 1) {
            redisTemplate.opsForValue().set("isStoreEmpty:" + good.getId(), 1);
            return null;
        }

        // 注意这里new UpdateWrapper<SpikeGoods>().set("store_count", goods.getStoreCount() - 1)会出错
        // 大并发环境下无法确保获取的goods是否已经被其他的请求修改了，所以这里用sql语句
        // 此方法仍不满足高并发
        boolean spike = spikeGoodsService.update(new UpdateWrapper<SpikeGoods>().setSql("store_count=store_count-1").
                eq("id", spikeGoods.getId()).gt("store_count", 0));
        if (!spike) return null;

        // 修改真实商品库存
        Goods real_good = goodsService.getById(good.getId());
        // 并发情况下这种更新方法会导致数据不一致
//        real_good.setGoodsStore(real_good.getGoodsStore() - 1);
//        goodsService.updateById(real_good);
        goodsService.update(new UpdateWrapper<Goods>().setSql("goods_store=goods_store-1").eq("id", real_good.getId()));


        /**
         * 生成订单
         */
        Order order = new Order();
        order.setUserId(user.getId());
        order.setGoodsId(good.getId());
        order.setGoodsName(good.getGoodsName());
        order.setGoodsCount(1);
        order.setGoodsPrice(good.getSpikePrice());
        order.setOrderChannel(1);
        order.setStatus(0);
        order.setCreateDate(new Date());
        orderMapper.insert(order);

        /**
         * 生成秒杀订单
         */
        SpikeOrder spikeOrder = new SpikeOrder();
        spikeOrder.setUserId(user.getId());
        spikeOrder.setOrderId(order.getId());
        spikeOrder.setGoodsId(good.getId());
        spikeOrderMapper.insert(spikeOrder);


//        Long diff = (spikeGoods.getEndDate().getTime() - new Date().getTime()) / 1000;
//        Long time = diff < 0 ? 0 :  diff;
        // 标记已购买
        redisTemplate.opsForValue().set("user_" + user.getId() + ":goods_" + good.getId(), spikeOrder, 60, TimeUnit.SECONDS);
        asyncUtil.sendInfo(user.getId(), new SpikeResultMessage(order.getId()));
        return order;
    }

    @Override
    public ResponseResult findOrderById(Long orderId) {
        OrderVo orderVo = orderMapper.findOrderById(orderId);

        if (orderVo == null) {
            return ResponseResult.error(ResponseResultEnum.ORDER_ERROR);
        }
        log.info(orderVo.toString());
        return ResponseResult.success(orderVo);
    }

    @Override
    public String createSpikePath(Long goodsId) {
        User user = UserContext.getUser();
        String path = MD5Util.md5(UUIDUtil.uuid());
        redisTemplate.opsForValue().set("spikePath:" + user.getId() + ":" + goodsId, path, 60, TimeUnit.SECONDS);
        return path;
    }


    @Override
    public Boolean validSpikePath(Long goodsId, String path) {
        User user = UserContext.getUser();
        if (user == null || StringUtils.isEmpty(path)) {
            return false;
        }

        String p = (String) redisTemplate.opsForValue().get("spikePath:" + user.getId() + ":" + goodsId);

        return path.equals(p);
    }

    @Override
    public Boolean validCaptcha(Long goodsId, String captcha) {
        User user = UserContext.getUser();
        if (user == null || StringUtils.isEmpty(captcha)) {
            return false;
        }
        String captchaValid = (String) redisTemplate.opsForValue().get("captcha:" + user.getId() + ":" + goodsId);
        return captcha.equals(captchaValid);
    }
}
