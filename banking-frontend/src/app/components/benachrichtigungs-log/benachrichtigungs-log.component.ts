import { Component, OnInit } from '@angular/core';
import { Benachrichtigung, BenachrichtigungTyp } from '../../models/benachrichtigung.model';
import { BenachrichtigungService } from '../../services/benachrichtigung.service';
import { BenachrichtigungsTab } from '../benachrichtigungs-tabs/benachrichtigungs-tabs.component';

@Component({
  selector: 'app-benachrichtigungs-log',
  templateUrl: './benachrichtigungs-log.component.html',
  styleUrl: './benachrichtigungs-log.component.css'
})
export class BenachrichtigungsLogComponent implements OnInit {

  benachrichtigungen: Benachrichtigung[] = [];
  ladevorgang: boolean = true;
  aktuellerTyp: BenachrichtigungTyp = 'KONTO';

  constructor(private benachrichtigungsService: BenachrichtigungService) {}

  ngOnInit(): void {
    this.benachrichtigungsService.getAll().subscribe({
      next: (data: Benachrichtigung[]) => {
        this.benachrichtigungen = data;
        this.ladevorgang = false;
      },
      error: (error) => {
        this.ladevorgang = false;
        console.error('Fehler beim Laden der Benachrichtigungen:', error);
      }
    });
  }

  get tabs(): BenachrichtigungsTab[] {
    return [
      { typ: 'KONTO',       label: 'Konto',       anzahl: this.benachrichtigungen.filter(b => b.typ === 'KONTO').length },
      { typ: 'TRANSAKTION', label: 'Transaktion', anzahl: this.benachrichtigungen.filter(b => b.typ === 'TRANSAKTION').length }
    ];
  }

  get gefilterteBenachrichtigungen(): Benachrichtigung[] {
    return this.benachrichtigungen.filter(b => b.typ === this.aktuellerTyp);
  }

  get aktuellerTypLabel(): string {
    return this.aktuellerTyp === 'KONTO' ? 'Konto' : 'Transaktion';
  }

  onTypGewaehlt(typ: BenachrichtigungTyp): void {
    this.aktuellerTyp = typ;
  }

}
