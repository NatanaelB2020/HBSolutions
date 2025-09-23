package com.api.HbSolution.entity;

import jakarta.persistence.*;
import lombok.*;
import java.time.LocalDateTime;
import com.api.HbSolution.enums.StatusAtendimento;

@Entity
@Table(name = "atendimento_mesa")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@EqualsAndHashCode(callSuper = true)
public class AtendimentoMesaEntity extends BaseEntity implements UsuarioAuditable {

    @Column(name = "numero_mesa", nullable = false)
    private Integer numeroMesa;

    @Enumerated(EnumType.STRING)
    @Column(name = "status_atendimento", nullable = false)
    private StatusAtendimento statusAtendimento;

    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "pedido_id", nullable = false)
    private PedidoEntity pedido;

    @Column(name = "data_abertura", nullable = false)
    private LocalDateTime dataAbertura;

    @Column(name = "data_fechamento")
    private LocalDateTime dataFechamento;

    @Column(name = "observacao", length = 500)
    private String observacao;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_id", insertable = false, updatable = false)
    private UsuarioEntity usuario;

    @Override
    public void setUsuario(UsuarioEntity usuario) {
        this.usuario = usuario;
    }

    @Override
    public UsuarioEntity getUsuario() {
        return this.usuario;
    }
}
