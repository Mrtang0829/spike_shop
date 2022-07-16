package com.tz.spike_shop.controller;


import com.tz.spike_shop.exception.GlobalException;
import com.tz.spike_shop.pojo.User;
import com.tz.spike_shop.rabbitmq.MqSender;
import com.tz.spike_shop.service.IUserService;
import com.tz.spike_shop.vo.LoginVo;
import com.tz.spike_shop.vo.RegisterVo;
import com.tz.spike_shop.vo.ResponseResult;
import com.tz.spike_shop.vo.ResponseResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

/**
 * <p>
 *  前端控制器
 * </p>
 *
 * @author tz
 * @since 2022-07-09
 */
@Controller
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private IUserService userService;

    @Autowired
    private MqSender sender;

    @GetMapping("/toLogin")
    public String toLogin(){
        return "login";
    }

    @PostMapping("/doLogin")
    @ResponseBody
    public ResponseResult doLogin(@Valid LoginVo loginVo, HttpServletRequest request, HttpServletResponse response){
        log.info(loginVo.toString());
        return userService.doLogin(loginVo, request, response);
    }

    @GetMapping("/toRegister")
    public String toRegister(){
        return "register";
    }

    @PostMapping("/doRegister")
    @ResponseBody
    public ResponseResult doRegister(@Valid RegisterVo registerVo){
        log.info(registerVo.toString());
        return userService.doRegister(registerVo);
    }


    @PostMapping("/updatePassword")
    public ResponseResult updatePassword(String cookie, String password, HttpServletRequest request, HttpServletResponse response) {
        return userService.updatePassword(cookie, password, request, response);
    }


    @GetMapping("/mq")
    @ResponseBody
    public String mq() {
        sender.sender("hello");
        return "success";
    }


    @GetMapping("/fanout")
    @ResponseBody
    public ResponseResult fanout() {
        sender.sender01("hello");
        return ResponseResult.success();
    }

    @GetMapping("/direct")
    @ResponseBody
    public ResponseResult direct(String key) {
        if (StringUtils.isEmpty(key)) throw new GlobalException("UserController direct, key null", ResponseResultEnum.ERROR);
        sender.sender02("hello", key);
        return ResponseResult.success();
    }

    @GetMapping("/topic")
    @ResponseBody
    public ResponseResult topic(String key) {
        if (StringUtils.isEmpty(key)) throw new GlobalException("UserController topic, key null", ResponseResultEnum.ERROR);
        sender.topicExchangeSender("hello", key);
        return ResponseResult.success();
    }
}
