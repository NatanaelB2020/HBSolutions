package com.api.HbSolution.entity;

import java.time.LocalDateTime;

import com.api.HbSolution.enums.StatusAtividade;
import com.api.HbSolution.enums.TipoAtividade;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
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

    @Enumerated(EnumType.STRING)
    @Column(name = "tipo", nullable = false, length = 50)
    private TipoAtividade tipo;

    @Column(name = "descricao", length = 1000)
    private String descricao;

    @Column(name = "data_atividade", nullable = false)
    private LocalDateTime dataAtividade;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private StatusAtividade status;

    @Column(name = "resultado", length = 1000)
    private String resultado;

    @Column(name = "data_agendamento")
    private LocalDateTime dataAgendamento;

    @Column(name = "duracao_minutos")
    private Integer duracaoMinutos;

    @Column(name = "notificacao_enviada")
    private Boolean notificacaoEnviada;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "oportunidade_id", nullable = false)
    private OportunidadeEntity oportunidade;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_responsavel_id")
    private UsuarioEntity usuarioResponsavel;
}
