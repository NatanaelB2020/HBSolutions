export interface Oportunidade {
    id: number;
    titulo: string;
    descricao: string;
    etapa: string;
    status: string;
    valor: number;
    probabilidade: number;
    dataFechamentoEstimada?: string | null;
    lead?: {
        id: number;
        nome: string;
    };
    cliente?: {
        id: number;
        nome: string;
    };
}
