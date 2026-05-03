import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { Konto } from '../models/konto.model';
import { Transaktion } from '../models/transaktion.model';

@Injectable({
  providedIn: 'root'
})
export class KontoService {
  private apiUrl: string = 'http://localhost:8081/api/konten';

  constructor(private http: HttpClient) { }

  getKonten() : Observable<Konto[]> {
    return this.http.get<Konto[]>(this.apiUrl); 
  }

  createKonto(konto: Partial<Konto>): Observable<Konto> {
    return this.http.post<Konto>(this.apiUrl, konto);
  }

  createBuchung(kontoId: number, transaktion: Partial<Transaktion>): Observable<Transaktion> {
    return this.http.post<Transaktion>(`${this.apiUrl}/${kontoId}/buchung`, transaktion);
  }

}
