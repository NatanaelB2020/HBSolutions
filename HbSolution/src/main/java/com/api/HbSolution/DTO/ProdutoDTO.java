package com.api.HbSolution.DTO;

import java.math.BigDecimal;

import lombok.Data;

@Data
public class ProdutoDTO {

    private Long id;
    private String nome;
    private String descricao;
    private BigDecimal preco;
    private Integer quantidadeEstoque;
    private String codigoBarras;
    private String categoria;
    private Long idEmpresa; // ID da empresa associada ao produto

}
