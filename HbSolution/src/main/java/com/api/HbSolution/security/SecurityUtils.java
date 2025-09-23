package com.api.HbSolution.security;

import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import com.api.HbSolution.entity.UsuarioEntity;

@Component
public class SecurityUtils {

    public static UsuarioEntity getUsuarioLogado() {
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        if (principal instanceof UsuarioDetails) {
            return ((UsuarioDetails) principal).getUsuario();
        }
        throw new RuntimeException("Usuário não está logado");
    }

}