package com.tz.spike_shop.utils;

import org.apache.commons.codec.digest.DigestUtils;
import org.springframework.stereotype.Component;

import java.util.Random;

public class MD5Util {

    public static String md5(String str){
        return DigestUtils.md5Hex(str);
    }

    public static String md5(String str, String salt){
        return DigestUtils.md5Hex(str + salt);
    }

    public static String randomSalt(int n){
        char[] str = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789!@#$%^&*()".toCharArray();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < n; i++){
            char c = str[new Random().nextInt(str.length)];
            sb.append(c);
        }
        return sb.toString();
    }

}
