package com.tz.spike_shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tz.spike_shop.pojo.Order;
import com.tz.spike_shop.pojo.User;
import com.tz.spike_shop.vo.GoodsVo;
import com.tz.spike_shop.vo.ResponseResult;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tz
 * @since 2022-07-11
 */
public interface IOrderService extends IService<Order> {

    Order spike(User user, GoodsVo good);

    ResponseResult findOrderById(Long orderId);

    String createSpikePath(User user, Long goodsId);

    Boolean validSpikePath(User user, Long goodsId, String path);

    Boolean validCaptcha(User user, Long goodsId, String captcha);
}
