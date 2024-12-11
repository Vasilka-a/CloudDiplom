package com.diplom.cloudstorage.service;

import com.diplom.cloudstorage.dtos.AuthResponse;
import com.diplom.cloudstorage.entity.User;
import com.diplom.cloudstorage.exceptions.UnauthorizedException;
import com.diplom.cloudstorage.jwt.JwtUtils;
import com.diplom.cloudstorage.jwt.TokenBlacklistService;
import com.diplom.cloudstorage.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final TokenBlacklistService tokenBlacklistService;

    public AuthResponse login(String login) {
        final User user = userRepository.findUsersByLogin(login)
                .orElseThrow(() -> new UnauthorizedException("Unauthorized error"));

        final String jwt = jwtUtils.generateToken(user);
        return new AuthResponse(jwt);
    }

    public void logout(String authToken) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = userRepository.findUsersByLogin(authentication.getName())
                .orElseThrow(() -> new UnauthorizedException("Unauthorized error"));
        if (user != null) {
            tokenBlacklistService.addToBlacklist(authToken);
            SecurityContextHolder.clearContext();
        }
    }
}
