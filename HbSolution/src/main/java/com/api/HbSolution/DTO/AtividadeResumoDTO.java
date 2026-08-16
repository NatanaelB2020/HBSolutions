package com.api.HbSolution.DTO;

import java.time.LocalDateTime;

import com.api.HbSolution.enums.StatusAtividade;
import com.api.HbSolution.enums.TipoAtividade;

import lombok.Data;

@Data
public class AtividadeResumoDTO {
    private Long id;
    private String titulo;
    private TipoAtividade tipo;
    private StatusAtividade status;
    private LocalDateTime dataAgendamento;
    private String resultado;
}
