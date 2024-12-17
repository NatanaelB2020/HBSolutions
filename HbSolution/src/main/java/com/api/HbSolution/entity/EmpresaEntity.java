package com.api.HbSolution.entity;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "empresa")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class EmpresaEntity extends BaseEntity {

    @Column(name = "nome_fantasia", nullable = false, length = 255)
    private String nomeFantasia;

    @Column(name = "cnpj", unique = true, nullable = false, length = 18)
    private String cnpj;

    @Column(name = "telefone", length = 15)
    private String telefone;

    @OneToOne(cascade = CascadeType.ALL)
    @JoinColumn(name = "id_endereco", referencedColumnName = "id")
    private EnderecoEntity endereco;
}
