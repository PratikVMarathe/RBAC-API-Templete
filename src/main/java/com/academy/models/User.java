package com.academy.models;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;
import java.util.ArrayList;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.academy.validators.ValidPhoneNumber;
import com.academy.validators.ValidPostalCode;
import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Past;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;


// @JsonFilter("dynamicFilter")
@Data
@Document(collection = "users")
@ValidPhoneNumber(message="Invalid phone number")
@ValidPostalCode(message="zipcode must be valid")
public class User {

    @Id
    private String id;

    @NotEmpty(message = "Username is required")
    private String username;

    @NotEmpty(message = "Email is required")
    private String email;

    @NotEmpty(message = "Password is required")
    private String password;

    private String name;
    
    private int age;

    private String isdCode;

    private String mobileNumber;

    private String country;
    private String city;

    @NotNull(message = "Date of birth cannot be null")
    @Past(message = "Date of birth must be in the past")  // Specify the date format pattern
    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private String zipcode;

    private String resetToken; // For password reset

    @JsonIgnore
    private String token;

    @JsonIgnore
    private LocalDateTime tokenStartDateTime;

    private List<Role> roles; // Set of roles for the user
    
    private String verifiedBy;

    private String profilePictureUrl;

    private boolean isActive=true;
    private boolean isVerified;

    // Additional fields for your user model if necessary...
}
