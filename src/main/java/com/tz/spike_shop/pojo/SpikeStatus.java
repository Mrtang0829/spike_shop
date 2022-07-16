package com.tz.spike_shop.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpikeStatus {

    private Integer orderId;

    private SpikeStatusEnum spikeStatusEnum;
}
