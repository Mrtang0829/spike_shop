package com.tz.spike_shop.vo;

import com.tz.spike_shop.annotation.IsMobile;
import lombok.Data;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

@Data
public class RegisterVo {

    @NotEmpty
    @NotNull
    @IsMobile
    private String mobile;

    @NotEmpty
    @NotNull
    private String password;
}
