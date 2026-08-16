package com.api.HbSolution.DTO;

import com.api.HbSolution.enums.OrigemLead;
import com.api.HbSolution.enums.StatusLead;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class LeadDTO extends BaseDTO {
    private String nome;
    private String email;
    private String telefone;
    private OrigemLead origem;
    private StatusLead status;
    private Integer score;
    private String observacao;
}
