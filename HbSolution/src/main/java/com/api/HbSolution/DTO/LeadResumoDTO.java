package com.api.HbSolution.DTO;

import java.time.LocalDateTime;

import com.api.HbSolution.enums.StatusLead;

import lombok.Data;

@Data
public class LeadResumoDTO {
    private Long id;
    private String nome;
    private StatusLead status;
    private Integer score;
    private LocalDateTime dataConversao;
}
