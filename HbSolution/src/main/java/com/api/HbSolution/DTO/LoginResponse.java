package com.api.HbSolution.DTO;

import lombok.Data;

@Data
public class LoginResponse {

    private String token;

    public LoginResponse() {
    }

    // Construtor para inicializar com o token
    public LoginResponse(String token) {
        this.token = token;
    }
}
