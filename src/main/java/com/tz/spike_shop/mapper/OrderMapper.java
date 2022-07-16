package com.tz.spike_shop.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.tz.spike_shop.pojo.Order;
import com.tz.spike_shop.vo.OrderVo;

/**
 * <p>
 *  Mapper 接口
 * </p>
 *
 * @author tz
 * @since 2022-07-11
 */
public interface OrderMapper extends BaseMapper<Order> {

    OrderVo findOrderById(Long orderId);
}
