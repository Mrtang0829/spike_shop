package com.tz.spike_shop.vo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.web.bind.annotation.ResponseBody;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseResult {

    private long code;
    private String message;
    private Object data;


    public static ResponseResult success(){
        return new ResponseResult(ResponseResultEnum.SUCCESS.getCode(), ResponseResultEnum.SUCCESS.getMessage(), null);
    }

    public static ResponseResult success(Object data){
        return new ResponseResult(ResponseResultEnum.SUCCESS.getCode(), ResponseResultEnum.SUCCESS.getMessage(), data);
    }

    public static ResponseResult error(ResponseResultEnum resEnum){
        return new ResponseResult(resEnum.getCode(), resEnum.getMessage(), null);
    }

    public static ResponseResult error(ResponseResultEnum resEnum, Object data){
        return new ResponseResult(resEnum.getCode(), resEnum.getMessage(), data);
    }

}
