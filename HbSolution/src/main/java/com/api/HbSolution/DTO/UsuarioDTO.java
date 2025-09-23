package com.api.HbSolution.DTO;

import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
public class UsuarioDTO {

    private Long id;
    private Long empresaId;
    private String nome;
    private String email;
    private LocalDateTime dataCriacao;
    private Boolean ativo;

}
