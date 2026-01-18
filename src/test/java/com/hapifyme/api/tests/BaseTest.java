package com.hapifyme.api.tests;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;

public class BaseTest {
    protected static RequestSpecification requestSpec;

    @BeforeClass
    public void setup() {
         requestSpec = new RequestSpecBuilder()
                .setBaseUri("https://test.hapifyme.com/api")
                .setContentType("application/json")
                .addHeader("Accept", "application/json")
                .build();
    }
}
