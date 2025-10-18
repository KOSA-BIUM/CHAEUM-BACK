package com.bium.chaeum.presentation;

import com.bium.chaeum.domain.shared.error.ProfileNotFoundException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.util.Map;

@RestControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(ProfileNotFoundException.class)
    @ResponseStatus(HttpStatus.NOT_FOUND)
    public Map<String,Object> handleProfileNotFound(ProfileNotFoundException ex) {
        return Map.of("error", "profile_not_found", "message", ex.getMessage());
    }
}
