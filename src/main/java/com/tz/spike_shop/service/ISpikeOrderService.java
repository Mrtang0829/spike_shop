package com.tz.spike_shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tz.spike_shop.pojo.SpikeOrder;
import com.tz.spike_shop.pojo.User;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tz
 * @since 2022-07-11
 */
public interface ISpikeOrderService extends IService<SpikeOrder> {

    Long getResult(Long goodsId);
}
