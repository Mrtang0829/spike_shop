package com.tz.spike_shop.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.conditions.update.UpdateWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.tz.spike_shop.exception.GlobalException;
import com.tz.spike_shop.mapper.UserMapper;
import com.tz.spike_shop.pojo.User;
import com.tz.spike_shop.service.IUserService;
import com.tz.spike_shop.utils.CookieUtil;
import com.tz.spike_shop.utils.MD5Util;
import com.tz.spike_shop.utils.MobileValidatorUtil;
import com.tz.spike_shop.utils.UUIDUtil;
import com.tz.spike_shop.vo.LoginVo;
import com.tz.spike_shop.vo.RegisterVo;
import com.tz.spike_shop.vo.ResponseResult;
import com.tz.spike_shop.vo.ResponseResultEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Date;
import java.util.concurrent.TimeUnit;

/**
 * <p>
 *  服务实现类
 * </p>
 *
 * @author tz
 * @since 2022-07-09
 */
@Service
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements IUserService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private RedisTemplate redisTemplate;

    /**
     * 登录逻辑
     * @param loginVo
     * @return
     */
    @Override
    public ResponseResult doLogin(LoginVo loginVo, HttpServletRequest request, HttpServletResponse response) {

        Long mobile = Long.parseLong(loginVo.getMobile());
        String password = loginVo.getPassword();

        // 通过@NotEmpty注解解决了
//        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)){
//            return ResponseResult.error(ResponseResultEnum.LOGIN_ERROR);
//        }
        // 通过@IsMobile注解解决
//        if (!MobileValidatorUtil.isMobile(mobile)){
//            return ResponseResult.error(ResponseResultEnum.MOBILE_ERROR);
//        }

        User user = userMapper.selectById(mobile);
        if (user == null){
//            return ResponseResult.error(ResponseResultEnum.LOGIN_ERROR);
            // 使用全局异常机制
            throw new GlobalException("UserServiceImpl,登录用户为空", ResponseResultEnum.LOGIN_ERROR);
        }

        // 验证密码是否正确
        if (!MD5Util.md5(password, user.getSlat()).equals(user.getPassword())){
//            return ResponseResult.error(ResponseResultEnum.LOGIN_ERROR);
            throw new GlobalException("UserServiceImpl,登录验证密码错误", ResponseResultEnum.LOGIN_ERROR);
        }

        user.setLastLoginDate(new Date());
        if (user.getLoginCount() == null) user.setLoginCount(1);
        else user.setLoginCount(user.getLoginCount() + 1);
        userMapper.updateById(user);

        /**
         * 设置cookie
         */
        String cookie = UUIDUtil.uuid();

        // 这里使用session存放用户信息，之后获取时也是通过session获取
//        request.getSession().setAttribute(ticket, user);

        // 分布式 session 后直接将用户存入redis中，之后从redis中获取即可
        redisTemplate.opsForValue().set("user:" + cookie, user, 24 * 60 * 60, TimeUnit.SECONDS);
        CookieUtil.setCookie(request, response, "userCookie", cookie);

        return ResponseResult.success(cookie);
    }

    /**
     *  注册逻辑
     * @param registerVo
     * @return
     */
    @Override
    public ResponseResult doRegister(RegisterVo registerVo) {
        Long mobile = Long.parseLong(registerVo.getMobile());
        String password = registerVo.getPassword();

//        if (StringUtils.isEmpty(mobile) || StringUtils.isEmpty(password)){
//            return ResponseResult.error(ResponseResultEnum.LOGIN_ERROR);
//        }

//        if (!MobileValidatorUtil.isMobile(mobile)){
//            log.error("mobile error" + mobile);
//            return ResponseResult.error(ResponseResultEnum.MOBILE_ERROR);
//        }

        User u = userMapper.selectById(mobile);
        if (u != null) {
//            return ResponseResult.error(ResponseResultEnum.EXIST_ERROR);
            throw new GlobalException("UserServiceImpl, 注册用户已存在", ResponseResultEnum.EXIST_ERROR);
        }

        String salt = MD5Util.randomSalt(10);
        String newPassword = MD5Util.md5(password, salt);

        User user = new User();
        user.setId(mobile);
        user.setPassword(newPassword);
        user.setRegisterDate(new Date());
        user.setSlat(salt);
        user.setUsername("hello");

        userMapper.insert(user);
        return ResponseResult.success();
    }

    /**
     * 通过 cookie 从redis中获取用户信息
     * @param cookie
     * @return
     */
    @Override
    public User getUserByCookie(String cookie, HttpServletRequest request, HttpServletResponse response) {
        if (StringUtils.isEmpty(cookie)) return null;
        User user = (User)redisTemplate.opsForValue().get("user:" + cookie);
        // 防止 cookie 失效
        if (user != null) {
            CookieUtil.setCookie(request, response, "userCookie", cookie);
        }
        return user;
    }

    @Override
    public ResponseResult updatePassword(String cookie, String password, HttpServletRequest request, HttpServletResponse response) {
        User user = getUserByCookie(cookie, request, response);
        if (user == null) {
            return ResponseResult.error(ResponseResultEnum.USER_NOT_EXIST_ERROR);
        }
        user.setPassword(MD5Util.md5(password, user.getSlat()));
        int res = userMapper.updateById(user);
        if (res == 1) {
            // 用户更新，删除之前存储的信息
            redisTemplate.delete("user:" + cookie);
            return ResponseResult.success();
        }
        return ResponseResult.error(ResponseResultEnum.UPDATE_ERROR);
    }

}
