package com.tz.spike_shop.vo;

import com.tz.spike_shop.annotation.IsMobile;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class LoginVo {

    @NotNull
    @NotEmpty
    @IsMobile
    private String mobile;

    @NotNull
    @NotEmpty
    private String password;
}
