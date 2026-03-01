package com.yash.chat_app.exception;

import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class GlobalExceptionHandler {
    @ExceptionHandler(UserNotFoundException.class)
    public ResponseEntity<String> handleUserNotFound(UserNotFoundException ex) {
        return ResponseEntity.status(404).body(ex.getMessage());
    }

    // 🔹 409 - Duplicate request
    @ExceptionHandler(DuplicateRequestException.class)
    public ResponseEntity<String> handleDuplicate(DuplicateRequestException ex) {
        return ResponseEntity.status(409).body(ex.getMessage());
    }

    // 🔹 401 - JWT invalid
    @ExceptionHandler(JwtInvalidException.class)
    public ResponseEntity<String> handleJwt(JwtInvalidException ex) {
        return ResponseEntity.status(401).body(ex.getMessage());
    }

//     🔹 400 - Bad request
    @ExceptionHandler(BadRequestException.class)
    public ResponseEntity<String> handleBadRequest(BadRequestException ex) {
        return ResponseEntity.status(400).body(ex.getMessage());
    }

    // 🔹 Catch ALL runtime exceptions
    @ExceptionHandler(RuntimeException.class)
    public ResponseEntity<String> handleRuntime(RuntimeException ex) {
        return ResponseEntity.status(500).body(ex.getMessage());
    }

    // 🔹 Catch ALL unknown exceptions
    @ExceptionHandler(Exception.class)
    public ResponseEntity<String> handleGeneral(Exception ex) {
        return ResponseEntity.status(500).body("Something went wrong");
    }
}

