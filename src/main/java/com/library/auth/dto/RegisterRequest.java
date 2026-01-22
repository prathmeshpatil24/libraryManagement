package com.library.auth.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RegisterRequest {

    @NotBlank
    @Size(min = 2, max =50, message = "name must be required")
    private String name;

    @NotBlank(message = "Username is required")
    @Size(min = 3, max = 30, message = "Username must be between 3 and 30 characters")
    @Pattern(
            regexp = "^(?=.{3,30}$)(?![._])(?!.*[._]{2})[a-z0-9._]+(?<![._])$",
            message = "Username can contain lowercase letters, numbers, dots and underscores only"
    )
    private String username;


    @NotBlank(message = "Email must not be empty")
    @Email(message = "Invalid email format")
    private String email;

    @NotBlank(message = "Password must not be empty")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*\\d)(?=.*[@$!%*?&])[A-Za-z\\d@$!%*?&]{8,}$",
            message = "Password must have at least 8 characters, 1 uppercase letter, 1 number, and 1 special character"
    )
    private String password;

    @NotBlank(message = "Mobile number cannot be empty")
    @Pattern(
            regexp = "^(\\+\\d{1,3}[- ]?)?\\d{10}$",
            message = "Invalid mobile number"
    )
    private String mobileNo;
}
