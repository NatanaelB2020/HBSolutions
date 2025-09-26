import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Router } from '@angular/router';
import { CommonModule } from '@angular/common';

@Component({
  selector: 'app-cliente-form',
  standalone: true,
  imports: [ReactiveFormsModule, HttpClientModule, CommonModule],
  templateUrl: './cliente-form.component.html',
  styleUrls: ['./cliente-form.component.css']
})
export class ClienteFormComponent implements OnInit {

  clienteForm!: FormGroup;
  carregandoEndereco: boolean = false; // Spinner
  private buscaEmAndamento: boolean = false; // Evita execução duplicada

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router
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
    const cep = this.clienteForm.get('cep')?.value;

    if (!cep || cep.length !== 8 || this.buscaEmAndamento) return;

    this.buscaEmAndamento = true;
    this.carregandoEndereco = true;

    const token = localStorage.getItem('authToken');
    const headers = { Authorization: `Bearer ${token}` };

    this.http.get(`http://localhost:8080/enderecos/buscar/${cep}`, { headers }).subscribe(
      (dados: any) => {
        this.clienteForm.patchValue({
          logradouro: dados.logradouro,
          bairro: dados.bairro,
          cidade: dados.cidade,
          estado: dados.estado
        });
        this.carregandoEndereco = false;
        this.buscaEmAndamento = false;
      },
      error => {
        console.error('Erro ao buscar o endereço', error);
        this.carregandoEndereco = false;
        this.buscaEmAndamento = false;
        // Mensagem não interfere na execução da função
        alert('Não foi possível buscar o endereço. Verifique seu login ou token.');
      }
    );
  }

  salvar() {
    if (!this.clienteForm.valid) {
      alert('Por favor, preencha todos os campos obrigatórios.');
      return;
    }

    // Habilita temporariamente campos desabilitados
    ['logradouro', 'bairro', 'cidade', 'estado'].forEach(campo => this.clienteForm.get(campo)?.enable());

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

    // Desabilita novamente os campos
    ['logradouro', 'bairro', 'cidade', 'estado'].forEach(campo => this.clienteForm.get(campo)?.disable());

    this.http.post('http://localhost:8080/clientes', cliente).subscribe(
      () => {
        alert('Cliente salvo com sucesso!');
        this.clienteForm.reset();
      },
      error => {
        console.error('Erro ao salvar o cliente', error);
        alert('Erro ao salvar o cliente. Tente novamente.');
      }
    );
  }

  limpar() {
    this.clienteForm.reset();
  }

  voltar() {
    this.router.navigate(['/']);
  }
}
