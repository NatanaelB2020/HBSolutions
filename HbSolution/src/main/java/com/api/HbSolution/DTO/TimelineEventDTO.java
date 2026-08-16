package com.api.HbSolution.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class TimelineEventDTO {
    private String tipo;
    private String descricao;
    private LocalDateTime data;
    private String status;
    private BigDecimal valor;
}
