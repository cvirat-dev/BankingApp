import { Injectable } from '@angular/core';
import { map, Observable, retry } from 'rxjs';
import {
  Benachrichtigung,
  BenachrichtigungTyp,
  BuchungBenachrichtigung,
  KontoBenachrichtigung,
  TransaktionBenachrichtigung
} from '../models/benachrichtigung.model';
import { BenachrichtigungControllerService } from '../api/benachrichtigung-service/api/benachrichtigungController.service';

export interface BenachrichtigungFilter {
  typ?: BenachrichtigungTyp;
  iban?: string;
  von?: string; // datetime-local string
  bis?: string; // datetime-local string
}

export interface KontoBenachrichtigungFilter {
  iban?: string;
  inhaber?: string;
  aktion?: KontoBenachrichtigung.AktionEnum;
  von?: string;
  bis?: string;
}

export interface BuchungBenachrichtigungFilter {
  iban?: string;
  inhaber?: string;
  betrag?: number;
  von?: string;
  bis?: string;
}

export interface TransaktionBenachrichtigungFilter {
  quelleIban?: string;
  zielIban?: string;
  quelleInhaber?: string;
  zielInhaber?: string;
  betrag?: number;
  von?: string;
  bis?: string;
}

@Injectable({
  providedIn: 'root'
})
export class BenachrichtigungService {
  constructor(private benachrichtigungController: BenachrichtigungControllerService) { }

  getAll(filters?: BenachrichtigungFilter): Observable<Benachrichtigung[]> {
    return this.benachrichtigungController.all(
      filters?.typ,
      undefined,
      undefined,
      undefined,
      undefined,
      undefined,
      filters?.iban,
      undefined,
      undefined,
      undefined,
      undefined,
      undefined,
      undefined,
      undefined,
      filters?.von,
      filters?.bis,
      'body',
      false
    ).pipe(
      retry({ count: 3, delay: 2000 }),
      map(benachrichtigungen => benachrichtigungen as Benachrichtigung[])
    );
  }

  getKonto(filters?: KontoBenachrichtigungFilter): Observable<KontoBenachrichtigung[]> {
    return this.benachrichtigungController.allKonto(
      undefined,
      this.optionalText(filters?.iban),
      this.optionalText(filters?.inhaber),
      filters?.aktion,
      this.optionalText(filters?.von),
      this.optionalText(filters?.bis),
      'body',
      false
    ).pipe(
      retry({ count: 3, delay: 2000 })
    );
  }

  getBuchungen(filters?: BuchungBenachrichtigungFilter): Observable<BuchungBenachrichtigung[]> {
    return this.benachrichtigungController.allBuchungen(
      undefined,
      undefined,
      this.optionalText(filters?.iban),
      this.optionalText(filters?.inhaber),
      filters?.betrag,
      this.optionalText(filters?.von),
      this.optionalText(filters?.bis),
      'body',
      false
    ).pipe(
      retry({ count: 3, delay: 2000 })
    );
  }

  getTransaktionen(filters?: TransaktionBenachrichtigungFilter): Observable<TransaktionBenachrichtigung[]> {
    return this.benachrichtigungController.allTransaktionen(
      undefined,
      undefined,
      undefined,
      this.optionalText(filters?.quelleIban),
      this.optionalText(filters?.zielIban),
      this.optionalText(filters?.quelleInhaber),
      this.optionalText(filters?.zielInhaber),
      filters?.betrag,
      this.optionalText(filters?.von),
      this.optionalText(filters?.bis),
      'body',
      false
    ).pipe(
      retry({ count: 3, delay: 2000 })
    );
  }

  private optionalText(value: string | undefined): string | undefined {
    const trimmed = value?.trim();
    return trimmed ? trimmed : undefined;
  }

}
