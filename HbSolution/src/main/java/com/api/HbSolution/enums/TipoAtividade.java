package com.api.HbSolution.enums;

public enum TipoAtividade {
    LIGACAO("Ligação"),
    EMAIL("E-mail"),
    REUNIAO("Reunião"),
    WHATSAPP("WhatsApp"),
    VISITA("Visita"),
    TAREFA("Tarefa"),
    NOTA("Nota");

    private final String descricao;

    TipoAtividade(String descricao) {
        this.descricao = descricao;
    }

    public String getDescricao() {
        return descricao;
    }
}
