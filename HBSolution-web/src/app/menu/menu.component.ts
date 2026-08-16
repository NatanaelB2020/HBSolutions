import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { Router, RouterModule } from '@angular/router';
import { DashboardComponent } from '../dashboard/dashboard.component';
import { LeadListComponent } from '../leads/lead-list/lead-list.component';
import { LeadImportComponent } from '../leads/lead-import/lead-import.component';
import { PipelineComponent } from '../oportunidades/pipeline/pipeline.component';
import { AtividadeListComponent } from '../atividades/atividade-list/atividade-list.component';
import { EmpresaFormComponent } from '../empresa-form/empresa-form.component';
import { ClienteFormComponent } from '../cliente-form/cliente-form.component';
import { ProdutoFormComponent } from '../produto-form/produto-form.component';
import { AuthService } from '../service/auth.service';

@Component({
  selector: 'app-menu',
  standalone: true,
  imports: [
    CommonModule,
    RouterModule,
    DashboardComponent,
    LeadListComponent,
    LeadImportComponent,
    PipelineComponent,
    AtividadeListComponent,
    EmpresaFormComponent,
    ClienteFormComponent,
    ProdutoFormComponent
  ],
  templateUrl: './menu.component.html',
  styleUrls: ['./menu.component.css']
})
export class MenuComponent {
  menuAtivo: string = 'empresa';

  constructor(private authService: AuthService, private router: Router) { }

  selecionarMenu(menu: string) {
    this.menuAtivo = menu;
  }

  logout(): void {
    this.authService.logout();
    this.router.navigate(['/']);
  }
}
