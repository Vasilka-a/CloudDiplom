package com.diplom.cloudstorage.dtos;

import lombok.Data;

@Data
public class AuthRequest {
    private String login;
    private String password;
}
