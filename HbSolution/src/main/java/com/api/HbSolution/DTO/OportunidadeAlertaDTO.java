package com.api.HbSolution.DTO;

import java.math.BigDecimal;
import java.time.LocalDateTime;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OportunidadeAlertaDTO {
    private Long oportunidadeId;
    private String titulo;
    private String etapa;
    private BigDecimal valor;
    private Integer diasParada;
    private String ultimaAtividadeTitulo;
    private LocalDateTime dataUltimaAtividade;
}
