import { CommonModule } from '@angular/common';
import { HttpClient } from '@angular/common/http';
import { Component, OnInit } from '@angular/core';
import { RouterModule } from '@angular/router';

interface Cliente {
    id: number;
    nome?: string;
    email?: string;
    telefone?: string;
}

@Component({
    selector: 'app-cliente-list',
    standalone: true,
    imports: [CommonModule, RouterModule],
    templateUrl: './cliente-list.component.html',
    styleUrl: './cliente-list.component.css'
})
export class ClienteListComponent implements OnInit {
    clientes: Cliente[] = [];

    constructor(private http: HttpClient) { }

    ngOnInit(): void {
        this.http.get<Cliente[]>('/api/clientes').subscribe({
            next: (response) => {
                this.clientes = response || [];
            },
            error: (err) => {
                console.error('Erro ao carregar clientes:', err);
            }
        });
    }
}
