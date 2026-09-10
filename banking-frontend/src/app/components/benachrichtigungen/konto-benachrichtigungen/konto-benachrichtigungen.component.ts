import { Component, OnInit } from '@angular/core';
import { KontoBenachrichtigung } from '../../../models/benachrichtigung.model';
import { BenachrichtigungService } from '../../../services/benachrichtigung.service';
import { filterSpeichern, getFilterVisibility, getStoredFilter, saveFilterVisibility } from '../shared/filter-storage.util';
import { Konto, KontoControllerService } from '../../../api/konto-service';

interface KontoFilterZustand {
  ibanFilter: string;
  inhaberFilter: string;
  aktionFilter: KontoBenachrichtigung.AktionEnum | '';
  vonFilter: string;
  bisFilter: string;
}

@Component({
  selector: 'app-konto-benachrichtigungen',
  templateUrl: './konto-benachrichtigungen.component.html',
  styleUrl: '../shared/benachrichtigungen-shared.css'
})
export class KontoBenachrichtigungenComponent implements OnInit {
  benachrichtigungen: KontoBenachrichtigung[] = [];
  ibanFilter = '';
  inhaberFilter = '';
  aktionFilter: KontoBenachrichtigung.AktionEnum | '' = '';
  vonFilter = '';
  bisFilter = '';
  istFilterEingeklappt = true;

  private readonly filterValueStorageKey = 'benachrichtigungen.filter.konto';
  private readonly filterVisibilitySaveKey = 'benachrichtigungen.filter.konto.visibility'; 
  readonly aktionen: KontoBenachrichtigung.AktionEnum[] = [
    'ERSTELLEN',
    'AKTUALISIEREN',
    'LOESCHEN'
  ];
  
  konten: Konto[] = [];

  constructor(
    private benachrichtigungsService: BenachrichtigungService,
    private kontoService: KontoControllerService) {}

  ngOnInit(): void {
    Object.assign(this, getStoredFilter<KontoFilterZustand>(this.filterValueStorageKey));
    this.istFilterEingeklappt = getFilterVisibility(this.filterVisibilitySaveKey) ?? true;
    this.kontoService.getAllKonten('body', false).subscribe({
      next: (data) => this.konten = data,
      error: () => console.error('Konten konnten nicht geladen werden.')
    });
    this.load();
  }

  load(): void {
    filterSpeichern(this.filterValueStorageKey, {
      ibanFilter: this.ibanFilter,
      inhaberFilter: this.inhaberFilter,
      aktionFilter: this.aktionFilter,
      vonFilter: this.vonFilter,
      bisFilter: this.bisFilter
    });
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

  reset(): void {
    this.ibanFilter = '';
    this.inhaberFilter = '';
    this.aktionFilter = '';
    this.vonFilter = '';
    this.bisFilter = '';
    this.load();
  }

  toggleFilterVisibility(): void {
    this.istFilterEingeklappt = !this.istFilterEingeklappt;
    saveFilterVisibility(this.filterVisibilitySaveKey, this.istFilterEingeklappt);
  }
}
