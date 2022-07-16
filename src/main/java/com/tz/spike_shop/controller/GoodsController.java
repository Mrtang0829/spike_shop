package com.tz.spike_shop.controller;

import com.tz.spike_shop.pojo.User;
import com.tz.spike_shop.service.IGoodsService;
import com.tz.spike_shop.service.IUserService;
import com.tz.spike_shop.vo.GoodsDetailVo;
import com.tz.spike_shop.vo.GoodsVo;
import com.tz.spike_shop.vo.ResponseResult;
import com.tz.spike_shop.vo.ResponseResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ValueOperations;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.thymeleaf.context.WebContext;
import org.thymeleaf.spring5.view.ThymeleafViewResolver;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Controller
@RequestMapping("/goods")
@Slf4j
public class GoodsController {

    @Autowired
    private IUserService userService;

    @Autowired
    private IGoodsService goodsService;

    @Autowired
    private RedisTemplate redisTemplate;

    @Autowired
    private ThymeleafViewResolver thymeleafViewResolver;

    /**
     * 测速redis中文16进制问题，通过redisConfig解决
     * @return
     */
    @GetMapping("/test")
    @ResponseBody
    public String test() {
        redisTemplate.opsForValue().set("hello", "你好", 60 * 3, TimeUnit.SECONDS);
        return "hello";
    }

    /**
     * 通过 session 形式获取用户
     * @param session
     * @param model
     * @param cookie
     * @return
     */
    @RequestMapping("/toListTest")
    public String toListTest(HttpSession session, Model model, @CookieValue("userCookie") String cookie) {
        if (StringUtils.isEmpty(cookie)) {
            return "login";
        }

        User user = (User)session.getAttribute(cookie);
        if (user == null) return "login";

        model.addAttribute("user", user);
        return "goodsList";
    }

    @RequestMapping("/toListTest2")
    public String toListTest2(HttpServletRequest request, HttpServletResponse response, Model model, @CookieValue("userCookie") String cookie) {
        if (StringUtils.isEmpty(cookie)) {
            return "login";
        }

        User user = userService.getUserByCookie(cookie, request, response);
        if (user == null) return "login";

        model.addAttribute("user", user);
        return "goodsList";
    }

    @RequestMapping(value = "/toListTest3")
    public String toList(Model model, User user) {
        if (user == null) return "login";

        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findAllGoodsVo());

        return "goodsList";
    }

    /**
     * 采用了 WebConfig , 传入user前会校验， user 通过 UserArgumentResolver 获取
     * 采用redis缓存页面，返回缓存的页面,使用produces定义返回类型以及防止乱码
     * 优化过程 -> 直接传页面过去(return goodsList) -> 缓存页面，redis中没有再生成传递过去 ->
     * 优化后 qbs : 9600, 优化前3400
     * @param model
     * @param user
     * @return
     */
    @RequestMapping(value = "/toList", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toList(Model model, User user, HttpServletRequest request, HttpServletResponse response) {
//        if (user == null) return "login";
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsList");

        if (!StringUtils.isEmpty(html)) {
            return html;
        }

        model.addAttribute("user", user);
        model.addAttribute("goodsList", goodsService.findAllGoodsVo());
        // 手动渲染页面并存入redis
        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        // goodsList为指定模板
        html = thymeleafViewResolver.getTemplateEngine().process("goodsList", webContext);
        valueOperations.set("goodsList", html, 60, TimeUnit.SECONDS);

        return html;
    }

    /**
     * 页面优化前
     * @param model
     * @param user
     * @param id
     * @return
     */
    @GetMapping("/toDetailTest/{goods_id}")
    public String toDetail(Model model, User user, @PathVariable("goods_id") Long id) {
        GoodsVo good = goodsService.findGoodById(id);
        System.out.println(good.getStartDate());
        Date startDate = good.getStartDate();
        Date endDate = good.getEndDate();
        Date nowDate = new Date();
        int status = 0;
        if (nowDate.before(startDate)) {
        }
        else if (nowDate.after(endDate)) status = 2;
        else status = 1;
        model.addAttribute("status", status);
        model.addAttribute("user", user);
        model.addAttribute("goods", good);
        return "goodsDetail";
    }

    /**
     * 页面优化后(整个页面缓存)
     * @param model
     * @param user
     * @param id
     * @param request
     * @param response
     * @return
     */
    @GetMapping(value = "/toDetailTuning1/{goods_id}", produces = "text/html;charset=utf-8")
    @ResponseBody
    public String toDetail(Model model, User user, @PathVariable("goods_id") Long id,
                            HttpServletRequest request, HttpServletResponse response) {
        ValueOperations valueOperations = redisTemplate.opsForValue();
        String html = (String) valueOperations.get("goodsDetail:" + id);

        if (!StringUtils.isEmpty(html)) {
            return html;
        }
        GoodsVo good = goodsService.findGoodById(id);
        Date startDate = good.getStartDate();
        Date endDate = good.getEndDate();
        Date nowDate = new Date();
        int status = 0;
        if (nowDate.before(startDate)) {
        }
        else if (nowDate.after(endDate)) status = 2;
        else status = 1;
        model.addAttribute("status", status);
        model.addAttribute("user", user);
        model.addAttribute("goods", good);

        WebContext webContext = new WebContext(request, response, request.getServletContext(), request.getLocale(), model.asMap());
        html = thymeleafViewResolver.getTemplateEngine().process("goodsDetail", webContext);
        valueOperations.set("goodsDetail:" + id, html, 60, TimeUnit.SECONDS);

        return html;
    }

    /**
     * 模拟前后端分离模式，按需返回动态数据,此时model没用了，需要自定义返回数据
     * @param user
     * @param id
     * @return
     */
    @GetMapping(value = "/detail/{goods_id}")
    @ResponseBody
    public ResponseResult toDetail(User user, @PathVariable("goods_id") Long id) {
        if (user == null) return ResponseResult.error(ResponseResultEnum.USER_NOT_EXIST_ERROR);
        log.info(String.valueOf(id));
        GoodsVo good = goodsService.findGoodById(id);
        Date startDate = good.getStartDate();
        Date endDate = good.getEndDate();
        Date nowDate = new Date();
        int status = 0, remainTime = 0;
        if (nowDate.before(startDate)) {
            remainTime = (int)((startDate.getTime() - nowDate.getTime()) / 1000);
        }
        else if (nowDate.after(endDate)) status = 2;
        else status = 1;

        GoodsDetailVo detailVo = new GoodsDetailVo();
        detailVo.setStatus(status);
        detailVo.setUser(user);
        detailVo.setGoods(good);
        detailVo.setRemainTime(remainTime);

        return ResponseResult.success(detailVo);
    }

}
