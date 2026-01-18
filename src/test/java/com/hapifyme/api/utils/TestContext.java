package com.hapifyme.api.utils;

public class TestContext {
    // Containerul magic: Fiecare thread are propriul sertar pentru Token
    private static ThreadLocal<String> authToken = new ThreadLocal<>();
    private static ThreadLocal<Integer> userId = new ThreadLocal<>();

    // Setters
    public static void setToken(String token) {
        authToken.set(token);
    }

    public static void setUserId(int id) {
        userId.set(id);
    }

    // Getters
    public static String getToken() {
        return authToken.get();
    }

    public static int getUserId() {
        return userId.get();
    }

    // Cleanup (Important!)
    public static void clear() {
        authToken.remove();
        userId.remove();
    }
}