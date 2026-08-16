package com.api.HbSolution.enums;

public enum EtapaOportunidade {
    PROSPECCAO("Prospecção"),
    QUALIFICACAO("Qualificação"),
    PROPOSTA_ENVIADA("Proposta enviada"),
    NEGOCIACAO("Negociação"),
    FECHAMENTO_GANHO("Fechamento ganho"),
    FECHAMENTO_PERDIDO("Fechamento perdido");

    private final String descricao;

    EtapaOportunidade(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
