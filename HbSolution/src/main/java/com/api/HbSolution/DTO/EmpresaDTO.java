package com.api.HbSolution.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
public class EmpresaDTO extends BaseDTO {
    private String nomeFantasia;
    private String cnpj;
    private String telefone;
    private EnderecoDTO endereco;
}
