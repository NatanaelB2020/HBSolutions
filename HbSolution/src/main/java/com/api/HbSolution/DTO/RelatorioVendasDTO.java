package com.api.HbSolution.DTO;

import java.math.BigDecimal;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RelatorioVendasDTO {
    private BigDecimal totalVendido;
    private Long quantidadeVendas;
    private BigDecimal ticketMedio;
    private Long quantidadePerdidas;
    private BigDecimal totalPerdido;
    private Double taxaConversaoPeriodo;
    private List<VendaPorDiaDTO> vendasPorDia;
}
