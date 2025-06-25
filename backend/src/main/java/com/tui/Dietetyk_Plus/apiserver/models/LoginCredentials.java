package com.tui.Dietetyk_Plus.apiserver.models;

import lombok.AllArgsConstructor;
import lombok.Data;

import java.io.Serializable;

@Data
@AllArgsConstructor
public class LoginCredentials implements Serializable {
    private String email;
    private String password;
}