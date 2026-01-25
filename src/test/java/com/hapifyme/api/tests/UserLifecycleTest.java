package com.hapifyme.api.tests;

import io.restassured.http.ContentType;
import io.restassured.response.Response;
import io.qameta.allure.*;
import com.hapifyme.api.models.*;
import com.hapifyme.api.utils.*;
import org.testng.annotations.Test;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.*;

@Epic("hapifyMe Project")
@Feature("e2e User LifeCycle module")
public class UserLifecycleTest extends BaseTest {
    private String email;
    private final String password = "Test1234!";
    private String apiKey;
    private String userid;
    private String username;
    private String bearerToken;

    @Test (priority = 1, description = "Register new user")
    @Severity(SeverityLevel.BLOCKER)
    @Description("This test registrates a new user and returns apiKey, user ID and user Name for the next steps ")
    @Story("JIRA-01. I want to create a new user, log in and delete it")
    @Step("Step1: Username with Email generated and Password={1}")
    public void registerUser() {
        email = DataGenerator.generateUniqueEmail();

        RegisterRequest request = new RegisterRequest();
        request.setFirst_name("Oxana");
        request.setLast_name("Storoj");
        request.setEmail(email);
        request.setPassword(password);

        logger.info("Step 1: Starting registration user with email:  " + email + " and password: " + password);

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
        username = response.getUsername();

        String statusUrl = ConfigManager.CONFIRM_STATUS + email;
        ApiPoller.pollForStatus(
                statusUrl,
                "success",
                apiKey
        );

        logger.debug("User created with");
        logger.debug("ID: " + userid);
        logger.debug("username: " + username);
        logger.debug("api key: " + apiKey);
    }

    @Test (priority = 2, dependsOnMethods = "registerUser")
    @Step("Step 2: Confirm email for {0}")
    public void waitForConfirmation() {
        String confirmUrl = ConfigManager.CONFIRM_EMAIL + apiKey;

        logger.info("Step 2: Start Confirmation email: " + email);

        Response response = given()
                                .header("Authorization", apiKey)
                                .accept(ContentType.JSON)
                                .when()
                                .get(confirmUrl);

        int statusCode = response.getStatusCode();

        if (statusCode == 200)
        {
            logger.debug("Email confirmed!");
        }
        else
        {
            logger.error("Email in not confirmed! Status code is " + statusCode);
        }
    }

    @Test (priority = 3)
    @Step("Step 3: Login with username={0}")
        public void login() {

        logger.info("Step 3: Logging in the system with user: " + email + " and password: " + password);

        LoginRequest loginBody = new LoginRequest(username, password);
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

        logger.debug("Logged in successfully!" + "\n Bearer Token is: " + bearerToken);

    }

    @Test (priority = 4)
    @Step("Step 4: Read Profile for userId={0}")
    public void readProfile() {
        String profileUrl = ConfigManager.GET_PROFILE + userid;

        logger.info("Step 4: Validation of data introduced in registration step for user with ID: " + userid);

        given()
                .header("Authorization", apiKey)
                .accept(ContentType.JSON)
                .when()
                .get(profileUrl)
                .then()
                .statusCode(200)
                .body("status", equalTo("success"))
                .body("user.email", equalTo(email))
                .body("user.username", equalTo(username))
                .body("user.id", equalTo(userid));

        logger.debug("Data validated successfully!");
    }

    @Test (priority = 5)
    @Step("Step 5: Update Profile for userID={0}")
    public void updateProfile() {
        String profileUrl = ConfigManager.GET_PROFILE + userid;

        logger.info("Step 5: Profile Update");

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

        logger.debug("Updated Successfully! New data is: ");
        logger.debug("name: " + updateBody.getFirstName());
        logger.debug("last name: " + updateBody.getLastName());
        logger.debug("email: " + updateBody.getEmail());
    }

    @Test(priority = 6)
    @Step("Step 6: Delete Profile with Bearer token and make negative check for userId={0}")
    public void deleteProfile() {

    logger.info("Step 6: Profile Delete. Negative Check");
    logger.info("Bearer Token is: " + bearerToken);

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

        logger.debug("Delete Done. User was deleted.");

        logger.info("Step7: Starting negaitve check...");

        String profileUrl = ConfigManager.GET_PROFILE + userid;
        given()
                .header("Authorization", apiKey)
                .accept(ContentType.JSON)
                .when()
                .get(profileUrl)
                .then()
                .body("status", equalTo("error"))
                .body("message", containsString("not found"));

        logger.debug("Negaitve check Done. User doesn't exist anymore");

    }

    // Metodă ajutătoare pentru a atașa text (JSON, Logs) în raportul Allure
    @Attachment(value = "{0}", type = "text/plain")
    public String attachTextToReport(String attachmentName, String message) {
        return message; // Allure ia valoarea returnată și o pune în raport
    }

  }

