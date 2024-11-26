package com.diplom.cloudstorage.controller;

import com.diplom.cloudstorage.dtos.AuthRequest;
import com.diplom.cloudstorage.dtos.AuthResponse;
import com.diplom.cloudstorage.service.AuthenticationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class AuthenticationController {

    private final AuthenticationService authenticationService;

    @Autowired
    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest authRequest) {
        AuthResponse authResponse = authenticationService.login(authRequest);
        return ResponseEntity.ok(authResponse);
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logout(@RequestHeader("auth-token") String authToken) {
        authenticationService.logout(authToken);
        return ResponseEntity.ok("Logout successful");
    }
}

