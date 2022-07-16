package com.tz.spike_shop.exception;

import com.tz.spike_shop.vo.ResponseResult;
import com.tz.spike_shop.vo.ResponseResultEnum;
import lombok.extern.slf4j.Slf4j;
import org.springframework.validation.BindException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    public ResponseResult ExceptionHandler(Exception e){
        log.error(e.getMessage());
        if (e instanceof GlobalException) {
            GlobalException ex = (GlobalException) e;
            return ResponseResult.error(ex.getResponseResultEnum());
        }
        else if (e instanceof BindException) {
            BindException ex = (BindException) e;
            ResponseResult result = ResponseResult.error(ResponseResultEnum.BIND_ERROR);
            result.setMessage("参数校验异常 : " + ex.getBindingResult().getAllErrors().get(0).getDefaultMessage());
            return result;
        }
        return ResponseResult.error(ResponseResultEnum.ERROR);
    }
}
