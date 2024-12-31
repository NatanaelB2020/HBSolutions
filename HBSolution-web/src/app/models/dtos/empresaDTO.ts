import { BaseDTO } from "./baseDTO";
import { EnderecoDTO } from "./enderecoDTO";



export class EmpresaDTO extends BaseDTO {
    public nomeFantasia!: string;
    public cnpj!: string;
    public telefone!: string;
    public endereco!: EnderecoDTO;

    constructor(model?: EmpresaDTO) {
        super(model);
        if (model) {
            this.nomeFantasia = model.nomeFantasia;
            this.cnpj = model.cnpj;
            this.telefone = model.telefone;
            this.endereco = new EnderecoDTO(model.endereco);
        }
    }

    static setPayload(json: any): EmpresaDTO {
        const empresa = new EmpresaDTO();
        empresa.id = json.id;
        empresa.nomeFantasia = json.nomeFantasia;
        empresa.cnpj = json.cnpj;
        empresa.telefone = json.telefone;
        if (json.endereco) {
            empresa.endereco = EnderecoDTO.setPayload(json.endereco);
        }
        empresa.createdAt = json.createdAt;
        return empresa;
    }
}
