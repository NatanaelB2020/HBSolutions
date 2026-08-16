import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { MenuComponent } from './menu/menu.component';
import { ClienteFormComponent } from './cliente-form/cliente-form.component';
import { authGuard } from '../app/security/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'menu', component: MenuComponent, canActivate: [authGuard] },
  {
    path: 'clientes',
    loadComponent: () => import('./clientes/cliente-list/cliente-list.component').then(m => m.ClienteListComponent),
    canActivate: [authGuard]
  },
  {
    path: 'dashboard',
    loadComponent: () => import('./dashboard/dashboard.component').then(m => m.DashboardComponent),
    canActivate: [authGuard]
  },
  {
    path: 'leads',
    loadComponent: () => import('./leads/lead-list/lead-list.component').then(m => m.LeadListComponent),
    canActivate: [authGuard]
  },
  {
    path: 'leads/novo',
    loadComponent: () => import('./leads/lead-form/lead-form.component').then(m => m.LeadFormComponent),
    canActivate: [authGuard]
  },
  {
    path: 'leads/:id/editar',
    loadComponent: () => import('./leads/lead-form/lead-form.component').then(m => m.LeadFormComponent),
    canActivate: [authGuard]
  },
  {
    path: 'leads/importar',
    loadComponent: () => import('./leads/lead-import/lead-import.component').then(m => m.LeadImportComponent),
    canActivate: [authGuard]
  },
  {
    path: 'pipeline',
    loadComponent: () => import('./oportunidades/pipeline/pipeline.component').then(m => m.PipelineComponent),
    canActivate: [authGuard]
  },
  {
    path: 'oportunidades/:id',
    loadComponent: () => import('./oportunidades/oportunidade-detail/oportunidade-detail.component').then(m => m.OportunidadeDetailComponent),
    canActivate: [authGuard]
  },
  {
    path: 'oportunidades/:id/editar',
    loadComponent: () => import('./oportunidades/oportunidade-edit/oportunidade-edit.component').then(m => m.OportunidadeEditComponent),
    canActivate: [authGuard]
  },
  {
    path: 'oportunidades/:id/fechar',
    loadComponent: () => import('./oportunidades/oportunidade-fechar/oportunidade-fechar.component').then(m => m.OportunidadeFecharComponent),
    canActivate: [authGuard]
  },
  {
    path: 'atividades',
    loadComponent: () => import('./atividades/atividade-list/atividade-list.component').then(m => m.AtividadeListComponent),
    canActivate: [authGuard]
  }
];
