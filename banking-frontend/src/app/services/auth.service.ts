import { HttpHeaders } from '@angular/common/http';
import { Injectable, computed, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { tap, map } from 'rxjs/operators';
import { Observable } from 'rxjs';
import { environment } from '../../environments/environment';

const STORAGE_KEY = 'banking-auth';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  private readonly token = signal<string | null>(sessionStorage.getItem(STORAGE_KEY));
  readonly isLoggedIn = computed(() => this.token() !== null);

  constructor(private http: HttpClient) { }

  login(username: string, password: string): Observable<void> {
    const token = btoa(`${username}:${password}`);
    const headers = new HttpHeaders({ Authorization: `Basic ${token}` });
    return this.http.get(`${environment.kontoServiceUrl}/api/me`, { headers }).pipe(
      tap(() => {
        sessionStorage.setItem(STORAGE_KEY, token);
        this.token.set(token);
      }),
      map(() => undefined)
    );
  }

  logout(): void {
    sessionStorage.removeItem(STORAGE_KEY);
    this.token.set(null);
  }

  authorizationHeader(): string | null {
    const token = this.token();
    return token ? `Basic ${token}` : null;
  }
}
