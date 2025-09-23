package com.api.HbSolution.entity;

import java.io.Serializable;
import java.time.LocalDateTime;

import com.api.HbSolution.security.SecurityUtils;

import jakarta.persistence.*;
import lombok.Data;

@MappedSuperclass
@Data
public abstract class BaseEntity implements Serializable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "created_at", nullable = false, updatable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

    @Column(name = "updated_at", nullable = false)
    private LocalDateTime updatedAt = LocalDateTime.now();

    @Column(name = "empresa_id", nullable = false, updatable = false)
    private Long empresaId;

    @Column(name = "usuario_id", nullable = false, updatable = false)
    private Long usuarioId;

    @Column(name = "ativo", nullable = false)
    private Boolean ativo = true; // para exclusão lógica

    @PrePersist
    private void onCreate() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();

        var usuarioLogado = SecurityUtils.getUsuarioLogado();
        if (usuarioLogado != null) {
            this.usuarioId = usuarioLogado.getId();
            this.empresaId = usuarioLogado.getEmpresaId();
        }
        this.ativo = true;
    }

    @PreUpdate
    private void onUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
}
