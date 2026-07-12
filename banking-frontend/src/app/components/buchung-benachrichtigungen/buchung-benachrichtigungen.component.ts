import { Component, OnInit } from '@angular/core';
import { BuchungBenachrichtigung } from '../../models/benachrichtigung.model';
import { BenachrichtigungService } from '../../services/benachrichtigung.service';

@Component({
  selector: 'app-buchung-benachrichtigungen',
  templateUrl: './buchung-benachrichtigungen.component.html',
  styleUrl: '../benachrichtigungen-shared/benachrichtigungen-shared.css'
})
export class BuchungBenachrichtigungenComponent implements OnInit {
  benachrichtigungen: BuchungBenachrichtigung[] = [];
  ibanFilter = '';
  inhaberFilter = '';
  betragFilter: number | undefined;
  vonFilter = '';
  bisFilter = '';

  constructor(private benachrichtigungsService: BenachrichtigungService) {}

  ngOnInit(): void {
    this.laden();
  }

  laden(): void {
    this.benachrichtigungsService.getBuchungen({
      iban: this.ibanFilter,
      inhaber: this.inhaberFilter,
      betrag: this.betragFilter,
      von: this.vonFilter,
      bis: this.bisFilter
    }).subscribe(benachrichtigungen => {
      this.benachrichtigungen = benachrichtigungen;
    });
  }

  zuruecksetzen(): void {
    this.ibanFilter = '';
    this.inhaberFilter = '';
    this.betragFilter = undefined;
    this.vonFilter = '';
    this.bisFilter = '';
    this.laden();
  }
}
