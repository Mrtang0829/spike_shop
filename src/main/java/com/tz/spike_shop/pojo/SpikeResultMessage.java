package com.tz.spike_shop.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpikeResultMessage {

    // 抢购成功后的订单号
    private Long orderId;
}
