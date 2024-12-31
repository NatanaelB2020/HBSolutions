import { Component } from '@angular/core';
import { EmpresaFormComponent } from '../empresa-form/empresa-form.component';
import { ClienteFormComponent } from '../cliente-form/cliente-form.component';
import { ProdutoFormComponent } from '../produto-form/produto-form.component';
import { CommonModule } from '@angular/common';
  // Importe o componente aqui

@Component({
  selector: 'app-menu',
  standalone: true,  // Indica que o MenuComponent também é standalone
  imports: [CommonModule, EmpresaFormComponent, ClienteFormComponent, ProdutoFormComponent],  // Aqui você importa o componente
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class MenuComponent {
  menuAtivo: string = 'empresa'; // Define o menu ativo inicial

  selecionarMenu(menu: string) {
    this.menuAtivo = menu;
  }
}
