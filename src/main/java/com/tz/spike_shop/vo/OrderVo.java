package com.tz.spike_shop.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.annotation.security.DenyAll;
import java.math.BigDecimal;
import java.util.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class OrderVo {

    private Long id;

    private String goodsName;

    private String picture;

    private BigDecimal goodsPrice;

    private Date createDate;

    private Integer status;
}
