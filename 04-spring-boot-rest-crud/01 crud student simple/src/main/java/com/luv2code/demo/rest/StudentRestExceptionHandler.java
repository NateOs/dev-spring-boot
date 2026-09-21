package com.luv2code.demo.rest;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;


    @ControllerAdvice
    public class StudentRestExceptionHandler {
        @ExceptionHandler
        public ResponseEntity<StudentErrorResponse> handleException(StudentNotFoundException ex){
// create a StudentErrorResponse
            StudentErrorResponse error = new StudentErrorResponse();

            error.setStatus(HttpStatus.NOT_FOUND.value());
            error.setMessage(ex.getMessage());
            error.setTimestamp(System.currentTimeMillis());

//        return new ResponseEntity
            return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
        }

        // add a catchall exception handler
        @ExceptionHandler
        public ResponseEntity<StudentErrorResponse> handleException(Exception ex){
            return null;
        }
    }

