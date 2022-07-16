package com.tz.spike_shop.pojo;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SpikeMessage {

    private User user;

    private Long goodsId;
}
