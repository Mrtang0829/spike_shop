package com.tz.spike_shop.config;

import com.tz.spike_shop.pojo.User;

public class UserContext {

    private static final ThreadLocal<User> userInfo = new ThreadLocal<>();

    public static void setUser(User user) {
        userInfo.set(user);
    }

    public static User getUser() {
        return userInfo.get();
    }

    public static void remove() {
        userInfo.remove();
    }
}
