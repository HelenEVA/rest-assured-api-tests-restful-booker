package ru.HelenEVA.tests;

import io.qameta.allure.*;
import lombok.ToString;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import ru.HelenEva.dao.BookingdatesRequest;
import ru.HelenEva.dao.CreateTokenRequest;
import ru.HelenEva.dao.PartialUpdateBookingRequest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

@Severity(SeverityLevel.BLOCKER)
@Story("Delete a booking")
@Feature("Tests for booking deletion")

@ToString

public class DeleteBookingTests extends BaseTest {

    final static Logger log = LoggerFactory.getLogger(DeleteBookingTests.class);

    @BeforeAll
    static void beforeAll() {

        log.info("Start of DeleteBookingTests");
        request = CreateTokenRequest.builder()
                .username("admin")
                .password("password123")
                .build();
        log.info(request.toString());

        log.info("Create a booking dates");
        requestBookingdates = BookingdatesRequest.builder()
                .checkin(dateFormat.format(faker.date().birthday().getDate()))
                .checkout(dateFormat.format(faker.date().birthday().getDate()))
                .build();
        log.info(requestBookingdates.toString());

        requestPartialupdate = PartialUpdateBookingRequest.builder()
                .firstname("Mary")
                .lastname("Brown")
                .totalprice(111)
                .depositpaid(true)
                .bookingdates(requestBookingdates)
                .additionalneeds("Breakfast")
                .build();
        log.info(requestPartialupdate.toString());

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
        log.info("The token is: " + token);

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
        log.info("Booking id is: " + id);
    }

    @Test
    @Description("Deleting a booking with a cookie")
    @Step("Delete booking cookie")
    void deleteBookingCookiePositiveTest() {

        log.info("Start test - Delete booking cookie");
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
        log.info("End test - Delete booking cookie");
    }

    @Test
    @Description("Deleting a booking with a token")
    @Step("Delete booking authorization")
    void deleteBookingAuthorizationPositiveTest() {

        log.info("Start test - Delete booking authorization");
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
        log.info("End test - Delete booking authorization");
    }

    @Test
    @Description("Negative test - Deleting a booking without authorisation")
    @Step("Delete booking without authorization")
    void deleteBookingWithoutAuthorisationNegativeTest() {

        log.info("Start test - Delete booking without authorization");
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
        log.info("End test - Delete booking without authorization");
    }
}