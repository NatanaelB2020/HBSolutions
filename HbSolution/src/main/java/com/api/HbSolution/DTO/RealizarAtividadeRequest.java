package com.api.HbSolution.DTO;

import java.time.LocalDateTime;

import com.api.HbSolution.enums.EtapaOportunidade;
import com.api.HbSolution.enums.StatusAtividade;

import lombok.Data;

@Data
public class RealizarAtividadeRequest {
    private String resultado;
    private Integer duracaoMinutos;
    private StatusAtividade status;
    private EtapaOportunidade proximaEtapaOportunidade;
    private String proximaAtividadeTitulo;
    private LocalDateTime proximaAtividadeData;
}
