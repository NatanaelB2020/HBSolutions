import { Component, OnInit } from '@angular/core';
import { FormGroup, FormBuilder, Validators, ReactiveFormsModule } from '@angular/forms';
import { HttpClient, HttpClientModule } from '@angular/common/http';
import { Router } from '@angular/router';

@Component({
  selector: 'app-produto-form',
  standalone: true,
  imports: [ReactiveFormsModule, HttpClientModule],
  templateUrl: './produto-form.component.html',
  styleUrls: ['./produto-form.component.css']
})
export class ProdutoFormComponent implements OnInit {
  
  produtoForm!: FormGroup;
  
  constructor(
    private fb: FormBuilder,
    private http: HttpClient,
    private router: Router
  ) { }

  ngOnInit() {
    this.produtoForm = this.fb.group({
      nome: ['', Validators.required],
      descricao: ['', Validators.required],
      preco: ['', [Validators.required, Validators.min(0)]],
      quantidadeEstoque: ['', [Validators.required, Validators.min(0)]],
      codigoBarras: ['', Validators.required],
      categoria: ['', Validators.required],
      idEmpresa: ['', Validators.required]
    });
  }

  // Função para salvar os dados do produto
  salvar() {
    if (this.produtoForm.valid) {
      const produto = {
        nome: this.produtoForm.get('nome')?.value,
        descricao: this.produtoForm.get('descricao')?.value,
        preco: this.produtoForm.get('preco')?.value,
        quantidadeEstoque: this.produtoForm.get('quantidadeEstoque')?.value,
        codigoBarras: this.produtoForm.get('codigoBarras')?.value,
        categoria: this.produtoForm.get('categoria')?.value,
        empresa: { id: this.produtoForm.get('idEmpresa')?.value }
      };
  
      console.log('Dados do produto a serem enviados:', produto);
  
      // Enviar dados para o backend
      this.http.post('http://localhost:8080/produtos', produto).subscribe(
        response => {
          console.log('Produto salvo com sucesso!', response);
          alert('Produto salvo com sucesso!');
          
          // Limpe o formulário após salvar
          this.produtoForm.reset();
        },
        error => {
          console.error('Erro ao salvar o produto', error);
          alert('Erro ao salvar o produto. Tente novamente.');
        }
      );
    } else {
      alert('Por favor, preencha todos os campos obrigatórios.');
    }
  }

  // Função para limpar o formulário
  limpar() {
    this.produtoForm.reset();
  }

  // Função para voltar à página anterior
  voltar() {
    this.router.navigate(['/']); // Ou use window.history.back() para voltar à página anterior
  }
}
