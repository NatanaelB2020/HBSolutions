import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { ActivatedRoute, Router } from '@angular/router';

@Component({
  selector: 'app-empresa-form',
  standalone: true,
  imports: [ReactiveFormsModule, HttpClientModule], 
  templateUrl: './empresa-form.component.html',
  styleUrls: ['./empresa-form.component.css']
})
export class EmpresaFormComponent implements OnInit {
  
  empresaForm!: FormGroup;
  
  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router
  ) { }

  ngOnInit() {
    this.empresaForm = this.fb.group({
      nomeFantasia: ['', Validators.required],
      cnpj: ['', Validators.required],
      telefone: ['', Validators.required],
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
  const cep = this.empresaForm.get('cep')?.value;
  if (cep && cep.length === 8) {
    this.http.get(`http://localhost:8080/enderecos/buscar/${cep}`).subscribe(
      (dados: any) => {
        console.log(dados); // Verifique a estrutura da resposta
        this.empresaForm.patchValue({
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

  
  // Função para salvar os dados da empresa
  salvar() {
    if (this.empresaForm.valid) {
      // Temporariamente habilite os campos desabilitados
      this.empresaForm.get('logradouro')?.enable();
      this.empresaForm.get('bairro')?.enable();
      this.empresaForm.get('cidade')?.enable();
      this.empresaForm.get('estado')?.enable();
  
      // Construa o objeto para o envio
      const empresa = {
        nomeFantasia: this.empresaForm.get('nomeFantasia')?.value,
        cnpj: this.empresaForm.get('cnpj')?.value,
        telefone: this.empresaForm.get('telefone')?.value,
        endereco: {
          cep: this.empresaForm.get('cep')?.value,
          logradouro: this.empresaForm.get('logradouro')?.value,
          numero: this.empresaForm.get('numero')?.value,
          bairro: this.empresaForm.get('bairro')?.value,
          cidade: this.empresaForm.get('cidade')?.value,
          estado: this.empresaForm.get('estado')?.value
        }
      };
  
      // Desabilite os campos novamente após construir o objeto
      this.empresaForm.get('logradouro')?.disable();
      this.empresaForm.get('bairro')?.disable();
      this.empresaForm.get('cidade')?.disable();
      this.empresaForm.get('estado')?.disable();
  
      console.log('Dados da empresa a serem enviados:', empresa);
  
      // Enviar dados para o backend
      this.http.post('http://localhost:8080/empresas', empresa).subscribe(
        response => {
          console.log('Empresa salva com sucesso!', response);
          alert('Empresa salva com sucesso!');
          
          // Limpe o formulário após salvar
          this.empresaForm.reset();
        },
        error => {
          console.error('Erro ao salvar a empresa', error);
          alert('Erro ao salvar a empresa. Tente novamente.');
        }
      );
    } else {
      alert('Por favor, preencha todos os campos obrigatórios.');
    }
  }
  
  


  // Função para limpar o formulário
  limpar() {
    this.empresaForm.reset();
  }

  // Função para voltar à página anterior
  voltar() {
    this.router.navigate(['/']); // Ou use window.history.back() para voltar à página anterior
  }
}
