import { BaseDTO } from "./baseDTO";

export class ProdutoDTO extends BaseDTO {
    public nome!: string;
    public descricao!: string;
    public preco!: number;
    public quantidadeEstoque!: number;
    public codigoBarras!: string;
    public categoria!: string;
    public idEmpresa!: number;

    constructor(model?: ProdutoDTO) {
        super(model);
        if (model) {
            this.nome = model.nome;
            this.descricao = model.descricao;
            this.preco = model.preco;
            this.quantidadeEstoque = model.quantidadeEstoque;
            this.codigoBarras = model.codigoBarras;
            this.categoria = model.categoria;
            this.idEmpresa = model.idEmpresa;
        }
    }

    static setPayload(json: any): ProdutoDTO {
        const produto = new ProdutoDTO();
        produto.id = json.id;
        produto.nome = json.nome;
        produto.descricao = json.descricao;
        produto.preco = json.preco;
        produto.quantidadeEstoque = json.quantidadeEstoque;
        produto.codigoBarras = json.codigoBarras;
        produto.categoria = json.categoria;
        produto.idEmpresa = json.idEmpresa;
        produto.createdAt = json.createdAt;
        return produto;
    }
}
