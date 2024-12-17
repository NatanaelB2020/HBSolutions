package com.api.HbSolution.DTO;

import lombok.Data;

@Data
public class EmpresaDTO extends BaseDTO {
    private String nomeFantasia;
    private String cnpj;
    private String telefone;
    private EnderecoDTO endereco;
}
