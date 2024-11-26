package com.diplom.cloudstorage.service;

import com.diplom.cloudstorage.dtos.AuthRequest;
import com.diplom.cloudstorage.dtos.AuthResponse;
import com.diplom.cloudstorage.entity.User;
import com.diplom.cloudstorage.exceptions.UnauthorizedException;
import com.diplom.cloudstorage.jwt.JwtUtils;
import com.diplom.cloudstorage.repository.UserRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class AuthenticationService {
    private final UserRepository userRepository;
    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;

    public AuthResponse login(AuthRequest input) {
        try {
            this.authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(input.getLogin(), input.getPassword()));
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Bad credentials");
        }
        final User user = userRepository.findUsersByLogin(input.getLogin())
                .orElseThrow(() -> new BadCredentialsException("Bad credentials"));

        final String jwt = jwtUtils.generateToken(user);
        return new AuthResponse(jwt);
    }

    public void logout(String authToken) {
        if (isUserAuthenticated(authToken)) {
            SecurityContextHolder.clearContext();
        }
    }

    public boolean isUserAuthenticated(String authToken) {
        if (authToken.startsWith("Bearer ")) {
            String tokenWithoutBearer = authToken.substring(7);
            String username = jwtUtils.extractUsername(tokenWithoutBearer);
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User user = userRepository.findUsersByLogin(username).orElse(null);
            if (authentication.isAuthenticated() && authentication.getPrincipal().equals(user)) {
                System.out.println(authentication);
                return true;
            }
        }
        throw new UnauthorizedException("Unauthorized error");
    }
}
