import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';

export interface LoginRequest {
  email: string;
  senha: string;
}

export interface LoginResponse {
  role: string;
  usuario: string;
  token: string;
}

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private baseUrl = 'http://localhost:8080/api/auth';
  private TOKEN_KEY = 'auth_token';
  private USER_KEY = 'auth_user';

  constructor(private http: HttpClient) {}

  /**
   * Faz login no servidor, salva token e informações do usuário
   */
  login(email: string, senha: string): Observable<LoginResponse> {
    return this.http.post<LoginResponse>(`${this.baseUrl}/login`, { email, senha })
      .pipe(
        tap(res => {
          localStorage.setItem(this.TOKEN_KEY, res.token);
          localStorage.setItem(this.USER_KEY, JSON.stringify(res));
        })
      );
  }

  /**
   * Faz logout removendo token e dados do usuário
   */
  logout(): void {
    localStorage.removeItem(this.TOKEN_KEY);
    localStorage.removeItem(this.USER_KEY);
  }

  /**
   * Retorna true se houver token válido no localStorage
   */
  isLoggedIn(): boolean {
    const token = localStorage.getItem(this.TOKEN_KEY);
    return token !== null && token !== '';
  }

  /**
   * Retorna o token JWT
   */
  getToken(): string | null {
    return localStorage.getItem(this.TOKEN_KEY);
  }

  /**
   * Retorna os dados do usuário logado
   */
  getUser(): LoginResponse | null {
    const user = localStorage.getItem(this.USER_KEY);
    return user ? JSON.parse(user) : null;
  }
}
