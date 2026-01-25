package com.hapifyme.api.tests;

import io.restassured.builder.RequestSpecBuilder;
import io.restassured.specification.RequestSpecification;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import com.hapifyme.api.utils.TestContext;
import java.lang.reflect.Method;
import com.hapifyme.api.utils.ConfigManager;

public class BaseTest {
    protected static RequestSpecification requestSpec;
    protected static final Logger logger = LogManager.getLogger(BaseTest.class);

    @BeforeClass
    public void setup() {
         requestSpec = new RequestSpecBuilder()
                .setBaseUri(ConfigManager.BASE_URL)
                .setContentType("application/json")
                .addHeader("Accept", "application/json")
                .build();
    }

    @BeforeMethod
    public void setup(Method method) {
        // Logăm informațiile esențiale la începutul fiecărui test
        logger.info("========================================");
        logger.info("Începere Test: " + method.getName());
        // Adăugăm ID-ul thread-ului pentru a putea depana execuția paralelă
        logger.info("Running on Thread ID: " + Thread.currentThread().getId());
        logger.info("Base URL: {}", ConfigManager.BASE_URL);
        logger.info("========================================");
    }

    @AfterMethod
    public void tearDown() {
        // Critic pentru paralelizare: Curățăm datele din ThreadLocal
        // Dacă nu facem asta, următorul test care refolosește acest thread ar putea vedea date vechi
        TestContext.clear();
        logger.debug("Context curățat pentru Thread ID: " + Thread.currentThread().getId());
    }

}
