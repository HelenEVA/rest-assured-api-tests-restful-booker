package ru.HelenEVA.tests;

import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import ru.HelenEva.dao.CreateTokenRequest;
import ru.HelenEva.dao.CreateTokenResponse;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

public class CreateTokenTests {

    private static final String PROPERTIES_FILE_PATH = "src/test/resources/application.properties";
    private static CreateTokenRequest request;
    private static CreateTokenResponse response;
    static Properties properties = new Properties();


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
    void createTokenPositiveTest() {

        response = given()
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