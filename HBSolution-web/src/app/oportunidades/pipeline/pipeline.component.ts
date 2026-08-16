import { CdkDragDrop, CdkDrag, CdkDropList, moveItemInArray, transferArrayItem } from '@angular/cdk/drag-drop';
import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { OportunidadeService } from '../../service/oportunidade.service';
import { CurrencyBrPipe } from '../../shared/pipes/currency-br.pipe';

@Component({
    selector: 'app-pipeline',
    standalone: true,
    imports: [CommonModule, RouterModule, CdkDropList, CdkDrag, CurrencyBrPipe],
    templateUrl: './pipeline.component.html',
    styleUrl: './pipeline.component.css'
})
export class PipelineComponent implements OnInit {
    pipeline: any = {};

    etapas = [
        'PROSPECCAO',
        'QUALIFICACAO',
        'PROPOSTA_ENVIADA',
        'NEGOCIACAO'
    ];

    constructor(private oportunidadeService: OportunidadeService) { }

    ngOnInit(): void {
        this.carregar();
    }

    carregar(): void {
        this.oportunidadeService.getPipeline().subscribe({
            next: (response) => {
                this.pipeline = response;
            },
            error: (err) => {
                console.error('Erro ao carregar pipeline:', err);
            }
        });
    }

    getEtapaLista(etapa: string): any[] {
        return this.pipeline?.[etapa] ?? [];
    }

    getNomeEtapa(etapa: string): string {
        const nomes = {
            PROSPECCAO: 'Prospecção',
            QUALIFICACAO: 'Qualificação',
            PROPOSTA_ENVIADA: 'Proposta enviada',
            NEGOCIACAO: 'Negociação'
        };

        return nomes[etapa as keyof typeof nomes] ?? etapa;
    }

    soltar(event: CdkDragDrop<any[]>, etapaDestino: string): void {
        if (event.previousContainer === event.container) {
            moveItemInArray(event.container.data, event.previousIndex, event.currentIndex);
            return;
        }

        const op = event.previousContainer.data[event.previousIndex];
        transferArrayItem(event.previousContainer.data, event.container.data, event.previousIndex, event.currentIndex);

        this.oportunidadeService.moverEtapa(op.id, etapaDestino).subscribe({
            error: () => this.carregar()
        });
    }
}
