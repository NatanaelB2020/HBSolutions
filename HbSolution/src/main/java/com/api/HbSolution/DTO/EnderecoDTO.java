package com.api.HbSolution.DTO;

import lombok.Data;

@Data
public class EnderecoDTO extends BaseDTO {
    private String logradouro;
    private String numero;
    private String bairro;
    private String cidade;
    private String estado;
    private String cep;
    private String complemento;
}
