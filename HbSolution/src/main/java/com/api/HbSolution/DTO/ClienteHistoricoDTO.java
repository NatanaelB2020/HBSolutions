package com.api.HbSolution.DTO;

import java.util.List;

import lombok.Data;

@Data
public class ClienteHistoricoDTO {
    private ClienteDTO cliente;
    private List<LeadResumoDTO> leads;
    private List<OportunidadeResumoDTO> oportunidades;
    private List<AtividadeResumoDTO> atividades;
    private List<PedidoResumoDTO> pedidos;
    private List<TimelineEventDTO> timeline;
}
