package com.api.HbSolution.DTO;

import java.math.BigDecimal;

import com.api.HbSolution.enums.EtapaOportunidade;
import com.api.HbSolution.enums.StatusOportunidade;

import lombok.Data;

@Data
public class OportunidadeResumoDTO {
    private Long id;
    private String titulo;
    private EtapaOportunidade etapa;
    private StatusOportunidade status;
    private BigDecimal valor;
}
