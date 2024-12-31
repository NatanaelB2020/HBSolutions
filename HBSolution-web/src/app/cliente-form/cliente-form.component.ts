import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-cliente-form',
  standalone: true,
  imports: [ReactiveFormsModule, HttpClientModule],
  templateUrl: './cliente-form.component.html',
  styleUrls: ['./cliente-form.component.css']
})
export class ClienteFormComponent implements OnInit {

  clienteForm!: FormGroup;

  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router
  ) { }

  ngOnInit() {
    this.clienteForm = this.fb.group({
      nome: ['', Validators.required],
      cpf: ['', Validators.required],
      telefone: ['', Validators.required],
      email: ['', [Validators.required, Validators.email]],
      cep: ['', Validators.required],
      logradouro: [{ value: '', disabled: true }],
      numero: [''],
      bairro: [{ value: '', disabled: true }],
      cidade: [{ value: '', disabled: true }],
      estado: [{ value: '', disabled: true }]
    });
  }

  // Função para buscar endereço por CEP
  buscarEndereco() {
    const cep = this.clienteForm.get('cep')?.value;
    if (cep && cep.length === 8) {
      this.http.get(`http://localhost:8080/enderecos/buscar/${cep}`).subscribe(
        (dados: any) => {
          console.log(dados); // Verifique a estrutura da resposta
          this.clienteForm.patchValue({
            logradouro: dados.logradouro,
            bairro: dados.bairro,
            cidade: dados.cidade,
            estado: dados.estado
          });
        },
        error => {
          console.error('Erro ao buscar o endereço', error);
        }
      );
    }
  }

  // Função para salvar os dados do cliente
  salvar() {
    if (this.clienteForm.valid) {
      // Temporariamente habilite os campos desabilitados
      this.clienteForm.get('logradouro')?.enable();
      this.clienteForm.get('bairro')?.enable();
      this.clienteForm.get('cidade')?.enable();
      this.clienteForm.get('estado')?.enable();

      // Construa o objeto para o envio
      const cliente = {
        nome: this.clienteForm.get('nome')?.value,
        cpf: this.clienteForm.get('cpf')?.value,
        telefone: this.clienteForm.get('telefone')?.value,
        email: this.clienteForm.get('email')?.value,
        endereco: {
          cep: this.clienteForm.get('cep')?.value,
          logradouro: this.clienteForm.get('logradouro')?.value,
          numero: this.clienteForm.get('numero')?.value,
          bairro: this.clienteForm.get('bairro')?.value,
          cidade: this.clienteForm.get('cidade')?.value,
          estado: this.clienteForm.get('estado')?.value
        }
      };

      // Desabilite os campos novamente após construir o objeto
      this.clienteForm.get('logradouro')?.disable();
      this.clienteForm.get('bairro')?.disable();
      this.clienteForm.get('cidade')?.disable();
      this.clienteForm.get('estado')?.disable();

      console.log('Dados do cliente a serem enviados:', cliente);

      // Enviar dados para o backend
      this.http.post('http://localhost:8080/clientes', cliente).subscribe(
        response => {
          console.log('Cliente salvo com sucesso!', response);
          alert('Cliente salvo com sucesso!');
          
          // Limpe o formulário após salvar
          this.clienteForm.reset();
        },
        error => {
          console.error('Erro ao salvar o cliente', error);
          alert('Erro ao salvar o cliente. Tente novamente.');
        }
      );
    } else {
      alert('Por favor, preencha todos os campos obrigatórios.');
    }
  }

  // Função para limpar o formulário
  limpar() {
    this.clienteForm.reset();
  }

  // Função para voltar à página anterior
  voltar() {
    this.router.navigate(['/']); // Ou use window.history.back() para voltar à página anterior
  }
}
