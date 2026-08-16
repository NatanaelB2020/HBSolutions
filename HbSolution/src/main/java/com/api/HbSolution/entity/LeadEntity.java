package com.api.HbSolution.entity;

import java.time.LocalDateTime;

import com.api.HbSolution.enums.OrigemLead;
import com.api.HbSolution.enums.StatusLead;

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
@Table(name = "lead")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LeadEntity extends BaseEntity {

    @Column(name = "nome", nullable = false, length = 255)
    private String nome;

    @Column(name = "email", length = 255)
    private String email;

    @Column(name = "telefone", length = 20)
    private String telefone;

    @Enumerated(EnumType.STRING)
    @Column(name = "origem", nullable = false, length = 50)
    private OrigemLead origem;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false, length = 50)
    private StatusLead status;

    @Column(name = "score")
    private Integer score;

    @Column(name = "observacao", length = 500)
    private String observacao;

    @Column(name = "data_conversao")
    private LocalDateTime dataConversao;

    @Column(name = "motivo_desqualificacao", length = 500)
    private String motivoDesqualificacao;

    @Column(name = "lgpd_consentimento")
    private Boolean lgpdConsentimento;

    @Column(name = "lgpd_consentimento_data")
    private LocalDateTime lgpdConsentimentoData;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", insertable = false, updatable = false)
    private EmpresaEntity empresa;
}
