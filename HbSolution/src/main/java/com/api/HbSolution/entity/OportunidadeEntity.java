package com.api.HbSolution.entity;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "oportunidade")
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OportunidadeEntity extends BaseEntity {

    @Column(name = "titulo", nullable = false, length = 255)
    private String titulo;

    @Column(name = "descricao", length = 1000)
    private String descricao;

    @Column(name = "etapa", nullable = false, length = 50)
    private String etapa;

    @Column(name = "status", nullable = false, length = 50)
    private String status;

    @Column(name = "valor", precision = 19, scale = 2)
    private BigDecimal valor;

    @Column(name = "probabilidade", precision = 5, scale = 2)
    private BigDecimal probabilidade;

    @Column(name = "data_fechamento")
    private LocalDate dataFechamento;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "lead_id")
    private LeadEntity lead;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "cliente_id")
    private ClienteEntity cliente;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "usuario_responsavel_id")
    private UsuarioEntity usuarioResponsavel;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "empresa_id", insertable = false, updatable = false)
    private EmpresaEntity empresa;

    @OneToMany(mappedBy = "oportunidade", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<AtividadeEntity> atividades;

    @OneToMany(mappedBy = "oportunidade", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<PedidoEntity> pedidos;
}
