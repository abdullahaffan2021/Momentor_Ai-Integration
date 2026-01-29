package com.momentor.mentors.DTO;

public class LogInResponseDto {
    private String token;
    public LogInResponseDto(String token) {
        this.token = token;
    }
    public String getToken() {
        return token;
    }
}
