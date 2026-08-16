import { Pipe, PipeTransform } from '@angular/core';

@Pipe({
    name: 'currencyBr',
    standalone: true
})
export class CurrencyBrPipe implements PipeTransform {
    transform(value: number | string | null | undefined): string {
        const valorNumerico = Number(value ?? 0);

        if (!Number.isFinite(valorNumerico)) {
            return 'R$ 0,00';
        }

        return new Intl.NumberFormat('pt-BR', {
            style: 'currency',
            currency: 'BRL'
        }).format(valorNumerico);
    }
}
