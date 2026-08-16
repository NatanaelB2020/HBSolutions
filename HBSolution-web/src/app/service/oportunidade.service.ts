import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Oportunidade } from '../models/oportunidade.model';

@Injectable({
    providedIn: 'root'
})
export class OportunidadeService {
    private readonly baseUrl = 'http://localhost:8080/api/oportunidades';

    constructor(private http: HttpClient) { }

    getPipeline(): Observable<any> {
        return this.http.get<any>(`${this.baseUrl}/pipeline`);
    }

    getMinhasOportunidades(): Observable<Oportunidade[]> {
        return this.http.get<Oportunidade[]>(`${this.baseUrl}/minhas`);
    }

    getOportunidade(id: number): Observable<any> {
        return this.http.get<any>(`${this.baseUrl}/${id}`);
    }

    getById(id: number): Observable<Oportunidade> {
        return this.http.get<Oportunidade>(`${this.baseUrl}/${id}`);
    }

    atualizar(id: number, opp: Partial<Oportunidade>): Observable<any> {
        return this.http.put<any>(`${this.baseUrl}/${id}`, opp);
    }

    fecharOportunidade(id: number, payload: { status: string; motivo?: string; valorFinal?: number }): Observable<any> {
        return this.http.post<any>(`${this.baseUrl}/${id}/fechar`, payload);
    }

    moverEtapa(id: number, etapa: string): Observable<any> {
        return this.http.patch<any>(`${this.baseUrl}/${id}/etapa`, { etapa });
    }
}
