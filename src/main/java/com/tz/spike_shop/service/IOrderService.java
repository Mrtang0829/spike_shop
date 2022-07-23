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

    Order spike(GoodsVo good);

    ResponseResult findOrderById(Long orderId);

    String createSpikePath(Long goodsId);

    Boolean validSpikePath(Long goodsId, String path);

    Boolean validCaptcha(Long goodsId, String captcha);
}
