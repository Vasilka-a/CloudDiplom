package com.diplom.cloudstorage.dtos;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@AllArgsConstructor
@Data
@Builder
@NoArgsConstructor(force = true)
public class AuthResponse {
    @JsonProperty("auth-token")
    private final String token;
}
