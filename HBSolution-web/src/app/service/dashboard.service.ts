import { HttpClient } from '@angular/common/http';
import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Dashboard } from '../models/dashboard.model';

@Injectable({
    providedIn: 'root'
})
export class DashboardService {
    private readonly baseUrl = 'http://localhost:8080/api/dashboard';

    constructor(private http: HttpClient) { }

    getDashboard(): Observable<Dashboard> {
        return this.http.get<Dashboard>(this.baseUrl);
    }
}
