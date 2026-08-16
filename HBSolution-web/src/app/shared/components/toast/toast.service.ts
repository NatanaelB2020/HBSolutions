import { Injectable } from '@angular/core';
import { BehaviorSubject } from 'rxjs';

export type ToastType = 'success' | 'error' | 'info';

export interface Toast {
    id: number;
    message: string;
    type: ToastType;
}

@Injectable({ providedIn: 'root' })
export class ToastService {
    private readonly autoRemoveMs = 3000;
    private nextId = 1;

    readonly toasts$ = new BehaviorSubject<Toast[]>([]);

    sucesso(message: string): void {
        this.add('success', message);
    }

    erro(message: string): void {
        this.add('error', message);
    }

    info(message: string): void {
        this.add('info', message);
    }

    private add(type: ToastType, message: string): void {
        const toast: Toast = {
            id: this.nextId++,
            message,
            type
        };

        const atual = this.toasts$.value;
        this.toasts$.next([...atual, toast]);

        setTimeout(() => this.remove(toast.id), this.autoRemoveMs);
    }

    private remove(id: number): void {
        const atual = this.toasts$.value.filter((toast) => toast.id !== id);
        this.toasts$.next(atual);
    }
}
