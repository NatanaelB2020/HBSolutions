package com.api.HbSolution.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;

@Entity
@Table(name = "cliente")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ClienteEntity extends BaseEntity implements UsuarioAuditable {

    @Column(name = "nome", nullable = false, length = 255)
    private String nome;

    @Column(name = "cpf", unique = true, nullable = false, length = 11)
    private String cpf;

    @Column(name = "telefone", length = 15)
    private String telefone;

    @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
    @JoinColumn(name = "id_endereco", referencedColumnName = "id")
    private EnderecoEntity endereco;

    // Transient para evitar conflito de coluna
    @Transient
    private UsuarioEntity usuario;

    @Override
    public void setUsuario(UsuarioEntity usuario) {
        this.usuario = usuario;
        if (usuario != null) {
            this.setUsuarioId(usuario.getId()); // popula o BaseEntity
        }
    }

    @Override
    public UsuarioEntity getUsuario() {
        return this.usuario;
    }
}
