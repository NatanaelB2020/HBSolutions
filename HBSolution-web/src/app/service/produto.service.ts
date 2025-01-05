import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { ProdutoDTO } from '../models/dtos/ProdutoDTO';
import { Observable } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class ProdutoService {

  private apiUrl = 'http://localhost:8080/produtos';

  constructor(private http: HttpClient) { }

  // Método para criar um produto
  createProduto(produto: ProdutoDTO): Observable<ProdutoDTO> {
    return this.http.post<ProdutoDTO>(this.apiUrl, produto);
  }

  // Outros métodos que você pode precisar
  getAllProdutos(): Observable<ProdutoDTO[]> {
    return this.http.get<ProdutoDTO[]>(this.apiUrl);
  }

  getProdutoById(id: number): Observable<ProdutoDTO> {
    return this.http.get<ProdutoDTO>(`${this.apiUrl}/${id}`);
  }
}
