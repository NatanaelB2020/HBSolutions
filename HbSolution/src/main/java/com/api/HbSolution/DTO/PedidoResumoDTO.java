package com.api.HbSolution.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.Data;

@Data
public class PedidoResumoDTO {
    private Long id;
    private LocalDateTime dataPedido;
    private BigDecimal valorTotal;
    private String status;
}
