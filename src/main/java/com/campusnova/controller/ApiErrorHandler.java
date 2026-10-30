package com.campusnova.controller;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/** Sends safe JSON errors to the chat client without exposing internal implementation details. */
@RestControllerAdvice public class ApiErrorHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> invalid(MethodArgumentNotValidException ignored){return response(HttpStatus.BAD_REQUEST,"Please enter a question before sending.");}
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,String>> unavailable(Exception ignored){return response(HttpStatus.SERVICE_UNAVAILABLE,"The information service is temporarily unavailable.");}
    private ResponseEntity<Map<String,String>> response(HttpStatus status,String message){Map<String,String> body=new LinkedHashMap<String,String>();body.put("error",message);return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(body);}
}
