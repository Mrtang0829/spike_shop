package com.tz.spike_shop.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.tz.spike_shop.pojo.User;
import com.tz.spike_shop.vo.LoginVo;
import com.tz.spike_shop.vo.RegisterVo;
import com.tz.spike_shop.vo.ResponseResult;
import org.springframework.web.bind.annotation.ResponseBody;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * <p>
 *  服务类
 * </p>
 *
 * @author tz
 * @since 2022-07-09
 */
public interface IUserService extends IService<User> {

    ResponseResult doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response);

    ResponseResult doRegister(RegisterVo registerVo);

    User getUserByCookie(String cookie, HttpServletRequest request, HttpServletResponse response);

    ResponseResult updatePassword(String cookie, String password, HttpServletRequest request, HttpServletResponse response);
}
