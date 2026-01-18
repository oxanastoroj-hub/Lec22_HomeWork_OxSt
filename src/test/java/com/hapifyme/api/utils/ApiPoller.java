package com.hapifyme.api.utils;

import static org.awaitility.Awaitility.await;
import static java.util.concurrent.TimeUnit.SECONDS;
import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

public class ApiPoller {

    /**
     * Așteaptă ca un endpoint să returneze un anumit status.
     * @param url Endpoint-ul de verificat
     * @param expectedStatus Valoarea așteptată pentru câmpul "status"
     */
    public static void pollForStatus(String url, String expectedStatus, String apiKey) {
        System.out.println("Polling URL: " + url + " expecting status: " + expectedStatus);

        await()
                .alias("Așteptare status " + expectedStatus + " pe " + url)
                .atMost(20, SECONDS)
                .pollInterval(2, SECONDS) // Verificăm la fiecare 2 secunde
                .untilAsserted(() -> {
                    given()
                            .header("Authorization", apiKey)
                            .when()
                            .get(url)
                            .then()
                            .statusCode(200) // Verificăm întâi că requestul tehnic e OK
                            .body("status", equalTo(expectedStatus)); // Verificăm business logic
                });

        System.out.println("Status confirmat!");
    }
}