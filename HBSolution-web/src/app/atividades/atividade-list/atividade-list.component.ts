import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { Atividade } from '../../models/atividade.model';
import { AtividadeService } from '../../service/atividade.service';
import { ToastService } from '../../shared/components/toast/toast.service';

@Component({
    selector: 'app-atividade-list',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './atividade-list.component.html',
    styleUrl: './atividade-list.component.css'
})
export class AtividadeListComponent implements OnInit {
    atividadesHoje: Atividade[] = [];
    atividadesAtrasadas: Atividade[] = [];

    constructor(
        private atividadeService: AtividadeService,
        private toastService: ToastService
    ) { }

    ngOnInit(): void {
        this.carregarAtividades();
    }

    carregarAtividades(): void {
        this.atividadeService.getHoje().subscribe({
            next: (response) => {
                this.atividadesHoje = response;
            },
            error: (err) => {
                console.error('Erro ao carregar atividades de hoje:', err);
            }
        });

        this.atividadeService.getAtrasadas().subscribe({
            next: (response) => {
                this.atividadesAtrasadas = response;
            },
            error: (err) => {
                console.error('Erro ao carregar atividades atrasadas:', err);
            }
        });
    }

    marcarComoFeita(id: number, lista: 'hoje' | 'atrasadas'): void {
        this.atividadeService.realizarAtividade(id).subscribe({
            next: () => {
                if (lista === 'hoje') {
                    this.atividadesHoje = this.atividadesHoje.filter(atividade => atividade.id !== id);
                } else {
                    this.atividadesAtrasadas = this.atividadesAtrasadas.filter(atividade => atividade.id !== id);
                }

                this.toastService.sucesso('Atividade concluida');
            },
            error: (err) => {
                console.error('Erro ao realizar atividade:', err);
            }
        });
    }

    get listaVazia(): boolean {
        return this.atividadesHoje.length === 0 && this.atividadesAtrasadas.length === 0;
    }
}
