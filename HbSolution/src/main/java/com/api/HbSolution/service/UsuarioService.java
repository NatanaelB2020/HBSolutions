package com.api.HbSolution.service;

import org.springframework.stereotype.Service;
import com.api.HbSolution.entity.UsuarioEntity;
import com.api.HbSolution.enums.StatusAtivo;
import com.api.HbSolution.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@Service
public class UsuarioService extends BaseService<UsuarioEntity> {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;

    public UsuarioService(UsuarioRepository usuarioRepository, PasswordEncoder passwordEncoder) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    public UsuarioEntity save(UsuarioEntity usuario) {
        if (!usuario.getSenha().startsWith("$2a$")) { // Verifica se já está criptografada
            usuario.setSenha(passwordEncoder.encode(usuario.getSenha()));
        }
        return usuarioRepository.save(usuario);
    }

    public UsuarioEntity authenticate(String email, String senha) {
        UsuarioEntity usuario = usuarioRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

        if (!passwordEncoder.matches(senha, usuario.getSenha())) {
            throw new IllegalArgumentException("Senha inválida");
        }

        return usuario;
    }

     public UsuarioEntity atualizarStatusUsuario(Long id, StatusAtivo status) {
    UsuarioEntity usuario = usuarioRepository.findById(id)
            .orElseThrow(() -> new IllegalArgumentException("Usuário não encontrado"));

    usuario.setAtivo(status);
    return usuarioRepository.save(usuario);
}
}
