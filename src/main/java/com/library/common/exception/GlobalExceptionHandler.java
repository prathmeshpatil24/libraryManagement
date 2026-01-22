package com.library.common.exception;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.NoHandlerFoundException;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentials(BadCredentialsException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "status", HttpStatus.UNAUTHORIZED.value(),
                        "message", "Invalid username or password"
                ));
    }

    @ExceptionHandler(AuthenticationException.class)
    public ResponseEntity<?>handleAuthenticationException(AuthenticationException ex){
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "message", ex.getMessage(),
                        "timestamp", LocalDateTime.now()
                ));
    }

//    @ExceptionHandler(ConflictException.class)
//    public ResponseEntity<?>handleConflictException(ConflictException ex){
//        return ResponseEntity.status(HttpStatus.CONFLICT)
//                .body(Map.of(
//                        "status", HttpStatus.CONFLICT.value(),
//                        "message", ex.getMessage(),
//                        "timestamp", LocalDateTime.now()
//                ));
//    }

    @ExceptionHandler(NoHandlerFoundException.class)
    public ResponseEntity<?> handleNoHandlerFound(NoHandlerFoundException ex) {

        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(Map.of(
                        "status", 404,
                        "error", "Not Found",
                        "message", "API endpoint not found",
                        "path", ex.getRequestURL()
                ));
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    @ResponseStatus(HttpStatus.BAD_REQUEST)
    public ResponseEntity<?> handleValidationExceptions(MethodArgumentNotValidException ex) {
        Map<String, String> errors = new HashMap<>();

        ex.getBindingResult().getFieldErrors().forEach(error ->
                errors.put(error.getField(), error.getDefaultMessage())
        );

        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                        "error", errors,
                        "status", HttpStatus.BAD_REQUEST.value(),
                        "timeStamp", LocalDateTime.now()
                ));
    }

//    @ExceptionHandler(DataIntegrityViolationException.class)
//    public ResponseEntity<?> handleDuplicateData(DataIntegrityViolationException ex) {
//        return ResponseEntity.status(HttpStatus.CONFLICT)
//                .body(Map.of(
//                       "status" ,HttpStatus.CONFLICT.value(),
//                       "error", "Duplicate/Invalid Data",
//                       "message", ex.getMessage(),
//                       "timestamp", LocalDateTime.now()
//                ));
//    }
//
    @ExceptionHandler(SaveFailedException.class)
    public ResponseEntity<?> handleSaveFailed(SaveFailedException ex) {
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(Map.of(
                       "status", HttpStatus.BAD_REQUEST.value(),
                       "error", "Failed to Save",
                        "message", ex.getMessage(),
                     "timestamp", LocalDateTime.now()
                ));
    }

//    @ExceptionHandler(InActiveCategoryException.class)
//    public ResponseEntity<?> handleInActiveCategoryException(InActiveCategoryException ex) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(Map.of(
//                        "status", HttpStatus.BAD_REQUEST.value(),
//                        "error", "Failed to Save",
//                        "message", ex.getMessage(),
//                        "timestamp", LocalDateTime.now()
//                ));
//    }
//
//    @ExceptionHandler(InvalidPaginationParameterException.class)
//    public ResponseEntity<?> handleInvalidPaginationException(InvalidPaginationParameterException ex) {
//        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
//                .body(Map.of(
//                        "status", HttpStatus.BAD_REQUEST.value(),
//                        "error", "Failed to retried",
//                        "message", ex.getMessage(),
//                        "timestamp", LocalDateTime.now()
//                ));
//    }
//


    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<?> handleUserNotFoundException(UserNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                .body(Map.of(
                        "status", HttpStatus.UNAUTHORIZED.value(),
                        "error", "Invalid username or password",
                        "message", ex.getMessage(),
                        "timestamp", LocalDateTime.now()
                ));
    }


    @ExceptionHandler(EmailException.class)
    public ResponseEntity<?> handleEmailException(EmailException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "status", HttpStatus.CONFLICT.value(),
                        "error", "Email error",
                        "message", ex.getMessage(),
                        "timestamp", LocalDateTime.now()
                ));
    }

    @ExceptionHandler(MobileNoException.class)
    public ResponseEntity<?> handleMobileNoException(MobileNoException ex) {
        return ResponseEntity.status(HttpStatus.CONFLICT)
                .body(Map.of(
                        "status", HttpStatus.CONFLICT.value(),
                        "error", "Mobile Number Error",
                        "message", ex.getMessage(),
                        "timestamp", LocalDateTime.now()
                ));
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<?> handleGlobal(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(Map.of(
                       "status", HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "error", "Internal Server Error",
                        "message", ex.getMessage(),
                      "timestamp", LocalDateTime.now()
                ));
    }


}
