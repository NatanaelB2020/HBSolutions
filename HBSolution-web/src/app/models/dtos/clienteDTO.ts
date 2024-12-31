import { BaseDTO } from "./baseDTO";
import { EnderecoDTO } from "./enderecoDTO";

export class ClienteDTO extends BaseDTO {
    public nome!: string;
    public cpf!: string;
    public email!: string;
    public telefone!: string;
    public endereco!: EnderecoDTO;

    constructor(model?: ClienteDTO) {
        super(model);
        if (model) {
            this.nome = model.nome;
            this.cpf = model.cpf;
            this.email = model.email;
            this.telefone = model.telefone;
            this.endereco = new EnderecoDTO(model.endereco);
        }
    }

    static setPayload(json: any): ClienteDTO {
        const cliente = new ClienteDTO();
        cliente.id = json.id;
        cliente.nome = json.nome;
        cliente.cpf = json.cpf;
        cliente.email = json.email;
        cliente.telefone = json.telefone;
        if (json.endereco) {
            cliente.endereco = EnderecoDTO.setPayload(json.endereco);
        }
        cliente.createdAt = json.createdAt;
        return cliente;
    }
}
