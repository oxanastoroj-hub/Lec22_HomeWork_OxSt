package com.hapifyme.api.utils;

public class DataGenerator {

    private DataGenerator() {}

    public static String generateUniqueEmail() {
        return "test_user" + System.currentTimeMillis() + "@gmail.com";
    }
}
