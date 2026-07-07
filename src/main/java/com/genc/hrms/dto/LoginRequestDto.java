package com.genc.hrms.dto;

public class LoginRequestDto {
    private String username;
    private String password;

    // Default Constructor for Jackson Deserialization
    public LoginRequestDto() {}

    public LoginRequestDto(String username, String password) {
        this.username = username;
        this.password = password;
    }

    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }

    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }
}