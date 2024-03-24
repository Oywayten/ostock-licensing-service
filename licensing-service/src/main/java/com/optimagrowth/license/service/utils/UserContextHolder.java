package com.optimagrowth.license.service.utils;

public class UserContextHolder {
    private static final ThreadLocal<UserContext> USER_CONTEXT_THREAD_LOCAL = new ThreadLocal<>();

    public static UserContext getContext() {
        UserContext context = USER_CONTEXT_THREAD_LOCAL.get();

        if (context == null) {
            context = createEmptyContext();
            setContext(context);
        }
        return USER_CONTEXT_THREAD_LOCAL.get();
    }

    public static void setContext(UserContext context) {
        USER_CONTEXT_THREAD_LOCAL.set(context);
    }

    public static UserContext createEmptyContext() {
        return new UserContext();
    }
}
