package com.tz.spike_shop.vo;

import com.tz.spike_shop.pojo.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.security.DenyAll;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsDetailVo {

    private Integer status;

    private User user;

    private Integer remainTime;

    private GoodsVo goods;
}
