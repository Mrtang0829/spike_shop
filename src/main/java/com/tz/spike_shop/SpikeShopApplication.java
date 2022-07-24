package com.tz.spike_shop;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableAsync;

@SpringBootApplication
@MapperScan("com.tz.spike_shop.mapper")
@EnableAsync
public class SpikeShopApplication {

    public static void main(String[] args) {
        SpringApplication.run(SpikeShopApplication.class, args);
    }

}
