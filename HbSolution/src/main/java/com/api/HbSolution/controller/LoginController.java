package com.api.HbSolution.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.api.HbSolution.DTO.LoginRequestDTO;
import com.api.HbSolution.entity.UsuarioEntity;
import com.api.HbSolution.service.UsuarioService;

@RestController
@RequestMapping("/api")
public class LoginController {

    private final UsuarioService usuarioService;

    public LoginController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/login")
    public ResponseEntity<String> login(@RequestBody LoginRequestDTO loginRequest) {
        try {
            UsuarioEntity usuario = usuarioService.authenticate(loginRequest.getEmail(), loginRequest.getSenha());
            return ResponseEntity.ok("Autenticado com sucesso! Bem-vindo, " + usuario.getEmail());
        } catch (IllegalArgumentException e) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Credenciais inválidas");
        }
    }
}
