package com.library.auth.controller;

import com.library.auth.dto.*;
import com.library.auth.service.AuthServiceImpl;
import com.library.user.entity.Users;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.client.registration.InMemoryClientRegistrationRepository;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/auth")
public class AuthController {


    private final AuthServiceImpl authService;
    private final ClientRegistrationRepository clientRegistrationRepository;


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

    @GetMapping("/oauth2-providers")
    public ResponseEntity<?>listOfOAuth2(){

        List<Map<String, String>> providers = new ArrayList<>();

        if (clientRegistrationRepository instanceof InMemoryClientRegistrationRepository repo) {

            for (ClientRegistration registration : repo) {

                Map<String, String> provider = new HashMap<>();
                provider.put("name", registration.getClientName());
                provider.put(
                        "authorizationUrl",
                        "/oauth2/authorization/" + registration.getRegistrationId()
                );

                providers.add(provider);
            }
        }

        providers.forEach(x-> System.out.println("OAuth2 Provider: " + x));

        return ResponseEntity.ok(
                Map.of(
                        "status", HttpStatus.OK.value(),
                        "message", "List of OAuth2 Providers",
                        "data", providers
                )
        );
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
