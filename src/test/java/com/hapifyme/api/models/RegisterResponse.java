package com.hapifyme.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)
public class RegisterResponse {
    private String status;
    private String message;
    @JsonProperty("api_key")
    private String api_key;
    @JsonProperty("user_id")
    private String user_id;
    @JsonProperty("username")
    private String username;


    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public String getApiKey() { return api_key; }
    public String getUserId() { return user_id; }
    public String getUsername() { return username; }

}
