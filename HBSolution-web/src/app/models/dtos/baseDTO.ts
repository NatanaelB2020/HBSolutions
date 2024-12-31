export class BaseDTO {
    public id!: number;
    public createdAt?: Date;

    constructor(model?: any) {
        if (model) {
            this.id = model.id;
            this.createdAt = model.createdAt;
        }
    }
}
