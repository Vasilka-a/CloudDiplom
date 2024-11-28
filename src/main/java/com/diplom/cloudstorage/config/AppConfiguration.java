package com.diplom.cloudstorage.config;

import com.diplom.cloudstorage.repository.UserRepository;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

@Configuration
public class AppConfiguration {
    private final UserRepository userRepository;

    public AppConfiguration(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Bean
    UserDetailsService userDetailsService() {
        return username -> userRepository.findUsersByLogin(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
    }
}
