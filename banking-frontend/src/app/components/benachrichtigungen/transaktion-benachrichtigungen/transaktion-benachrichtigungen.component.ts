import { Component, OnInit } from '@angular/core';
import { TransaktionBenachrichtigung } from '../../../models/benachrichtigung.model';
import { BenachrichtigungService } from '../../../services/benachrichtigung.service';
import { filterSpeichern, getFilterVisibility, getStoredFilter, saveFilterVisibility } from '../shared/filter-storage.util';
import { Konto } from '../../../api/konto-service/model/konto';
import { KontoControllerService } from '../../../api/konto-service/api/kontoController.service';

interface TransaktionFilterZustand {
  quelleIbanFilter: string;
  zielIbanFilter: string;
  quelleInhaberFilter: string;
  zielInhaberFilter: string;
  betragFilter: number | undefined;
  vonFilter: string;
  bisFilter: string;
}

@Component({
  selector: 'app-transaktion-benachrichtigungen',
  templateUrl: './transaktion-benachrichtigungen.component.html',
  styleUrl: '../shared/benachrichtigungen-shared.css'
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
  istFilterEingeklappt = true;

  private readonly filterValueStorageKey = 'benachrichtigungen.filter.transaktion';
  private readonly filterVisibilitySaveKey = 'benachrichtigungen.filter.transaktion.visibility';
  konten: Konto[] = [];

  constructor(
    private benachrichtigungsService: BenachrichtigungService,
    private kontoService: KontoControllerService) {}

  ngOnInit(): void {
    Object.assign(this, getStoredFilter<TransaktionFilterZustand>(this.filterValueStorageKey));
    this.istFilterEingeklappt = getFilterVisibility(this.filterVisibilitySaveKey) ?? true;
    this.kontoService.getAllKonten('body', false).subscribe({
      next: (data) => this.konten = data,
      error: () => console.error('Konten konnten nicht geladen werden.')
    });
    this.load();
  }

  load(): void {
    filterSpeichern(this.filterValueStorageKey, {
      quelleIbanFilter: this.quelleIbanFilter,
      zielIbanFilter: this.zielIbanFilter,
      quelleInhaberFilter: this.quelleInhaberFilter,
      zielInhaberFilter: this.zielInhaberFilter,
      betragFilter: this.betragFilter,
      vonFilter: this.vonFilter,
      bisFilter: this.bisFilter
    });
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

  reset(): void {
    this.quelleIbanFilter = '';
    this.zielIbanFilter = '';
    this.quelleInhaberFilter = '';
    this.zielInhaberFilter = '';
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
