import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { EmpresaFormComponent } from '../empresa-form/empresa-form.component';
import { ClienteFormComponent } from '../cliente-form/cliente-form.component';
import { ProdutoFormComponent } from '../produto-form/produto-form.component';
import { AuthService } from '../service/auth.service';
import { Router } from '@angular/router';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [CommonModule, EmpresaFormComponent, ClienteFormComponent, ProdutoFormComponent],
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class MenuComponent {
  menuAtivo: string = 'empresa';

  constructor(private authService: AuthService, private router: Router) {}

  selecionarMenu(menu: string) {
    this.menuAtivo = menu;
  }

  logout(): void {
    this.authService.logout();       // remove token e dados do usuário
    this.router.navigate(['/']);     // redireciona para login
  }
}
