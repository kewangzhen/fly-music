package com.example.flymusic.config;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.http.HttpStatus;

import java.util.HashMap;
import java.util.Map;

@ControllerAdvice
public class GlobalExceptionHandler {

    @ExceptionHandler(Exception.class)
    @ResponseStatus(HttpStatus.INTERNAL_SERVER_ERROR)
    @ResponseBody
    public Map<String, Object> handleException(Exception e) {
        Map<String, Object> response = new HashMap<>();
        response.put("timestamp", System.currentTimeMillis());
        response.put("status", 500);
        response.put("error", e.getClass().getSimpleName());
        response.put("message", e.getMessage());
        
        Throwable cause = e.getCause();
        if (cause != null) {
            response.put("cause", cause.getClass().getSimpleName());
            response.put("causeMessage", cause.getMessage());
        }
        
        StackTraceElement[] stackTrace = e.getStackTrace();
        if (stackTrace.length > 0) {
            response.put("location", stackTrace[0].toString());
        }
        
        return response;
    }
}
