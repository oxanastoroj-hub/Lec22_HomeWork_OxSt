package com.hapifyme.api.utils;

import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
//import java.util.concurrent.TimeUnit;
//import static org.awaitility.Awaitility.await;

public class ApiPoller {
    // Adaugarea logger
    private static final Logger logger = LogManager.getLogger(ApiPoller.class);

    /**
     * Așteaptă ca un endpoint să returneze un anumit status.
     * @param url Endpoint-ul de verificat
     * @param expectedStatus Valoarea așteptată pentru câmpul "status"
     */
    public static void pollForStatus(String url, String expectedStatus, String apiKey) {
        //System.out.println("Polling URL: " + url + " expecting status: " + expectedStatus);
        logger.info("Start Polling for URL: " + url);
        logger.info("Expecting status: " + expectedStatus);
        logger.info("............................");


        await()
                .alias("Waiting for status " + expectedStatus + " for " + url)
                .atMost(20, SECONDS)
                .pollInterval(2, SECONDS) // Verificăm la fiecare 2 secunde
                .untilAsserted(() -> {
                    var response = given()
                                    .header("Authorization", apiKey)
                                    .when()
                                    .get(url);
                    int statusCode = response.getStatusCode();
                    String actualStatus = response.jsonPath().getString("status");

                    if (statusCode == 200 && expectedStatus.equals(actualStatus))
                    {
                        logger.debug("\n Status confirmed: " + actualStatus);
                    }
                    else
                    {
                        logger.error("\n Status is: " + actualStatus + "(HTTP " + statusCode + " )");
                    }

                    response.then()
                                    .statusCode(200) // Verificăm întâi că requestul tehnic e OK
                                    .body("status", equalTo(expectedStatus)); // Verificăm business logic
                });

    }
}