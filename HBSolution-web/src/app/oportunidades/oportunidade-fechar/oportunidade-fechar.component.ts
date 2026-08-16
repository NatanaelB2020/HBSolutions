import { CommonModule } from '@angular/common';
import { Component, OnInit } from '@angular/core';
import { FormBuilder, FormGroup, ReactiveFormsModule, Validators } from '@angular/forms';
import { ActivatedRoute, Router, RouterModule } from '@angular/router';
import { OportunidadeService } from '../../service/oportunidade.service';
import { ToastService } from '../../shared/components/toast/toast.service';

@Component({
    selector: 'app-oportunidade-fechar',
    standalone: true,
    imports: [CommonModule, ReactiveFormsModule, RouterModule],
    templateUrl: './oportunidade-fechar.component.html',
    styleUrl: './oportunidade-fechar.component.css'
})
export class OportunidadeFecharComponent implements OnInit {
    oportunidade: any = null;
    form!: FormGroup;

    constructor(
        private route: ActivatedRoute,
        private router: Router,
        private fb: FormBuilder,
        private oportunidadeService: OportunidadeService,
        private toastService: ToastService
    ) { }

    ngOnInit(): void {
        const id = Number(this.route.snapshot.paramMap.get('id'));

        this.form = this.fb.group({
            status: ['GANHA', Validators.required],
            motivo: [''],
            valorFinal: [null]
        });

        this.oportunidadeService.getOportunidade(id).subscribe({
            next: (response) => {
                this.oportunidade = response;
                this.form.patchValue({
                    status: 'GANHA',
                    valorFinal: response?.valor ?? null
                });
            },
            error: (err) => {
                console.error('Erro ao carregar oportunidade:', err);
            }
        });
    }

    get statusControl() {
        return this.form.get('status');
    }

    get motivoControl() {
        return this.form.get('motivo');
    }

    get valorFinalControl() {
        return this.form.get('valorFinal');
    }

    get titulo(): string {
        return this.oportunidade?.titulo ?? 'Oportunidade';
    }

    get valor(): number | string {
        return this.oportunidade?.valor ?? 0;
    }

    get mostrarMotivo(): boolean {
        return this.statusControl?.value === 'PERDIDA';
    }

    get mostrarValorFinal(): boolean {
        return this.statusControl?.value === 'GANHA';
    }

    onSubmit(): void {
        if (!this.form.valid || !this.oportunidade) {
            return;
        }

        const status = this.form.value.status;
        const motivo = this.form.value.motivo?.trim();
        const valorFinal = this.form.value.valorFinal;

        if (status === 'PERDIDA') {
            if (!motivo) {
                this.motivoControl?.setErrors({ required: true });
                return;
            }
        }

        if (status === 'GANHA') {
            if (valorFinal === null || valorFinal === undefined || valorFinal === '') {
                this.valorFinalControl?.setErrors({ required: true });
                return;
            }
        }

        const payload: any = { status };

        if (status === 'PERDIDA') {
            payload.motivo = motivo;
        }

        if (status === 'GANHA') {
            payload.valorFinal = Number(valorFinal);
        }

        const id = Number(this.route.snapshot.paramMap.get('id'));

        this.oportunidadeService.fecharOportunidade(id, payload).subscribe({
            next: () => {
                this.router.navigate(['/pipeline']);
            },
            error: (err) => {
                console.error('Erro ao fechar oportunidade:', err);
                this.toastService.erro('Erro ao fechar oportunidade. Tente novamente.');
            }
        });
    }

    cancelar(): void {
        const id = Number(this.route.snapshot.paramMap.get('id'));
        this.router.navigate(['/oportunidades', id]);
    }
}
