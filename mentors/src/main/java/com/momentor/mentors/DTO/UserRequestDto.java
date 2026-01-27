package com.momentor.mentors.DTO;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserRequestDto {
    @NotBlank(message = "Name Cannot Be Empty")
    private String name;
    @Email(message = "Email Format is Invalid")
    @NotBlank(message = "Email Cannot Be Empty")
    private String email;
    @Size(min=5,message="Password Must Be Less Than Five Character")
    private String password;
    @NotBlank(message = "Role Is Required")
    private String role;
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }
    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }
    public String getRole() {
        return role;
    }
    public void setRole(String role) {
        this.role = role;
    }
}
