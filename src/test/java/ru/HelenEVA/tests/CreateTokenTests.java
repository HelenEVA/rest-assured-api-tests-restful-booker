package ru.HelenEVA.tests;

import io.qameta.allure.*;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.HelenEva.dao.CreateTokenRequest;
import ru.HelenEva.dao.CreateTokenResponse;

import java.io.FileInputStream;
import java.io.IOException;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@Severity(SeverityLevel.BLOCKER)
@Feature("Create token")

public class CreateTokenTests extends BaseTest{


    @BeforeAll
    static void beforeAll() throws IOException {

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();

        request = CreateTokenRequest.builder()
                .username("admin")
                .password("password123")
                .build();

        properties.load(new FileInputStream(PROPERTIES_FILE_PATH));
        RestAssured.baseURI = properties.getProperty("base.url");
    }

    @Test
    @io.qameta.allure.Muted
    @Step ("Creating a token - in correct authorisation")
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
    @io.qameta.allure.Muted
    @Step ("Negative test - incorrect password entry")
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
    @io.qameta.allure.Muted
    @Step("Negative test - incorrect password and name entry")
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