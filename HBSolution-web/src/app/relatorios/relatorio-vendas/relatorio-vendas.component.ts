import { HttpClient } from '@angular/common/http';
import { Component } from '@angular/core';
import { FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { CommonModule } from '@angular/common';

export interface RelatorioVendasDTO {
    totalVendido: number;
    totalPerdido: number;
    ticketMedio: number;
    taxaConversaoPeriodo: number;
    quantidadeVendas: number;
    quantidadePerdidas: number;
}

@Component({
    selector: 'app-relatorio-vendas',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule],
    templateUrl: './relatorio-vendas.component.html',
    styleUrl: './relatorio-vendas.component.css'
})
export class RelatorioVendasComponent {
    form = new FormGroup({
        dataInicio: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
        dataFim: new FormControl('', { nonNullable: true, validators: [Validators.required] })
    });

    resultado: RelatorioVendasDTO | null = null;
    mostrandoResultado = false;

    constructor(private http: HttpClient) { }

    get isDateRangeValid(): boolean {
        const dataInicio = this.form.value.dataInicio;
        const dataFim = this.form.value.dataFim;

        if (!dataInicio || !dataFim) {
            return false;
        }

        return dataInicio <= dataFim;
    }

    onSubmit(): void {
        if (this.form.invalid || !this.isDateRangeValid) {
            this.form.markAllAsTouched();
            return;
        }

        const dataInicio = this.formatDateToISO(this.form.value.dataInicio ?? '');
        const dataFim = this.formatDateToISO(this.form.value.dataFim ?? '');

        if (!dataInicio || !dataFim) {
            return;
        }

        this.http.get<RelatorioVendasDTO>('/api/oportunidades/relatorio/vendas', {
            params: {
                dataInicio,
                dataFim
            }
        }).subscribe({
            next: (response) => {
                this.resultado = response;
                this.mostrandoResultado = true;
            },
            error: (err) => {
                console.error('Erro ao gerar relatório de vendas:', err);
                this.resultado = null;
                this.mostrandoResultado = false;
            }
        });
    }

    private formatDateToISO(value: string): string | null {
        if (!value) {
            return null;
        }

        const [year, month, day] = value.split('-').map(Number);
        if (!year || !month || !day) {
            return null;
        }

        const date = new Date(year, month - 1, day);
        return date.toISOString().slice(0, 10);
    }
}
