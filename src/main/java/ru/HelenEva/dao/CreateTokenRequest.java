package ru.HelenEva.dao;

import com.fasterxml.jackson.annotation.JsonInclude;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Builder;
import lombok.With;

@JsonInclude(JsonInclude.Include.NON_NULL)
@Builder
@With

public class CreateTokenRequest {

    @JsonProperty("username")
    private String username;
    @JsonProperty("password")
    private String password;

}