package com.api.HbSolution.DTO;

import java.util.ArrayList;
import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ImportacaoResultadoDTO {

    private int totalLidos;
    private int totalImportados;
    private int totalAtualizados;
    private int totalErros;
    private List<String> erros = new ArrayList<>();
}
