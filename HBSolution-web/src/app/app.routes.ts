// src/app/app.routes.ts
import { Routes, provideRouter } from '@angular/router';
import { LoginComponent } from './login/login.component';

export const routes: Routes = [
  { path: '', redirectTo: 'login', pathMatch: 'full' },
  { path: 'login', component: LoginComponent }
];

export const appConfig = {
  providers: [
    provideRouter(routes)
  ]
};
