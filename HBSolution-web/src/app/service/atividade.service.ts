import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Atividade } from '../models/atividade.model';

@Injectable({
    providedIn: 'root'
})
export class AtividadeService {
    private readonly baseUrl = 'http://localhost:8080/api/atividades';

    constructor(private http: HttpClient) { }

    getHoje(): Observable<Atividade[]> {
        return this.http.get<Atividade[]>(`${this.baseUrl}/hoje`);
    }

    getAtrasadas(): Observable<Atividade[]> {
        return this.http.get<Atividade[]>(`${this.baseUrl}/atrasadas`);
    }

    criarAtividade(atividade: any): Observable<any> {
        return this.http.post<any>(this.baseUrl, atividade);
    }

    realizarAtividade(id: number): Observable<any> {
        return this.http.post<any>(`${this.baseUrl}/${id}/realizar`, {});
    }
}
