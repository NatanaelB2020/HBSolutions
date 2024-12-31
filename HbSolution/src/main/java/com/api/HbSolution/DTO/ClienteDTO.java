package com.api.HbSolution.DTO;

import lombok.Data;

@Data
public class ClienteDTO extends BaseDTO {
    private String nome;
    private String cpf;
    private String email;
    private String telefone;
    private EnderecoDTO endereco;
}
