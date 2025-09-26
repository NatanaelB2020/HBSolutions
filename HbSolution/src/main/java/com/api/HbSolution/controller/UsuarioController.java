package com.api.HbSolution.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import com.api.HbSolution.entity.UsuarioEntity;
import com.api.HbSolution.enums.StatusAtivo;
import com.api.HbSolution.service.UsuarioService;

@RestController
@RequestMapping("/usuarios")
public class UsuarioController extends BaseController<UsuarioEntity, UsuarioService> {

    private UsuarioService usuarioService;

    @PutMapping("/{id}/status")
    public ResponseEntity<UsuarioEntity> atualizarStatus(
            @PathVariable Long id,
            @RequestParam StatusAtivo status) {

        UsuarioEntity usuario = usuarioService.atualizarStatusUsuario(id, status);
        return ResponseEntity.ok(usuario);
    }

}
