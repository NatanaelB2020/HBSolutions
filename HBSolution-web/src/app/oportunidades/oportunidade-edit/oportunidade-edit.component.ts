import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormControl, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { OportunidadeService } from '../../service/oportunidade.service';

@Component({
    selector: 'app-oportunidade-edit',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, RouterModule],
    templateUrl: './oportunidade-edit.component.html',
    styleUrl: './oportunidade-edit.component.css'
})
export class OportunidadeEditComponent implements OnInit {
    oportunidadeId: number | null = null;

    form: FormGroup = new FormGroup({
        titulo: new FormControl('', { nonNullable: true, validators: [Validators.required] }),
        descricao: new FormControl('', { nonNullable: true }),
        valor: new FormControl(0, { nonNullable: true, validators: [Validators.required, Validators.min(0)] }),
        probabilidade: new FormControl(0, { nonNullable: true, validators: [Validators.required, Validators.min(0), Validators.max(100)] }),
        dataFechamentoEstimada: new FormControl('', { nonNullable: true })
    });

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private oportunidadeService: OportunidadeService,
        private fb: FormBuilder
    ) {
        this.form = this.fb.group({
            titulo: ['', Validators.required],
            descricao: [''],
            valor: [0, [Validators.required, Validators.min(0)]],
            probabilidade: [0, [Validators.required, Validators.min(0), Validators.max(100)]],
            dataFechamentoEstimada: ['']
        });
    }

    ngOnInit(): void {
        this.oportunidadeId = Number(this.route.snapshot.paramMap.get('id'));

        if (!this.oportunidadeId) {
            this.router.navigate(['/pipeline']);
            return;
        }

        this.oportunidadeService.getById(this.oportunidadeId).subscribe({
            next: (oportunidade) => {
                this.form.patchValue({
                    titulo: oportunidade.titulo,
                    descricao: oportunidade.descricao,
                    valor: oportunidade.valor,
                    probabilidade: oportunidade.probabilidade,
                    dataFechamentoEstimada: oportunidade.dataFechamentoEstimada ?? ''
                });
            },
            error: (err) => {
                console.error('Erro ao carregar oportunidade:', err);
            }
        });
    }

    onSubmit(): void {
        if (this.form.invalid || !this.oportunidadeId) {
            this.form.markAllAsTouched();
            return;
        }

        const payload = {
            titulo: this.form.value.titulo,
            descricao: this.form.value.descricao ?? '',
            valor: Number(this.form.value.valor ?? 0),
            probabilidade: Number(this.form.value.probabilidade ?? 0),
            dataFechamentoEstimada: this.form.value.dataFechamentoEstimada || null
        };

        this.oportunidadeService.atualizar(this.oportunidadeId, payload).subscribe({
            next: () => {
                this.router.navigate(['/oportunidades', this.oportunidadeId]);
            },
            error: (err) => {
                console.error('Erro ao atualizar oportunidade:', err);
            }
        });
    }
}
