package com.api.HbSolution.DTO;

import java.math.BigDecimal;

import com.api.HbSolution.enums.StatusOportunidade;

import lombok.Data;

@Data
public class FecharOportunidadeRequest {
    private StatusOportunidade status;
    private String motivo;
    private BigDecimal valorFinal;
}
