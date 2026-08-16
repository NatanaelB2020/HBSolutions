package com.api.HbSolution.DTO;

import java.math.BigDecimal;
import java.util.List;

import lombok.Data;

@Data
public class DashboardDTO {
    private List<AtividadeDTO> atividadesHoje;
    private List<AtividadeDTO> atividadesAtrasadas;
    private List<OportunidadeAlertaDTO> oportunidadesAlerta;
    private BigDecimal valorPipeline;
    private Long quantidadeLeadsNovosHoje;
    private Long quantidadeOportunidadesAtivas;
    private BigDecimal totalVendidoMes;
    private Double taxaConversaoMes;
}
