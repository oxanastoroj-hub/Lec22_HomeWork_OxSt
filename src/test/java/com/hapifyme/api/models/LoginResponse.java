package com.hapifyme.api.models;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

@JsonIgnoreProperties(ignoreUnknown = true)

public class LoginResponse {
    private String status;
    private String message;
    @JsonProperty("token")
    private String token;
    private UserDetails user;

    public String getStatus() { return status; }
    public String getMessage() { return message; }
    public String getToken() { return token; }
    public UserDetails getUser() { return user; }

    public void setToken (String token) { this.token = token; }

    public static class UserDetails {
        private String id;
        private String username;
        private String email;

        public String getId() { return id; }
        public String getUsername() { return username; }
        public String getEmail() { return email; }
    }

}
