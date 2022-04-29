package ru.HelenEVA.tests;

import com.github.javafaker.Faker;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import ru.HelenEva.dao.BookingdatesRequest;
import ru.HelenEva.dao.CreateTokenRequest;
import ru.HelenEva.dao.PartialUpdateBookingRequest;
import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Properties;
import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;
import static org.hamcrest.CoreMatchers.containsStringIgnoringCase;
import static org.hamcrest.MatcherAssert.assertThat;

public class PartialUpdateBookingTests {

    private static final String PROPERTIES_FILE_PATH = "src/test/resources/application.properties";
    private static CreateTokenRequest request;
    private static BookingdatesRequest requestBookingdates;
    private static PartialUpdateBookingRequest requestPartialupdate;
    static Properties properties = new Properties();
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    static Faker faker = new Faker();

    static String token;
    static String id;

    @BeforeAll
    static void beforeAll() throws IOException {
        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        properties.load(new FileInputStream(PROPERTIES_FILE_PATH));
        RestAssured.baseURI = properties.getProperty("base.url");

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
                .post("https://restful-booker.herokuapp.com/auth")
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