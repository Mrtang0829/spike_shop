package com.tz.spike_shop.pojo;

import lombok.*;

@Getter
@ToString
@AllArgsConstructor
public enum SpikeStatusEnum {

    SPIKE_QUEUEING(0, "秒杀排队中"),

    SPIKE_SUCCESS(1, "秒杀成功"),

    SPIKE_ERROR(2, "秒杀失败"),
    ;

    private Integer code;

    private String message;

}
