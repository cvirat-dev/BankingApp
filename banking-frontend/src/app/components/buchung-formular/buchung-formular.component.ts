import { Component, OnInit, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Konto } from '../../models/konto.model';
import { KontoService } from '../../services/konto.service';

@Component({
  selector: 'app-buchung-formular',
  templateUrl: './buchung-formular.component.html',
  styleUrl: './buchung-formular.component.css'
})
export class BuchungFormularComponent implements OnInit {

  konten: Konto[] = [];
  kontoId: number | '' = '';
  betrag: number | null = null;
  beschreibung: string = '';
  laden: boolean = false;
  erfolgsMeldung: string = '';
  fehlerMeldung: string = '';

  constructor(private kontoService: KontoService) {}

  ngOnInit(): void {
    this.kontoService.getAll().subscribe({
      next: (data) => this.konten = data,
      error: () => this.fehlerMeldung = 'Konten konnten nicht geladen werden.'
    });
  }

  onSubmit(): void {
    if (!this.kontoId || this.betrag === null || !this.beschreibung) return;
    this.laden = true;
    this.erfolgsMeldung = '';
    this.fehlerMeldung = '';

    this.kontoService.createBuchung(+this.kontoId, { betrag: this.betrag!, beschreibung: this.beschreibung }).subscribe({
      next: (updated) => {
        this.laden = false;
        this.erfolgsMeldung = 'Buchung erfolgreich.';
        this.betrag = null;
        this.beschreibung = '';
      },
      error: () => {
        this.laden = false;
        this.fehlerMeldung = 'Buchung fehlgeschlagen. Bitte erneut versuchen.';
      }
    });
  }

  zuruecksetzen(form: NgForm): void {
    form.resetForm();
    this.kontoId = '';
    this.betrag = null;
    this.beschreibung = '';
    this.erfolgsMeldung = '';
    this.fehlerMeldung = '';
  }
}
