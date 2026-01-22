package com.library.auth.controller;

import com.library.auth.dto.RegisterRequest;
import com.library.auth.service.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/internal/auth")
public class AdminAuthController {


    private final AuthServiceImpl authService;

    @PostMapping("/register-admin")
    public ResponseEntity<?>registerAdmin(@Valid @RequestBody RegisterRequest request) throws BadRequestException {

        Map<String, Object> registerAdmin = authService.registerAdmin(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "message", registerAdmin.get("message"),
                        "email", ((String) registerAdmin.get("email")),
                        "timestamp", registerAdmin.get("timestamp")
                ));

    }

}
