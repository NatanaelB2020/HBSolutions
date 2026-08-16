package com.api.HbSolution.DTO;

import java.math.BigDecimal;
import java.time.LocalDate;

import com.api.HbSolution.enums.EtapaOportunidade;
import com.api.HbSolution.enums.StatusOportunidade;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class OportunidadeDTO extends BaseDTO {
    private String titulo;
    private String descricao;
    private EtapaOportunidade etapa;
    private StatusOportunidade status;
    private BigDecimal valor;
    private Integer probabilidade;
    private LocalDate dataFechamentoEstimada;
    private LocalDate dataFechamentoReal;
    private String motivoPerda;
    private Boolean alertaAtivo;
}
