package com.api.HbSolution.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "atividade")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AtividadeEntity extends BaseEntity {

    @Column(name = "titulo", nullable = false, length = 255)
    private String titulo;

    @Column(name = "tipo", nullable = false, length = 50)
    private String tipo;

    @Column(name = "descricao", length = 1000)
    private String descricao;

    @Column(name = "data_atividade", nullable = false)
    private LocalDateTime dataAtividade;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oportunidade_id", nullable = false)
    private OportunidadeEntity oportunidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_responsavel_id")
    private UsuarioEntity usuarioResponsavel;
}
