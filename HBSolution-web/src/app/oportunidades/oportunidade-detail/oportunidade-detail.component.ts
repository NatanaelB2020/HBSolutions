import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { Atividade } from '../../models/atividade.model';
import { AtividadeService } from '../../service/atividade.service';
import { OportunidadeService } from '../../service/oportunidade.service';

@Component({
    selector: 'app-oportunidade-detail',
    standalone: true,
    imports: [CommonModule, RouterModule, FormsModule],
    templateUrl: './oportunidade-detail.component.html',
    styleUrl: './oportunidade-detail.component.css'
})
export class OportunidadeDetailComponent implements OnInit {
    oportunidade: any = null;
    atividades: Atividade[] = [];
    oportunidadeId: number | null = null;
    novaAtividade = {
        titulo: '',
        tipo: 'LIGACAO',
        descricao: '',
        dataAgendamento: ''
    };

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private oportunidadeService: OportunidadeService,
        private atividadeService: AtividadeService,
        private http: HttpClient
    ) { }

    ngOnInit(): void {
        this.oportunidadeId = Number(this.route.snapshot.params['id']);

        this.oportunidadeService.getOportunidade(this.oportunidadeId).subscribe({
            next: (response) => {
                this.oportunidade = response;
                this.carregarAtividades();
            },
            error: (err) => {
                console.error('Erro ao carregar oportunidade:', err);
            }
        });
    }

    carregarAtividades(): void {
        this.http.get<Atividade[]>('http://localhost:8080/api/atividades').subscribe({
            next: (response) => {
                this.atividades = response.filter((atividade: any) => {
                    return this.oportunidade && atividade.oportunidade?.id === this.oportunidade.id;
                });
            },
            error: (err) => {
                console.error('Erro ao carregar atividades:', err);
            }
        });
    }

    agendarAtividade(): void {
        if (!this.oportunidadeId) {
            return;
        }

        if (!this.novaAtividade.titulo || !this.novaAtividade.dataAgendamento) {
            return;
        }

        const payload = {
            titulo: this.novaAtividade.titulo,
            tipo: this.novaAtividade.tipo,
            descricao: this.novaAtividade.descricao,
            dataAgendamento: this.novaAtividade.dataAgendamento,
            oportunidadeId: this.oportunidadeId
        };

        this.atividadeService.criarAtividade(payload).subscribe({
            next: () => {
                this.carregarAtividades();
                this.novaAtividade = {
                    titulo: '',
                    tipo: 'LIGACAO',
                    descricao: '',
                    dataAgendamento: ''
                };
            },
            error: (err) => {
                console.error('Erro ao agendar atividade:', err);
            }
        });
    }

    irParaFechamento(): void {
        this.router.navigate(['/oportunidades', this.oportunidade.id, 'fechar']);
    }

    voltar(): void {
        this.router.navigate(['/pipeline']);
    }
}
