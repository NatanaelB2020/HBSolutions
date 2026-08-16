import { CommonModule } from '@angular/common';
import { Component } from '@angular/core';
import { ToastService } from './toast.service';

@Component({
    selector: 'app-toast',
    standalone: true,
    imports: [CommonModule],
    templateUrl: './toast.component.html',
    styleUrl: './toast.component.css'
})
export class ToastComponent {
    toasts$;

    constructor(private toastService: ToastService) {
        this.toasts$ = this.toastService.toasts$;
    }

    trackByToast(index: number, toast: { id: number }): number {
        return toast.id;
    }
}
