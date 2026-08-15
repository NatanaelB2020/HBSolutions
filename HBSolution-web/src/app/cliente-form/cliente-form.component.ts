import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule, HttpHeaders } from '@angular/common/http';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';
import { AuthService } from '../service/auth.service';

@Component({
  selector: 'app-cliente-form',
  standalone: true,
  imports: [ReactiveFormsModule, HttpClientModule, CommonModule],
  templateUrl: './cliente-form.component.html',
  styleUrls: ['./cliente-form.component.css']
})
export class ClienteFormComponent implements OnInit {

  clienteForm!: FormGroup;
  carregandoEndereco: boolean = false;

  mensagemErro: string | null = null;
  mensagemSucesso: string | null = null;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router,
    private authService: AuthService
  ) { }

  ngOnInit() {
    this.clienteForm = this.fb.group({
      nome: ['', Validators.required],
      cpf: ['', [Validators.required, Validators.pattern(/^\d{11}$/)]],
      telefone: ['', Validators.required],
      cep: ['', Validators.required],
      logradouro: [{ value: '', disabled: true }],
      numero: [''],
      bairro: [{ value: '', disabled: true }],
      cidade: [{ value: '', disabled: true }],
      estado: [{ value: '', disabled: true }]
    });
  }

  buscarEndereco() {
    this.mensagemErro = null;
    const cep = this.clienteForm.get('cep')?.value;

    if (cep && cep.length === 8) {
      const token = this.authService.getToken();
      if (!token) {
        this.mensagemErro = 'Você não está autenticado. Faça login novamente.';
        return;
      }

      this.carregandoEndereco = true;

      const headers = new HttpHeaders({
        'Authorization': `Bearer ${token}`
      });

      this.http.get(`http://localhost:8080/enderecos/buscar/${cep}`, { headers })
        .subscribe(
          (dados: any) => {
            this.carregandoEndereco = false;
            this.clienteForm.patchValue({
              logradouro: dados.logradouro,
              bairro: dados.bairro,
              cidade: dados.cidade,
              estado: dados.estado
            });
          },
          error => {
            this.carregandoEndereco = false;
            console.error('Erro ao buscar o endereço', error);
            this.mensagemErro = 'Não foi possível buscar o endereço. Verifique seu login.';
          }
        );
    }
  }

  salvar() {
    this.mensagemErro = null;
    this.mensagemSucesso = null;

    if (!this.authService.isLoggedIn()) {
      this.mensagemErro = 'Você não está autenticado. Faça login novamente.';
      return;
    }

    if (this.clienteForm.valid) {
      this.clienteForm.get('logradouro')?.enable();
      this.clienteForm.get('bairro')?.enable();
      this.clienteForm.get('cidade')?.enable();
      this.clienteForm.get('estado')?.enable();

      const cliente = {
        nome: this.clienteForm.get('nome')?.value,
        cpf: this.clienteForm.get('cpf')?.value,
        telefone: this.clienteForm.get('telefone')?.value,
        endereco: {
          cep: this.clienteForm.get('cep')?.value,
          logradouro: this.clienteForm.get('logradouro')?.value,
          numero: this.clienteForm.get('numero')?.value,
          bairro: this.clienteForm.get('bairro')?.value,
          cidade: this.clienteForm.get('cidade')?.value,
          estado: this.clienteForm.get('estado')?.value
        }
      };

      this.clienteForm.get('logradouro')?.disable();
      this.clienteForm.get('bairro')?.disable();
      this.clienteForm.get('cidade')?.disable();
      this.clienteForm.get('estado')?.disable();

      const headers = new HttpHeaders({
        'Authorization': `Bearer ${this.authService.getToken() || ''}`
      });

      this.http.post('http://localhost:8080/clientes', cliente, { headers })
        .subscribe(
          response => {
            this.mensagemSucesso = 'Cliente salvo com sucesso!';
            this.clienteForm.reset();
          },
          error => {
            console.error('Erro ao salvar o cliente', error);
            this.mensagemErro = 'Erro ao salvar o cliente. Tente novamente.';
          }
        );
    } else {
      this.mensagemErro = 'Por favor, preencha todos os campos obrigatórios.';
    }
  }

  limpar() {
    this.mensagemErro = null;
    this.mensagemSucesso = null;
    this.clienteForm.reset();
  }

  voltar() {
    this.router.navigate(['/']);
  }
}
