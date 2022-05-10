package ru.HelenEVA.tests;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.HelenEva.dao.CreateTokenRequest;
import ru.HelenEva.dao.CreateTokenResponse;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@Severity(SeverityLevel.BLOCKER)
@Feature("Create token")
@Story("Generate a user token")

public class CreateTokenTests extends BaseTest{


    @BeforeAll
    static void beforeAll() {

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        request = CreateTokenRequest.builder()
                .username("admin")
                .password("password123")
                .build();

    }

    @Test
    @Description("Creating a token - in correct authorisation")
    @Step ("Create token")
    void createTokenPositiveTest() {

        CreateTokenResponse response = given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .body(request)
                .expect()
                .statusCode(200)
                .when()
                .post("auth")
                .prettyPeek()
                .then()
                .extract()
                .as(CreateTokenResponse.class);
        assertThat (response.getToken().length(),equalTo(15));
    }

    @Test
    @Description("Negative test - incorrect password entry")
    @Step ("Create token with a wrong password")
    void createTokenWithAWrongPasswordNegativeTest() {

        given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .body(request.withPassword("password"))
                .when()
                .post("auth")
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("reason", equalTo("Bad credentials"));
    }

    @Test
    @Description("Negative test - incorrect password and name entry")
    @Step("Create token with a wrong username and password")
    void createTokenWithAWrongUsernameAndPasswordNegativeTest() {

        Response response = given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .body(request.withUsername("admin1111") .withPassword("357"))
                .when()
                .post("https://restful-booker.herokuapp.com/auth")
                .prettyPeek();
        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.body().jsonPath().get("reason"), containsStringIgnoringCase("Bad credentials"));

    }
}