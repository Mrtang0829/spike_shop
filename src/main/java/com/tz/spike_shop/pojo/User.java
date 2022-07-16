package com.tz.spike_shop.pojo;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.util.Date;

/**
 * <p>
 *
 * </p>
 *
 * @author tz
 * @since 2022-07-09
 */
@Data
@EqualsAndHashCode(callSuper = false)
@TableName("t_user")
public class User implements Serializable {

    private static final long serialVersionUID = 1L;

    private Long id;

    private String username;

    private String password;

    /**
     * md5加密盐值
     */
    private String slat;

    /**
     * 头像
     */
    private String picture;

    private Date registerDate;

    private Date lastLoginDate;

    private Integer loginCount;


}
