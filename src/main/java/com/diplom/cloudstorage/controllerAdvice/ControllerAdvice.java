package com.diplom.cloudstorage.controllerAdvice;

import com.diplom.cloudstorage.exceptions.CrudExceptions;
import com.diplom.cloudstorage.exceptions.InputDataException;
import com.diplom.cloudstorage.exceptions.UnauthorizedException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ExceptionHandler;

@Slf4j
@org.springframework.web.bind.annotation.ControllerAdvice
public class ControllerAdvice {
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<String> handlerBadCredentials(BadCredentialsException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(CrudExceptions.class)
    public ResponseEntity<String> handlerTransferOrConfirmation(CrudExceptions e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(UnauthorizedException.class)
    public ResponseEntity<String> handlerUnauthorized(UnauthorizedException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.UNAUTHORIZED);
    }

    @ExceptionHandler(InputDataException.class)
    public ResponseEntity<String> handlerInputData(InputDataException e) {
        log.error(e.getMessage());
        return new ResponseEntity<>(e.getMessage(), HttpStatus.BAD_REQUEST);
    }
}

