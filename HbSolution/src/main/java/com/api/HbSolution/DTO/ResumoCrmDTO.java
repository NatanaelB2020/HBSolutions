package com.api.HbSolution.DTO;

import java.math.BigDecimal;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ResumoCrmDTO {
    private BigDecimal valorTotalPipeline;
    private BigDecimal valorTotalGanhoMes;
    private BigDecimal valorTotalPerdidoMes;
    private Long quantidadeOportunidadesAtivas;
    private Long quantidadeLeadsNovosMes;
    private Double taxaConversao;
}
