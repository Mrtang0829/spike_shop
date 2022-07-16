package com.tz.spike_shop.config;

import com.tz.spike_shop.pojo.User;

public class UserContext {

    private static final ThreadLocal<User> threadLocal = new ThreadLocal<>();

    public static void setThreadLocal(User user) {
        threadLocal.set(user);
    }

    public static User getThreadLocal() {
        return threadLocal.get();
    }
}
