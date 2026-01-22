package com.library.auth.controller;

import com.library.auth.dto.*;
import com.library.auth.service.AuthServiceImpl;
import com.library.user.entity.Users;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {


    private final AuthServiceImpl authService;


    @PostMapping("/register-user")
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody RegisterRequest dto) throws BadRequestException {

        Users user = authService.registerUser(dto);

        return ResponseEntity.ok(
                Map.of(
                        "message", "User registered successfully! Please check your email for verification link.",
                        "email", user.getEmail(),
                        "timestamp", LocalDateTime.now()
                )
        );
    }


    @GetMapping("/verify")
    public ResponseEntity<?> verifyEmail(
            @RequestParam String email,
            @RequestParam String code
    ) throws BadRequestException {

        String response = authService.verifyLink(email, code);

        return ResponseEntity.ok(
                Map.of(
                        "message", response,
                        "email", email,
                        "timestamp", LocalDateTime.now()
                )
        );
    }

    @PostMapping(value = "/login")
    public ResponseEntity<?> login(@RequestBody LoginRequest request) {

        LoginResponse loginResponse = authService.login(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "status", HttpStatus.OK.value(),
                        "message", "Login Successfully",
                        "data", loginResponse
                ));
    }


    @PostMapping("/forget-password")
    public ResponseEntity<?> forgetPassword(@RequestBody ForgotPasswordRequest request) {

        authService.forgetPassword(request.getEmail());

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "status", HttpStatus.OK.value(),
                        "message", "Please Check Mail"
                ));
    }

    @PostMapping("/resetForgetPassword")
    public ResponseEntity<?> forgetPasswordReset(@Valid @RequestBody ResetPasswordRequest resetPasswordRequest) {

        String response = authService.forgetPasswordReset(resetPasswordRequest.getToken(),
                resetPasswordRequest.getNewPassword());

        return ResponseEntity.ok(
                Map.of(
                        "message", response,
                        "status", HttpStatus.OK.value(),
                                "timestamp", LocalDateTime.now()
                )
        );
    }
}
