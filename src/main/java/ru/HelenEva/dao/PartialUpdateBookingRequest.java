package ru.HelenEva.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.With;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@With

public class PartialUpdateBookingRequest {
    @Setter
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