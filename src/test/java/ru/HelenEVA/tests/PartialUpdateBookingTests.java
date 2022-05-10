package ru.HelenEVA.tests;

import io.qameta.allure.*;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.HelenEva.dao.BookingdatesRequest;
import ru.HelenEva.dao.CreateTokenRequest;
import ru.HelenEva.dao.PartialUpdateBookingRequest;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.MatcherAssert.assertThat;

@Severity(SeverityLevel.BLOCKER)
@Story("Partial update a booking")
@Feature("Tests for changes to the booking")

public class PartialUpdateBookingTests extends BaseTest {

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

    @AfterAll
    static void afterAll() {

        given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Cookie", "token="+token)
                .when()
                .delete("booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(201);
    }

    @Test
    @Description("Change of booking, authorization with cookies")
    @Step("Update booking with cookie")
    void updateBookingCookiePositiveTest() {

        given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(requestPartialupdate)
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("firstname", equalTo("Mary"))
                .body("lastname", equalTo("Brown"))
                .body("totalprice", equalTo(Integer.valueOf("111")))
                .body("depositpaid", equalTo(Boolean.valueOf("true")))
                .body("bookingdates.checkin", is(equalTo("1970-01-01")))
                .body("bookingdates.checkout", is(equalTo("1970-01-01")))
                .body("additionalneeds", equalTo("Breakfast"));
    }

    @Test
    @Description("Change of booking, authorization with token")
    @Step("Update booking with authorisation")
    void updateBookingAuthorisationPositiveTest() {

        Response response = given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Authorization", "Basic YWRtaW46cGFzc3dvcmQxMjM=")
                .body(requestPartialupdate)
                .when()
                .patch("/booking/" + id)
                .prettyPeek();
        assertThat(response.statusCode(), equalTo(200));
        assertThat(response.body().jsonPath().get("firstname"),equalTo("Mary"));
        assertThat(response.body().jsonPath().get("lastname"),equalTo("Brown"));
        assertThat(response.body().jsonPath().get("totalprice"),equalTo(Integer.valueOf("111")));
        assertThat(response.body().jsonPath().get("depositpaid"),equalTo(Boolean.valueOf("true")));
        assertThat(response.body().jsonPath().get("bookingdates.checkin"),is(equalTo("1970-01-01")));
        assertThat(response.body().jsonPath().get("bookingdates.checkout"),is(equalTo("1970-01-01")));
        assertThat(response.body().jsonPath().get("additionalneeds"),equalTo("Breakfast"));
    }

    @Test
    @Description("Change of booking without authorization")
    @Step ("Update booking without authorisation")
    void updateBookingWithoutAuthorisationNegativeTest() {

        given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .body(requestPartialupdate)
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(403);
    }

    @Test
    @Description("Changing the firstname in latin on the booking")
    @Step("Update booking firstname in latin")
    void updateBookingFirstNameInLatinPositiveTest() {

        given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(requestPartialupdate.withFirstname("Tom"))
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("firstname", equalTo("Tom"));
    }

    @Test
    @Description("Changing the firstname in capital letters  on the booking")
    @Step("Update booking firstname in capital letters")
    void updateBookingFirstNameInCapitalLettersPositiveTest() {


        given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(requestPartialupdate.withFirstname("MARY"))
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("firstname", equalTo("MARY"));
    }

    @Test
    @Description("Changing the lastname in cyrillic on the booking")
    @Step ("Update booking lastname in cyrillic")
    void updateBookingLastNameInCyrillicPositiveTest() {
        given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(requestPartialupdate.withLastname("Мария"))
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("lastname", equalTo("Мария"));
    }

    @Test
    @Description("Changing the firstname consisting of 1 character")
    @Step("Update booking firstname 1 symbol")
    void updateBookingFirstName1siSymbolPositiveTest() {
        given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(requestPartialupdate.withFirstname("L"))
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("firstname", equalTo("L"));
    }

    @Test
    @Description("Changing the check in in correct")
    @Step("Update booking check in change")
    void updateBookingCheckInChangePositiveTest() {

        given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(requestPartialupdate.withBookingdates(requestBookingdates.withCheckin("1990-05-05")))
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("bookingdates.checkin", equalTo("1990-05-05"));
    }

    @Test
    @Description("Changing the check out in correct")
    @Step("Update booking check out change")
    void updateBookingCheckOutChangePositiveTest() {

        given()
                .log()
                .method()
                .log()
                .uri()
                .log()
                .body()
                .header("Content-Type", "application/json")
                .header("Accept", "application/json")
                .header("Cookie", "token=" + token)
                .body(requestPartialupdate.withBookingdates(requestBookingdates.withCheckout("1990-05-05")))
                .when()
                .patch("/booking/" + id)
                .prettyPeek()
                .then()
                .statusCode(200)
                .body("bookingdates.checkout", equalTo("1990-05-05"));
    }
}