package com.api.HbSolution.DTO;

import com.api.HbSolution.enums.EtapaOportunidade;

import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class MoverEtapaRequest {

    @NotNull(message = "A etapa é obrigatória")
    private EtapaOportunidade etapa;

    private String motivo;
}
