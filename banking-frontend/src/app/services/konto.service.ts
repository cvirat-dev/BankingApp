import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, retry } from 'rxjs';
import { Konto } from '../models/konto.model';
import { Buchung } from '../models/buchung.model';
import { Transaktion } from '../models/transaktion.model';

@Injectable({
  providedIn: 'root'
})
export class KontoService {
  private apiUrl: string = 'http://localhost:8081/api/konten';

  constructor(private http: HttpClient) { }

  getAll() : Observable<Konto[]> {
    return this.http.get<Konto[]>(this.apiUrl).pipe(
      retry({count: 3, delay: 2000})
    ); 
  }

  createKonto(konto: Partial<Konto>): Observable<Konto> {
    return this.http.post<Konto>(this.apiUrl, konto);
  }

  createBuchung(kontoId: number, transaktion: Partial<Buchung>): Observable<Buchung> {
    return this.http.post<Buchung>(`${this.apiUrl}/${kontoId}/buchung`, transaktion);
  }

  createTransaktion(transaktion: Partial<Transaktion>): Observable<Transaktion> {
    return this.http.post<Transaktion>(`${this.apiUrl}/transaktion`, transaktion);
  }

}
