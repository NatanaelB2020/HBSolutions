import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';
import { Lead } from '../../models/lead.model';
import { LeadService } from '../../service/lead.service';
import { ToastService } from '../../shared/components/toast/toast.service';

@Component({
    selector: 'app-lead-list',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './lead-list.component.html',
    styleUrl: './lead-list.component.css'
})
export class LeadListComponent implements OnInit {
    leads: Lead[] = [];

    constructor(
        private leadService: LeadService,
        private toastService: ToastService
    ) { }

    ngOnInit(): void {
        this.carregarLeads();
    }

    carregarLeads(): void {
        this.leadService.getMeusLeads().subscribe({
            next: (response) => {
                this.leads = response;
            },
            error: (err) => {
                console.error('Erro ao carregar leads:', err);
            }
        });
    }

    converterLead(id: number): void {
        this.leadService.converterLead(id).subscribe({
            next: () => {
                this.carregarLeads();
                this.toastService.sucesso('Lead convertido!');
            },
            error: (err) => {
                console.error('Erro ao converter lead:', err);
            }
        });
    }

    getScoreWidth(score: number): string {
        return `${Math.min(score, 100)}%`;
    }

    getStatusClass(status: string): string {
        switch (status) {
            case 'QUALIFICADO':
                return 'status status-qualificado';
            case 'EM_CONTATO':
                return 'status status-em-contato';
            default:
                return 'status status-novo';
        }
    }

    podeConverter(status: string): boolean {
        return status === 'QUALIFICADO';
    }
}
