import { Injectable } from '@angular/core';
import { Observable, retry } from 'rxjs';
import { Benachrichtigung, BenachrichtigungTyp } from '../models/benachrichtigung.model';
import { HttpClient, HttpParams } from '@angular/common/http';

export interface BenachrichtigungFilter {
  typ?: BenachrichtigungTyp;
  iban?: string;
  von?: string; // datetime-local string
  bis?: string; // datetime-local string
}

@Injectable({
  providedIn: 'root'
})
export class BenachrichtigungService {
  private apiUrl: string = 'http://localhost:8082/api/benachrichtigungen';

  constructor(private http: HttpClient) { }

  getAll(filters?: BenachrichtigungFilter): Observable<Benachrichtigung[]> {
    let params = new HttpParams();

    if (filters?.typ) {
      params = params.set('typ', filters.typ);
    }

    if (filters?.iban) {
      params = params.set('iban', filters.iban);
    }

    if (filters?.von) {
      params = params.set('von', filters.von);
    }

    if (filters?.bis) {
      params = params.set('bis', filters.bis);
    }

    return this.http.get<Benachrichtigung[]>(this.apiUrl, { params }).pipe(
      retry({ count: 3, delay: 2000 })
    );
  }

}
