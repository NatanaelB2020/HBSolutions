package com.api.HbSolution.enums;

public enum StatusLead {
    NOVO("Novo"),
    EM_CONTATO("Em contato"),
    QUALIFICADO("Qualificado"),
    DESQUALIFICADO("Desqualificado"),
    CONVERTIDO("Convertido");

    private final String descricao;

    StatusLead(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
