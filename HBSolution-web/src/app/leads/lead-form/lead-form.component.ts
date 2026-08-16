import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { LeadService } from '../../service/lead.service';

@Component({
    selector: 'app-lead-form',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, RouterModule],
    templateUrl: './lead-form.component.html',
    styleUrl: './lead-form.component.css'
})
export class LeadFormComponent implements OnInit {
    readonly opcoesOrigem = ['SITE', 'WHATSAPP', 'INDICACAO', 'FEIRA', 'REDE_SOCIAL', 'OUTROS'];

    form: ReturnType<FormBuilder['group']>;
    leadId: number | null = null;

    constructor(
        private fb: FormBuilder,
        private leadService: LeadService,
        private router: Router,
        private route: ActivatedRoute
    ) {
        this.form = this.fb.group({
            nome: ['', Validators.required],
            email: [''],
            telefone: ['', Validators.required],
            origem: new FormControl('SITE'),
            observacao: ['']
        });
    }

    ngOnInit(): void {
        this.route.params.subscribe(params => {
            const id = Number(params['id']);

            if (id) {
                this.leadId = id;
                this.carregarLead(id);
            }
        });
    }

    carregarLead(id: number): void {
        this.leadService.getById(id).subscribe({
            next: (lead) => {
                this.form.patchValue({
                    nome: lead.nome,
                    email: lead.email,
                    telefone: lead.telefone,
                    origem: lead.origem,
                    observacao: lead.observacao
                });
            },
            error: (err) => {
                console.error('Erro ao carregar lead:', err);
            }
        });
    }

    get tituloBotao(): string {
        return this.leadId ? 'Atualizar Lead' : 'Salvar Lead';
    }

    onSubmit(): void {
        if (this.form.invalid) {
            this.form.markAllAsTouched();
            return;
        }

        const payload = {
            nome: this.form.value.nome ?? '',
            email: this.form.value.email ?? '',
            telefone: this.form.value.telefone ?? '',
            origem: this.form.value.origem ?? 'SITE',
            observacao: this.form.value.observacao ?? ''
        };

        const operacao = this.leadId
            ? this.leadService.atualizarLead(this.leadId, payload)
            : this.leadService.criarLead(payload);

        operacao.subscribe({
            next: () => {
                this.router.navigate(['/leads']);
            },
            error: (err) => {
                console.error(this.leadId ? 'Erro ao atualizar lead:' : 'Erro ao criar lead:', err);
            }
        });
    }
}
