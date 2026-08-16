package com.api.HbSolution.enums;

public enum StatusOportunidade {
    ABERTA("Aberta"),
    GANHA("Ganha"),
    PERDIDA("Perdida"),
    PAUSADA("Pausada");

    private final String descricao;

    StatusOportunidade(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
