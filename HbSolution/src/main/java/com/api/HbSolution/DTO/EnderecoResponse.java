package com.api.HbSolution.DTO;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import lombok.Data;

@Data
@JsonIgnoreProperties(ignoreUnknown = true)
public class EnderecoResponse {
    private String cep;
    private String logradouro;
    private String bairro;
    private String localidade; // Cidade
    private String uf; // Estado
    private boolean erro;
}
