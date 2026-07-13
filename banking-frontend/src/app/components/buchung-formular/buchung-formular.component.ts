import { Component, OnInit, ViewChild } from '@angular/core';
import { NgForm } from '@angular/forms';
import { BuchungRequest } from '../../models/buchung.model';
import { BuchungControllerService, Konto, KontoControllerService } from '../../api/konto-service';

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

  constructor(
    private kontoService: KontoControllerService, 
    private buchungService: BuchungControllerService
  ) {}

  ngOnInit(): void {
    this.kontoService.getAllKonten('body', false).subscribe({
      next: (data) => this.konten = data,
      error: () => this.fehlerMeldung = 'Konten konnten nicht geladen werden.'
    });
  }

  onSubmit(form: NgForm): void {
    if (!this.kontoId || this.betrag === null || !this.beschreibung) return;
    this.laden = true;
    this.erfolgsMeldung = '';
    this.fehlerMeldung = '';

    const buchungRequest: BuchungRequest = {
      betrag: this.betrag!,
      beschreibung: this.beschreibung,
      kontoId: +this.kontoId
    };

    this.buchungService.createBuchung(buchungRequest, 'body', false).subscribe({
      next: (updated) => {
        this.laden = false;
        this.erfolgsMeldung = 'Buchung erfolgreich.';
        this.betrag = null;
        this.beschreibung = '';
        form.resetForm();
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
