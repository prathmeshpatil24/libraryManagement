package com.library.auth.controller;

import com.library.auth.dto.RegisterRequest;
import com.library.auth.service.AuthServiceImpl;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@AllArgsConstructor
@RestController
@RequestMapping("/api/librarian/auth")
public class LibrarianAuthController {

    private final AuthServiceImpl authService;

    @PostMapping("/register-librarian")
    public ResponseEntity<?> registerLibrarian(@Valid RegisterRequest request) throws BadRequestException {

        Map<String, Object> registerLibrarian = authService.registerLibrarian(request);

        return ResponseEntity.status(HttpStatus.OK)
                .body(Map.of(
                        "message", registerLibrarian.get("message"),
//                        "email", ((String) registerLibrarian.get("email")),
                        "timestamp", registerLibrarian.get("timestamp")
                ));
    }
}
