import { Component, EventEmitter, OnInit, Output } from '@angular/core';
import { Konto } from '../../models/konto.model';
import { KontoService } from '../../services/konto.service';

@Component({
  selector: 'app-filter-leiste',
  templateUrl: './filter-leiste.component.html',
  styleUrl: './filter-leiste.component.css'
})
export class FilterLeisteComponent implements OnInit {
  konten: Konto[] = [];
  ibanFilter: string = '';
  filterVon: string = '';
  filterBis: string = '';

  @Output() 
  filterGeaendert = new EventEmitter<{ iban: string; von: string; bis: string }>();

  constructor(private kontoService: KontoService) {}

  ngOnInit(): void {
    this.kontoService.getAll().subscribe({
      next: (data: Konto[]) => this.konten = data,
      error: (err) => console.error('Fehler beim Laden der Konten:', err)
    });
  }

  laden(): void {
    this.filterGeaendert.emit({
      iban: this.ibanFilter,
      von: this.filterVon,
      bis: this.filterBis
    });
  }
}
