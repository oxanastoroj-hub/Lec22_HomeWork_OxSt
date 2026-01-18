package com.hapifyme.api.models;

public class LoginRequest {
    private String username;
    private String password;

    // Constructor
    public LoginRequest(String username, String password) {
        this.username = username;
        this.password = password;
    }

    // Getters și Setters (obligatorii pentru Jackson)
    public String getUsername() { return username; }
    public void setUsername(String email) { this.username = username; }
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}