import { Component } from '@angular/core';
import { CommonModule } from '@angular/common';
import { LoginComponent } from './login/login.component';

@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, LoginComponent], // Importa o LoginComponent
  template: `<app-login></app-login>`, // Mostra o login direto
  styleUrls: ['./app.component.css']
})
export class AppComponent {}
