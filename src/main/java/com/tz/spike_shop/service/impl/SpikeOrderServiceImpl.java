package com.tz.spike_shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tz.spike_shop.mapper.SpikeOrderMapper;
import com.tz.spike_shop.pojo.SpikeOrder;
import com.tz.spike_shop.pojo.User;
import com.tz.spike_shop.service.ISpikeOrderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tz
 * @since 2022-07-11
 */
@Service
public class SpikeOrderServiceImpl extends ServiceImpl<SpikeOrderMapper, SpikeOrder> implements ISpikeOrderService {

    @Autowired
    private SpikeOrderMapper spikeOrderMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public Long getResult(User user, Long goodsId) {
        SpikeOrder spikeOrder = spikeOrderMapper.selectOne(new QueryWrapper<SpikeOrder>().
                eq("user_id", user.getId()).eq("goods_id", goodsId));
        if (spikeOrder != null) return spikeOrder.getOrderId();
        else if (redisTemplate.hasKey("isStoreEmpty:" + goodsId)) return -1L;
        else return 0L;
    }
}
