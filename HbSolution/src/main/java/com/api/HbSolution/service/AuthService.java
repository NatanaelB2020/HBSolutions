package com.api.HbSolution.service;

import com.api.HbSolution.DTO.LoginRequestDTO;
import com.api.HbSolution.entity.UsuarioEntity;
import com.api.HbSolution.enums.StatusAtivo;
import com.api.HbSolution.repository.UsuarioRepository;
import com.api.HbSolution.security.JwtUtil;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

@Service
public class AuthService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(UsuarioRepository usuarioRepository,
                       PasswordEncoder passwordEncoder,
                       JwtUtil jwtUtil) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    public ResponseEntity<?> login(LoginRequestDTO loginRequest) {
        String email = loginRequest.getEmail();
        String senha = loginRequest.getSenha();

        UsuarioEntity usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("Usuário não encontrado"));

        if (usuario.getAtivo() == StatusAtivo.INATIVO) {
            return ResponseEntity.status(401).body("Usuário inativo");
        }

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            return ResponseEntity.status(401).body("Senha incorreta");
        }

        String token = jwtUtil.generateToken(usuario.getEmail());

        Map<String, String> response = new HashMap<>();
        response.put("token", token);
        response.put("usuario", usuario.getNome());
        response.put("role", usuario.getRole());

        return ResponseEntity.ok(response);
    }
}
