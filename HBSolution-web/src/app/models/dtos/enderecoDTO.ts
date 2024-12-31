import { BaseDTO } from "./baseDTO";

export class EnderecoDTO extends BaseDTO {
    public logradouro!: string;
    public numero!: string;
    public bairro!: string;
    public cidade!: string;
    public estado!: string;
    public cep!: string;
    public complemento?: string;

    constructor(model?: EnderecoDTO) {
        super(model);
        if (model) {
            this.logradouro = model.logradouro;
            this.numero = model.numero;
            this.bairro = model.bairro;
            this.cidade = model.cidade;
            this.estado = model.estado;
            this.cep = model.cep;
            this.complemento = model.complemento;
        }
    }

    static setPayload(json: any): EnderecoDTO {
        const endereco = new EnderecoDTO();
        endereco.id = json.id;
        endereco.logradouro = json.logradouro;
        endereco.numero = json.numero;
        endereco.bairro = json.bairro;
        endereco.cidade = json.cidade;
        endereco.estado = json.estado;
        endereco.cep = json.cep;
        endereco.complemento = json.complemento;
        endereco.createdAt = json.createdAt;
        return endereco;
    }
}
