import { Component, OnInit } from '@angular/core';
import { KontoBenachrichtigung } from '../../models/benachrichtigung.model';
import { BenachrichtigungService } from '../../services/benachrichtigung.service';

@Component({
  selector: 'app-konto-benachrichtigungen',
  templateUrl: './konto-benachrichtigungen.component.html',
  styleUrl: '../benachrichtigungen-shared/benachrichtigungen-shared.css'
})
export class KontoBenachrichtigungenComponent implements OnInit {
  benachrichtigungen: KontoBenachrichtigung[] = [];
  ibanFilter = '';
  inhaberFilter = '';
  aktionFilter: KontoBenachrichtigung.AktionEnum | '' = '';
  vonFilter = '';
  bisFilter = '';

  readonly aktionen: KontoBenachrichtigung.AktionEnum[] = [
    'ERSTELLEN',
    'AKTUALISIEREN',
    'LOESCHEN'
  ];

  constructor(private benachrichtigungsService: BenachrichtigungService) {}

  ngOnInit(): void {
    this.laden();
  }

  laden(): void {
    this.benachrichtigungsService.getKonto({
      iban: this.ibanFilter,
      inhaber: this.inhaberFilter,
      aktion: this.aktionFilter || undefined,
      von: this.vonFilter,
      bis: this.bisFilter
    }).subscribe(benachrichtigungen => {
      this.benachrichtigungen = benachrichtigungen;
    });
  }

  zuruecksetzen(): void {
    this.ibanFilter = '';
    this.inhaberFilter = '';
    this.aktionFilter = '';
    this.vonFilter = '';
    this.bisFilter = '';
    this.laden();
  }
}
