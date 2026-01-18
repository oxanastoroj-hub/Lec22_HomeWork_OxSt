package com.hapifyme.api.tests;

import io.restassured.http.ContentType;

import com.hapifyme.api.models.*;
import com.hapifyme.api.utils.*;

//import org.testng.IReporter;
import org.testng.annotations.Test;

//import java.util.GregorianCalendar;

import static io.restassured.RestAssured.given;
//import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.hamcrest.Matchers.containsString;


public class UserLifecycleTest {

    private String email;
    private final String password = "Test1234!";
    private String apiKey;
    private String userid;
    private String usermane;
    private String bearerToken;

    @Test (priority = 1)
    public void registerUser() {
        email = DataGenerator.generateUniqueEmail();

        RegisterRequest request = new RegisterRequest();
        request.setFirst_name("Oxana");
        request.setLast_name("Storoj");
        request.setEmail(email);
        request.setPassword(password);

        System.out.println("Step 1");
        System.out.println("Starting registration user with email:  " + email);

        RegisterResponse response =
                given()
                        .baseUri(ConfigManager.BASE_URL)
                        .contentType(ContentType.JSON)
                        .body(request)
                        .when()
                        .post(ConfigManager.REGISTER)
                        .then()
                        .statusCode(201)
                        .extract()
                        .as(RegisterResponse.class);

        apiKey = response.getApiKey();
        userid = response.getUserId();
        usermane = response.getUsername();
        System.out.println("User created with: \n ID: " + userid + " \n username: " + usermane + "\n api key: " + apiKey );

        String statusUrl = ConfigManager.CONFIRM_STATUS + email;
        ApiPoller.pollForStatus(
                statusUrl,
                "success",
                apiKey
        );

    }

    @Test (priority = 2)
        public void waitForConfirmation() {
        String confirmUrl = ConfigManager.CONFIRM_EMAIL + apiKey;

        System.out.println("\n Step 2");
        System.out.println("Start Confirmation email: " + email);

        given()
                .header("Authorization", apiKey)
                .accept(ContentType.JSON)
                .when()
                .get(confirmUrl)
                .then()
                .statusCode(200);

        System.out.println("Email confirmed!");
    }

    @Test (priority = 3)
        public void login() {

        System.out.println("\n Step 3");
        System.out.println("Logging in the system with user: " + email + " and password: " + password);

        LoginRequest loginBody = new LoginRequest(usermane, password);
        LoginResponse loginResponse =
                 given()
                        .baseUri(ConfigManager.BASE_URL)
                        .contentType(ContentType.JSON)
                        .body(loginBody)
                        .when()
                        .post(ConfigManager.LOGIN)
                        .then()
                        .statusCode(200)
                        .extract().as(LoginResponse.class);


       bearerToken = loginResponse.getToken();

        System.out.println("Logged in successfully!" + "\n Bearer Token is: " + bearerToken);

    }

    @Test (priority = 4)
    public void readProfile() {
        String profileUrl = ConfigManager.GET_PROFILE + userid;

        System.out.println("\n Step 4");
        System.out.println("Validation of data introduced in registration step for user with ID: " + userid);

        given()
                .header("Authorization", apiKey)
                .accept(ContentType.JSON)
                //.log().all()
                .when()
                .get(profileUrl)
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("user.email", equalTo(email))
                .body("user.username", equalTo(usermane))
                .body("user.id", equalTo(userid));
                //.log().all();


        System.out.println("Data validated successfully!");
    }

    @Test (priority = 5)
    public void updateProfile() {
        String profileUrl = ConfigManager.GET_PROFILE + userid;

        System.out.println("\n Step 5");
        System.out.println("Profile Update");

        UpdateProfileRequest updateBody = new UpdateProfileRequest(userid, "UpdatedName",
                "UpdatedLast", "updated@updated.com", "updated.jpg");

        given()
                .baseUri(ConfigManager.BASE_URL)
                .header("Authorization", apiKey)
                .accept(ContentType.JSON)
                .body(updateBody)
                //.log().all()
                .when()
                .put(ConfigManager.UPDATE_PROFILE)
                //.put("/user/update_profile.php")
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("message", containsString("successfully"));
                //.log().all();

        System.out.println("Updated Successfully! New data is: "
                + "\n name: " + updateBody.getFirstName()
                + "\n last name: " + updateBody.getLastName()
                + "\n email: " + updateBody.getEmail() );
    }

    @Test(priority = 6)
    public void deleteProfile() {


    System.out.println("\n Step 6");
    System.out.println("Profile Delete. Negative Check");
    System.out.println("Bearer Token is: " + bearerToken);

    given()
            .baseUri(ConfigManager.BASE_URL)
            .header("Authorization", "Bearer " + bearerToken)
            .contentType(ContentType.JSON)
            .when()
            .delete(ConfigManager.DELETE_PROFILE)
            .then()
            .statusCode(200)
            .body("status", equalTo("success"))
            .body("message", containsString("deleted"));

        System.out.println("\nDelete Done. User was deleted.");

        System.out.println("\nStarting negaitve check...");

        String profileUrl = ConfigManager.GET_PROFILE + userid;
        given()
                .header("Authorization", apiKey)
                .accept(ContentType.JSON)
                .when()
                .get(profileUrl)
                .then()
                .body("status", equalTo("error"))
                .body("message", containsString("not found"));

        System.out.println("\nNegaitve check Done. User doesn't exist anymore");




    }
  }

