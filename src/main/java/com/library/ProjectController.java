package com.library;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDateTime;
import java.util.Map;

@RestController
@RequestMapping("/api")
public class ProjectController {


    @GetMapping("/greet")
    public ResponseEntity<?>greetMessage(){
        return ResponseEntity
                .status(HttpStatus.OK)
                .body(Map.of(
                        "status", HttpStatus.OK.value(),
                        "message", "Welcome to Project Management API",
                        "timestamp", LocalDateTime.now()
                ));
    }
}
