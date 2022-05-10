package ru.HelenEVA.tests;

import io.qameta.allure.*;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.HelenEva.dao.BookingdatesRequest;
import ru.HelenEva.dao.CreateTokenRequest;
import ru.HelenEva.dao.PartialUpdateBookingRequest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@Severity(SeverityLevel.BLOCKER)
@Story("Delete a booking")
@Feature("Tests for booking deletion")

public class DeleteBookingTests extends BaseTest {

    @BeforeAll
    static void beforeAll() {

        request = CreateTokenRequest.builder()
                .username("admin")
                .password("password123")
                .build();

        requestBookingdates = BookingdatesRequest.builder()
                .checkin(dateFormat.format(faker.date().birthday().getDate()))
                .checkout(dateFormat.format(faker.date().birthday().getDate()))
                .build();

        requestPartialupdate = PartialUpdateBookingRequest.builder()
                .firstname("Mary")
                .lastname("Brown")
                .totalprice(111)
                .depositpaid(true)
                .bookingdates(requestBookingdates)
                .additionalneeds("Breakfast")
                .build();

        token = given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .body(request)
                .expect()
                .statusCode(200)
                .body("token", is(not(nullValue())))
                .when()
                .post("auth")
                .prettyPeek()
                .body()
                .jsonPath()
                .get("token")
                .toString();

    }

    @BeforeEach
    void setUp() {

        id = given()
                .log()
                .all()
                .header("Content-Type", "application/json")
                .body(requestPartialupdate)
                .expect()
                .statusCode(200)
                .when()
                .post("booking")
                .prettyPeek()
                .body()
                .jsonPath()
                .get("bookingid")
                .toString();
    }

    @Test
    @Description("Deleting a booking with a cookie")
    @Step("Delete booking cookie")
    void deleteBookingCookiePositiveTest() {

        given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Cookie", "token="+token)
                .when()
                .delete("/booking/"+id)
                .prettyPeek()
                .then()
                .statusCode(201);
    }

    @Test
    @Description("Deleting a booking with a token")
    @Step("Delete booking authorization")
    void deleteBookingAuthorizationPositiveTest() {

        given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .when()
                .delete("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(201);
    }

    @Test
    @Description("Negative test - Deleting a booking without authorisation")
    @Step("Delete booking without authorization")
    void deleteBookingWithoutAuthorisationNegativeTest() {

        given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .when()
                .delete("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(403);
    }
}