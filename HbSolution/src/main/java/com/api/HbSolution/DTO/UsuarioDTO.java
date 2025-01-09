package com.api.HbSolution.DTO;

import lombok.Data;
import java.time.LocalDateTime;

@Data
public class UsuarioDTO {

    private Long id;
    private Long empresaId;
    private String nome;
    private String email;
    private LocalDateTime dataCriacao;
    private Boolean ativo;

}
