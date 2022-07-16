package com.tz.spike_shop.exception;

import com.tz.spike_shop.vo.ResponseResultEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class GlobalException extends RuntimeException{

    private String message;

    private ResponseResultEnum responseResultEnum;
}
