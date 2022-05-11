package ru.HelenEva.dao;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;

public class PartialUpdateBookingResponse {
    @Getter
    @JsonProperty("firstname")
    private String firstname;
    @JsonProperty("lastname")
    private String lastname;
    @JsonProperty("totalprice")
    private Integer totalprice;

    @JsonProperty("depositpaid")
    private Boolean depositpaid;
    @JsonProperty("bookingdates")
    private BookingdatesRequest bookingdates;
    @JsonProperty("additionalneeds")
    private String additionalneeds;
}
