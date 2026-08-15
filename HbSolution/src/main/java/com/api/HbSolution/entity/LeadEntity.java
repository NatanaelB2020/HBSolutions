package com.api.HbSolution.entity;

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

    @Column(name = "origem", length = 100)
    private String origem;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "score")
    private Integer score;

    @Column(name = "observacao", length = 500)
    private String observacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", insertable = false, updatable = false)
    private EmpresaEntity empresa;
}
