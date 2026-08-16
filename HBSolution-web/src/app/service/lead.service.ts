import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Lead } from '../models/lead.model';

export interface Page<T> {
    content: T[];
    totalElements: number;
    totalPages: number;
    size: number;
    number: number;
}

export interface ImportacaoResultado {
    totalLidos?: number;
    importados?: number;
    atualizados?: number;
    erros?: number;
    listaErros?: string[];
}

@Injectable({
    providedIn: 'root'
})
export class LeadService {
    private readonly baseUrl = 'http://localhost:8080/api/leads';

    constructor(private http: HttpClient) { }

    getMeusLeads(): Observable<Lead[]> {
        return this.http.get<Lead[]>(`${this.baseUrl}/meus`);
    }

    getById(id: number): Observable<Lead> {
        return this.http.get<Lead>(`${this.baseUrl}/${id}`);
    }

    buscarLeads(filtros: any): Observable<Page<Lead>> {
        return this.http.get<Page<Lead>>(`${this.baseUrl}/busca`, { params: filtros });
    }

    criarLead(lead: Partial<Lead>): Observable<any> {
        return this.http.post<any>(this.baseUrl, lead);
    }

    atualizarLead(id: number, lead: Partial<Lead>): Observable<any> {
        return this.http.put<any>(`${this.baseUrl}/${id}`, lead);
    }

    importarLeads(arquivo: File): Observable<ImportacaoResultado> {
        const formData = new FormData();
        formData.append('arquivo', arquivo);

        return this.http.post<ImportacaoResultado>(`${this.baseUrl}/importar`, formData);
    }

    converterLead(id: number): Observable<any> {
        return this.http.post<any>(`${this.baseUrl}/${id}/converter`, {});
    }
}
