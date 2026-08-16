package com.api.HbSolution.enums;

public enum StatusAtividade {
    PENDENTE("Pendente"),
    REALIZADA("Realizada"),
    CANCELADA("Cancelada"),
    ADIADA("Adiada");

    private final String descricao;

    StatusAtividade(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
