import { Injectable } from '@angular/core';
import { Observable } from 'rxjs';
import { Benachrichtigung } from '../models/benachrichtigung.model';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class BenachrichtigungService {
  private apiUrl: string = 'http://localhost:8082/api/benachrichtigungen';

  constructor(private http: HttpClient) { }

  getAll() : Observable<Benachrichtigung[]> {
    return this.http.get<Benachrichtigung[]>(this.apiUrl); 
  }

}
