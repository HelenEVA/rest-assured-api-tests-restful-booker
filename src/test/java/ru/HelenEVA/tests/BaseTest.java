package ru.HelenEVA.tests;

import com.github.javafaker.Faker;
import io.qameta.allure.Step;
import io.qameta.allure.Story;
import io.qameta.allure.restassured.AllureRestAssured;
import io.restassured.RestAssured;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import ru.HelenEva.dao.BookingdatesRequest;
import ru.HelenEva.dao.CreateTokenRequest;
import ru.HelenEva.dao.PartialUpdateBookingRequest;

import java.io.FileInputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Properties;

import static io.restassured.RestAssured.given;
import static org.hamcrest.CoreMatchers.*;

public abstract class BaseTest {

    protected static final String PROPERTIES_FILE_PATH = "src/test/resources/application.properties";
    protected static CreateTokenRequest request;
    protected static BookingdatesRequest requestBookingdates;
    protected static PartialUpdateBookingRequest requestPartialupdate;
    static Properties properties = new Properties();
    static SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
    static Faker faker = new Faker();

    static String token;
    static String id;


    @BeforeAll
    @Story("Create a booking")
    static void beforeAll() throws IOException {

        RestAssured.enableLoggingOfRequestAndResponseIfValidationFails();
        RestAssured.filters(new AllureRestAssured());


        properties.load(new FileInputStream(PROPERTIES_FILE_PATH));
        RestAssured.baseURI = properties.getProperty("base.url");

    }
}
