package com.tz.spike_shop.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.Date;
import java.util.TimeZone;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GoodsVo {

//    public static void main(String[] args) {
//        TimeZone zone = TimeZone.getDefault();
//        System.out.println(zone.getID());
//    }

    /**
     * 商品id
     */
    private Long id;

    /**
     * 商品价格
     */
    private BigDecimal price;

    /**
     * 商品秒杀价格
     */
    private BigDecimal spikePrice;

    /**
     * 商品名称
     */
    private String goodsName;

    /**
     * 商品标题
     */
    private String goodsTitle;

    /**
     * 商品详情
     */
    private String detail;

    /**
     * 商品库存
     */
    private Integer storeCount;

    /**
     * 商品秒杀库存
     */
    private Integer spikeCount;

    /**
     * 商品图片
     */
    private String picture;

    /**
     * 商品秒杀开始时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date startDate;

    /**
     * 商品秒杀结束时间
     */
    @JsonFormat(shape = JsonFormat.Shape.STRING , pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Shanghai")
    private Date endDate;

}
