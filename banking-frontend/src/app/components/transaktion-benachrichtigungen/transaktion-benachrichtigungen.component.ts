import { Component, OnInit } from '@angular/core';
import { TransaktionBenachrichtigung } from '../../models/benachrichtigung.model';
import { BenachrichtigungService } from '../../services/benachrichtigung.service';

@Component({
  selector: 'app-transaktion-benachrichtigungen',
  templateUrl: './transaktion-benachrichtigungen.component.html',
  styleUrl: '../benachrichtigungen-shared/benachrichtigungen-shared.css'
})
export class TransaktionBenachrichtigungenComponent implements OnInit {
  benachrichtigungen: TransaktionBenachrichtigung[] = [];
  quelleIbanFilter = '';
  zielIbanFilter = '';
  quelleInhaberFilter = '';
  zielInhaberFilter = '';
  betragFilter: number | undefined;
  vonFilter = '';
  bisFilter = '';

  constructor(private benachrichtigungsService: BenachrichtigungService) {}

  ngOnInit(): void {
    this.laden();
  }

  laden(): void {
    this.benachrichtigungsService.getTransaktionen({
      quelleIban: this.quelleIbanFilter,
      zielIban: this.zielIbanFilter,
      quelleInhaber: this.quelleInhaberFilter,
      zielInhaber: this.zielInhaberFilter,
      betrag: this.betragFilter,
      von: this.vonFilter,
      bis: this.bisFilter
    }).subscribe(benachrichtigungen => {
      this.benachrichtigungen = benachrichtigungen;
    });
  }

  route(benachrichtigung: TransaktionBenachrichtigung): string {
    return [benachrichtigung.quelleIban, benachrichtigung.zielIban]
      .filter((value): value is string => Boolean(value))
      .join(' -> ') || 'Unbekannt';
  }

  zuruecksetzen(): void {
    this.quelleIbanFilter = '';
    this.zielIbanFilter = '';
    this.quelleInhaberFilter = '';
    this.zielInhaberFilter = '';
    this.betragFilter = undefined;
    this.vonFilter = '';
    this.bisFilter = '';
    this.laden();
  }
}
