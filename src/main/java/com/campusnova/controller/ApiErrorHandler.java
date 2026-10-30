package com.campusnova.controller;

import org.springframework.http.*;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.*;
import java.util.*;

/** Keeps API failures machine-readable without revealing implementation or credentials. */
@RestControllerAdvice public class ApiErrorHandler {
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<Map<String,String>> invalidRequest(MethodArgumentNotValidException ignored){return error(HttpStatus.BAD_REQUEST,"Please enter a question before sending.");}
    @ExceptionHandler(Exception.class)
    public ResponseEntity<Map<String,String>> unavailable(Exception ignored){return error(HttpStatus.SERVICE_UNAVAILABLE,"The information service is temporarily unavailable.");}
    private ResponseEntity<Map<String,String>> error(HttpStatus status,String message){Map<String,String> body=new LinkedHashMap<String,String>();body.put("error",message);return ResponseEntity.status(status).contentType(MediaType.APPLICATION_JSON).body(body);}
}
