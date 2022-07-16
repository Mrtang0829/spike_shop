package com.tz.spike_shop.controller;


import com.tz.spike_shop.pojo.Order;
import com.tz.spike_shop.pojo.User;
import com.tz.spike_shop.service.IOrderService;
import com.tz.spike_shop.vo.ResponseResult;
import com.tz.spike_shop.vo.ResponseResultEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseBody;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author tz
 * @since 2022-07-11
 */
@Controller
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private IOrderService orderService;


    @GetMapping("/detail/{id}")
    @ResponseBody
    public ResponseResult getDetail(User user, @PathVariable("id") Long orderId) {
        if (user == null) return ResponseResult.error(ResponseResultEnum.USER_NOT_LOGIN);
        return orderService.findOrderById(orderId);
    }

}
