package com.tz.spike_shop.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tz.spike_shop.annotation.AccessLimit;
import com.tz.spike_shop.pojo.User;
import com.tz.spike_shop.service.IUserService;
import com.tz.spike_shop.utils.CookieUtil;
import com.tz.spike_shop.vo.ResponseResult;
import com.tz.spike_shop.vo.ResponseResultEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.concurrent.TimeUnit;

/**
 * 配置拦截器，对接口做流量控制，用户登录控制（配合方法解析器）
 *
 * 使用ThreadLocal配置全局用户信息，单个线程内共享
 */
@Component
public class AccessLimitInterceptor implements HandlerInterceptor {

    @Autowired
    private IUserService userService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            User user = getUser(request, response);
            UserContext.setUser(user);
            HandlerMethod handlerMethod = (HandlerMethod)handler;
            AccessLimit accessLimit = handlerMethod.getMethodAnnotation(AccessLimit.class);
            if (accessLimit == null) return true;

            int seconds = accessLimit.seconds();
            int maxCount = accessLimit.maxCount();
            boolean needLogin = accessLimit.needLogin();
            String key = request.getRequestURI();

            if (needLogin) {
                if (user == null) {
                    render(response, ResponseResultEnum.USER_NOT_LOGIN);
                    // 拦截
                    return false;
                }
                key += ":" + user.getId();
            }
            Integer number = (Integer)redisTemplate.opsForValue().get(key);
            if (number == null) {
                redisTemplate.opsForValue().set(key, 1, seconds, TimeUnit.SECONDS);
            }
            else if (number < maxCount) {
                redisTemplate.opsForValue().increment(key);
            }
            else {
                render(response, ResponseResultEnum.ACCESS_ERROR);
                return false;
            }
        }
        return true;
    }


    /**
     * servlet 版返回json信息
     * @param response
     * @param responseResultEnum
     * @throws IOException
     */
    private void render(HttpServletResponse response, ResponseResultEnum responseResultEnum) throws IOException {
        response.setContentType("application/json");
        response.setCharacterEncoding("UTF-8");
        PrintWriter printWriter = response.getWriter();
        ResponseResult result = ResponseResult.error(responseResultEnum);
        printWriter.write(new ObjectMapper().writeValueAsString(result));
        printWriter.flush();
        printWriter.close();
    }


    public User getUser(HttpServletRequest request, HttpServletResponse response) {
        String cookie = CookieUtil.getCookieValue(request, "userCookie");
        if (StringUtils.isEmpty(cookie)) return null;

        return userService.getUserByCookie(cookie, request, response);
    }

    /**
     * 线程访问完毕后，清除用户数据
     * @param request
     * @param response
     * @param handler
     * @param ex
     * @throws Exception
     */
    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.remove();
    }
}
