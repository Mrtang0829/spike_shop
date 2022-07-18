package com.tz.spike_shop.controller;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/demo")
@Slf4j
public class DemoController {

    @GetMapping("/hello")
    public String hello(Model model){
        log.info("hello");
        model.addAttribute("name", "ttt");
        return "hello";
    }
}
