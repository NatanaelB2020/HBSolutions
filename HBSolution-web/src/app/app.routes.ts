import { Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { MenuComponent } from './menu/menu.component';
import { ClienteFormComponent } from './cliente-form/cliente-form.component';
import { authGuard } from '../app/security/auth.guard';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent },
  { path: 'menu', component: MenuComponent, canActivate: [authGuard] },
  { path: 'clientes', component: ClienteFormComponent, canActivate: [authGuard] }
];
