package com.tz.spike_shop.config;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.tz.spike_shop.pojo.User;
import com.tz.spike_shop.service.IUserService;
import com.tz.spike_shop.utils.CookieUtil;
import com.tz.spike_shop.vo.ResponseResult;
import com.tz.spike_shop.vo.ResponseResultEnum;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.HandlerInterceptor;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;

@Component
public class UserInterceptor implements HandlerInterceptor {

    @Autowired
    private IUserService userService;

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
        if (handler instanceof HandlerMethod) {
            User user = getUser(request, response);
            if (user == null) {
                render(response, ResponseResultEnum.USER_NOT_LOGIN);
                return false;
            }
            UserContext.setUser(user);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response, Object handler, Exception ex) throws Exception {
        UserContext.remove();
    }

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
}
