package com.api.HbSolution.DTO;

import java.time.LocalDateTime;

import com.api.HbSolution.enums.StatusAtividade;
import com.api.HbSolution.enums.TipoAtividade;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AtividadeDTO extends BaseDTO {
    private String titulo;
    private TipoAtividade tipo;
    private String descricao;
    private StatusAtividade status;
    private LocalDateTime dataAgendamento;
    private Integer duracaoMinutos;
    private String resultado;
}
