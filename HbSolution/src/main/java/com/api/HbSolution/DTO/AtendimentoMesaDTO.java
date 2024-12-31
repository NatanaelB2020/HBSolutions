package com.api.HbSolution.DTO;

import com.api.HbSolution.enums.StatusAtendimento;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class AtendimentoMesaDTO {

    private Long id;
    private Long empresaId;
    private Integer numeroMesa;
    private StatusAtendimento statusAtendimento;
    private Long pedidoId;
    private LocalDateTime dataAbertura;
    private LocalDateTime dataFechamento;
    private String observacao;
}
