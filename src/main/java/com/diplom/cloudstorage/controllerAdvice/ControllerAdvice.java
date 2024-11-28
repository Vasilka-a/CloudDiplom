package com.diplom.cloudstorage.controllerAdvice;

import com.diplom.cloudstorage.dtos.Error;
import com.diplom.cloudstorage.exceptions.BadCredentialsException;
import com.diplom.cloudstorage.exceptions.CrudExceptions;
import com.diplom.cloudstorage.exceptions.InputDataException;
import com.diplom.cloudstorage.exceptions.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<Error> handlerBadCredentials(BadCredentialsException e) {
        Error error = new Error(e.getMessage());
        log.error("Error message: {} ", e.getMessage());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }

    @ExceptionHandler(CrudExceptions.class)
    public ResponseEntity<Error> handlerCrud(CrudExceptions e) {
        Error error = new Error(e.getMessage(), e.getId());
        log.error("Error message: {}, User id: = {}", e.getMessage(), e.getId());
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(error);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<Error> handlerUnauthorized(UnauthorizedException e) {
        Error error = new Error(e.getMessage());
        log.error("Error message: {}", e.getMessage());
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(error);
    }

    @ExceptionHandler(InputDataException.class)
    public ResponseEntity<Error> handlerInputData(InputDataException e) {
        Error error = new Error(e.getMessage(), e.getId());
        log.error("Error message: {}, User id: = {}", e.getMessage(), e.getId());
        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(error);
    }
}

