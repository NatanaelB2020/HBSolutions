export interface Atividade {
    id: number;
    titulo: string;
    tipo: string;
    descricao: string;
    status: string;
    dataAgendamento: Date;
    resultado: string;
    duracaoMinutos: number;
}
