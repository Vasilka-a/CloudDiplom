package com.diplom.cloudstorage.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;

@AllArgsConstructor
@Data
public class AuthResponse {
    @JsonProperty("auth-token")
    private final String token;
}
