import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { LeadService, ImportacaoResultado } from '../../service/lead.service';

@Component({
    selector: 'app-lead-import',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './lead-import.component.html',
    styleUrl: './lead-import.component.css'
})
export class LeadImportComponent {
    arquivoSelecionado: File | null = null;
    resultado: ImportacaoResultado | null = null;

    constructor(private leadService: LeadService) { }

    onFileSelected(event: Event): void {
        const input = event.target as HTMLInputElement;
        this.arquivoSelecionado = input.files?.[0] ?? null;

        if (this.resultado) {
            this.resultado = null;
        }
    }

    onSubmit(): void {
        if (!this.arquivoSelecionado) {
            return;
        }

        this.leadService.importarLeads(this.arquivoSelecionado).subscribe({
            next: (response) => {
                this.resultado = response;
                this.arquivoSelecionado = null;

                const input = document.querySelector<HTMLInputElement>('input[type="file"]');
                if (input) {
                    input.value = '';
                }
            },
            error: (err) => {
                console.error('Erro ao importar leads:', err);
                this.resultado = {
                    totalLidos: 0,
                    importados: 0,
                    atualizados: 0,
                    erros: 1,
                    listaErros: ['Falha na importação do arquivo. Verifique o formato do CSV e tente novamente.']
                };
            }
        });
    }
}
