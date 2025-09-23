package com.api.HbSolution.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;

@Entity
@Table(name = "item_pedido")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class ItemPedidoEntity extends BaseEntity implements UsuarioAuditable {

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoEntity pedido;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "produto_id", nullable = false)
    private ProdutoEntity produto;

    @Column(name = "quantidade", nullable = false)
    private Integer quantidade;

    @Column(name = "preco_unitario", nullable = false)
    private BigDecimal precoUnitario;

    @Column(name = "preco_total", nullable = false)
    private BigDecimal precoTotal;

    @Column(name = "observacao", length = 500)
    private String observacao;

    // Mapeia o usuário, mas não tenta gerar a coluna novamente
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    private UsuarioEntity usuario;

    @Override
    public void setUsuario(UsuarioEntity usuario) {
        this.usuario = usuario;
        if (usuario != null) {
            this.setUsuarioId(usuario.getId()); // seta no BaseEntity
        }
    }

    @Override
    public UsuarioEntity getUsuario() {
        return this.usuario;
    }
}
