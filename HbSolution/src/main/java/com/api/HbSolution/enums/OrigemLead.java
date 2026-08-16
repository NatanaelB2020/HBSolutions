package com.api.HbSolution.enums;

public enum OrigemLead {
    SITE("Site"),
    INDICACAO("Indicação"),
    WHATSAPP("WhatsApp"),
    FEIRA("Feira"),
    REDE_SOCIAL("Rede social"),
    OUTROS("Outros");

    private final String descricao;

    OrigemLead(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
