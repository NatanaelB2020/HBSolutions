import { Component } from '@angular/core';
import { RouterOutlet } from '@angular/router';
import { EmpresaFormComponent } from './empresa-form/empresa-form.component'; // Importando o componente
import { CommonModule } from '@angular/common';
import { MenuComponent } from './menu/menu.component';


@Component({
  selector: 'app-root',
  standalone: true,
  imports: [CommonModule, EmpresaFormComponent, MenuComponent ], // Adicionando o componente em imports
  templateUrl: './app.component.html',
  styleUrls: ['./app.component.css']
})
export class AppComponent {
  title = 'HBSolution-web';
  mostrarFormulario: boolean = false;
}
