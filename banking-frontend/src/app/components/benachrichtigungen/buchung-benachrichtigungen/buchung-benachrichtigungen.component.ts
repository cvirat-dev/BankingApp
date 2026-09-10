import { Component, OnInit } from '@angular/core';
import { BuchungBenachrichtigung } from '../../../models/benachrichtigung.model';
import { BenachrichtigungService } from '../../../services/benachrichtigung.service';
import { filterSpeichern, getFilterVisibility, getStoredFilter, saveFilterVisibility } from '../shared/filter-storage.util';
import { Konto } from '../../../api/konto-service/model/konto';
import { KontoControllerService } from '../../../api/konto-service/api/kontoController.service';

interface BuchungFilterZustand {
  ibanFilter: string;
  inhaberFilter: string;
  betragFilter: number | undefined;
  vonFilter: string;
  bisFilter: string;
}

@Component({
  selector: 'app-buchung-benachrichtigungen',
  templateUrl: './buchung-benachrichtigungen.component.html',
  styleUrl: '../shared/benachrichtigungen-shared.css'
})
export class BuchungBenachrichtigungenComponent implements OnInit {
  benachrichtigungen: BuchungBenachrichtigung[] = [];
  ibanFilter = '';
  inhaberFilter = '';
  betragFilter: number | undefined;
  vonFilter = '';
  bisFilter = '';
  istFilterEingeklappt = true;

  private readonly filterValueStorageKey = 'benachrichtigungen.filter.buchung';
  private readonly filterVisibilitySaveKey = 'benachrichtigungen.filter.buchung.visibility'; 
  konten: Konto[] = [];

  constructor(private benachrichtigungsService: BenachrichtigungService,
              private kontoService: KontoControllerService) {}

  ngOnInit(): void {
    Object.assign(this, getStoredFilter<BuchungFilterZustand>(this.filterValueStorageKey));
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
      betragFilter: this.betragFilter,
      vonFilter: this.vonFilter,
      bisFilter: this.bisFilter
    });
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

  reset(): void {
    this.ibanFilter = '';
    this.inhaberFilter = '';
    this.betragFilter = undefined;
    this.vonFilter = '';
    this.bisFilter = '';
    this.load();
  }

  toggleFilterVisibility(): void {
    this.istFilterEingeklappt = !this.istFilterEingeklappt;
    saveFilterVisibility(this.filterVisibilitySaveKey, this.istFilterEingeklappt);
  }
}
