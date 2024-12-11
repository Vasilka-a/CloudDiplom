package com.diplom.cloudstorage.controller;

import com.diplom.cloudstorage.dtos.AuthRequest;
import com.diplom.cloudstorage.dtos.AuthResponse;
import com.diplom.cloudstorage.exceptions.BadCredentialsException;
import com.diplom.cloudstorage.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
public class AuthenticationController {
    private final AuthenticationService authenticationService;
    private final AuthenticationManager authenticationManager;

    public AuthenticationController(AuthenticationService authenticationService, AuthenticationManager authenticationManager) {
        this.authenticationService = authenticationService;
        this.authenticationManager = authenticationManager;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest authRequest) {
        Authentication authentication;
        try {
            authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(authRequest.getLogin(), authRequest.getPassword()));
        } catch (AuthenticationException e) {
            throw new BadCredentialsException("Bad credentials");
        }
        AuthResponse authResponse = authenticationService.login(authentication.getName());
        log.info("Login successful for user: {}", authRequest.getLogin());
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("auth-token") String authToken) {
        authenticationService.logout(authToken);
        log.info("Logout successful for token: {}", authToken);
        return ResponseEntity.ok("Logout successful");
    }
}

