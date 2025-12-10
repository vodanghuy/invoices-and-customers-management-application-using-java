package io.huyvo.securecapita.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.experimental.SuperBuilder;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
// Ho tro ke thua giua cha - con.
@SuperBuilder
// Chi dua vao JSON nhung field co gia tri khac voi gia tri mac dinh.
@JsonInclude(JsonInclude.Include.NON_DEFAULT)
public class User {
    private Long id;
    @NotEmpty(message = "First name cannot be empty")
    private String firstName;
    @NotEmpty(message = "Last name cannot be empty")
    private String lastName;
    @NotEmpty(message = "Email cannot be empty")
    @Email(message = "Invalid email. Please enter a valid email address")
    private String email;
    @NotEmpty(message = "Password cannot be empty")
    private String password;
    private String address;
    private String phone;
    private String title;
    private String bio;
    private Boolean enabled;
    private Boolean isNotLocked;
    private Boolean isUsingMfa;
    private String imageUrl;
    private LocalDateTime createdAt;
}
