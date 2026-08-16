import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { AfterViewInit, Component, ElementRef, OnInit, ViewChild } from '@angular/core';
import { Dashboard } from '../models/dashboard.model';
import { DashboardService } from '../service/dashboard.service';
import { CurrencyBrPipe } from '../shared/pipes/currency-br.pipe';

interface VendaPorDia {
    data: string;
    valor: number;
}

@Component({
    selector: 'app-dashboard',
    standalone: true,
    imports: [CommonModule, CurrencyBrPipe],
    templateUrl: './dashboard.component.html',
    styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit, AfterViewInit {
    @ViewChild('grafico') graficoCanvas?: ElementRef<HTMLCanvasElement>;

    dados!: Dashboard;
    vendasPorDia: VendaPorDia[] = [];

    constructor(
        private dashboardService: DashboardService,
        private http: HttpClient
    ) { }

    ngOnInit(): void {
        this.dashboardService.getDashboard().subscribe({
            next: (response) => {
                this.dados = response;
            },
            error: (err) => {
                console.error('Erro ao carregar dashboard:', err);
            }
        });

        const hoje = new Date();
        const primeiroDiaMes = new Date(hoje.getFullYear(), hoje.getMonth(), 1);
        const ultimoDiaMes = new Date(hoje.getFullYear(), hoje.getMonth() + 1, 0);

        const dataInicio = this.formatarData(primeiroDiaMes);
        const dataFim = this.formatarData(ultimoDiaMes);

        this.http.get<any>(`/api/oportunidades/relatorio/vendas`, {
            params: {
                dataInicio,
                dataFim
            }
        }).subscribe({
            next: (response) => {
                this.vendasPorDia = (response?.vendasPorDia ?? []).map((item: any) => ({
                    data: item.data,
                    valor: Number(item.valor ?? 0)
                }));
                this.desenharGrafico(this.graficoCanvas?.nativeElement);
            },
            error: (err) => {
                console.error('Erro ao carregar relatório de vendas:', err);
                this.vendasPorDia = [];
            }
        });
    }

    ngAfterViewInit(): void {
        this.desenharGrafico(this.graficoCanvas?.nativeElement);
    }

    private formatarData(data: Date): string {
        const ano = data.getFullYear();
        const mes = String(data.getMonth() + 1).padStart(2, '0');
        const dia = String(data.getDate()).padStart(2, '0');
        return `${ano}-${mes}-${dia}`;
    }

    desenharGrafico(canvas?: HTMLCanvasElement): void {
        if (!canvas || this.vendasPorDia.length === 0) {
            return;
        }

        const contexto = canvas.getContext('2d');
        if (!contexto) {
            return;
        }

        const largura = canvas.width;
        const altura = canvas.height;
        const margem = { topo: 20, direita: 20, baixo: 30, esquerda: 50 };
        const areaGrafico = {
            width: largura - margem.esquerda - margem.direita,
            height: altura - margem.topo - margem.baixo
        };

        const maxValor = Math.max(...this.vendasPorDia.map(item => item.valor), 1);
        const espacoBarra = areaGrafico.width / this.vendasPorDia.length;
        const larguraBarra = Math.max(8, espacoBarra * 0.6);

        contexto.clearRect(0, 0, largura, altura);
        contexto.fillStyle = '#ffffff';
        contexto.fillRect(0, 0, largura, altura);

        contexto.strokeStyle = '#dfe3e8';
        contexto.lineWidth = 1;
        contexto.beginPath();
        contexto.moveTo(margem.esquerda, margem.topo);
        contexto.lineTo(margem.esquerda, altura - margem.baixo);
        contexto.lineTo(largura - margem.direita, altura - margem.baixo);
        contexto.stroke();

        const passos = 4;
        for (let i = 0; i <= passos; i++) {
            const valorY = (maxValor / passos) * i;
            const y = altura - margem.baixo - (valorY / maxValor) * areaGrafico.height;

            contexto.strokeStyle = '#eef2f7';
            contexto.beginPath();
            contexto.moveTo(margem.esquerda, y);
            contexto.lineTo(largura - margem.direita, y);
            contexto.stroke();

            contexto.fillStyle = '#475569';
            contexto.font = '11px sans-serif';
            contexto.fillText(this.formatarValorEixo(valorY), 8, y + 4);
        }

        this.vendasPorDia.forEach((item, indice) => {
            const x = margem.esquerda + indice * espacoBarra + (espacoBarra - larguraBarra) / 2;
            const alturaBarra = (item.valor / maxValor) * areaGrafico.height;
            const y = altura - margem.baixo - alturaBarra;

            contexto.fillStyle = '#4caf50';
            contexto.fillRect(x, y, larguraBarra, alturaBarra);

            contexto.fillStyle = '#475569';
            contexto.font = '10px sans-serif';
            contexto.textAlign = 'center';
            const dia = new Date(item.data).getDate();
            contexto.fillText(String(dia), x + larguraBarra / 2, altura - margem.baixo + 15);

            contexto.fillStyle = '#1f2937';
            contexto.font = '10px sans-serif';
            contexto.fillText(this.formatarValorTooltip(item.valor), x + larguraBarra / 2, y - 8);
        });
    }

    formatarValorTooltip(valor: number): string {
        return new Intl.NumberFormat('pt-BR', {
            style: 'currency',
            currency: 'BRL',
            minimumFractionDigits: 2
        }).format(valor);
    }

    formatarValorEixo(valor: number): string {
        return `R$ ${valor.toLocaleString('pt-BR', { maximumFractionDigits: 0 })}`;
    }
}

