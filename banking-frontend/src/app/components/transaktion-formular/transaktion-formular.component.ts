import { Component, OnInit } from '@angular/core';
import { TransaktionRequest } from '../../models/transaktion.model';
import { NgForm } from '@angular/forms';
import { Konto, KontoControllerService, TransaktionControllerService } from '../../api/konto-service';

@Component({
  selector: 'app-transaktion-formular',
  templateUrl: './transaktion-formular.component.html',
  styleUrl: './transaktion-formular.component.css'
})
export class TransaktionFormularComponent implements OnInit {
  
  konten: Konto[] = [];
  quelleKontoId: number | '' = '';
  zielKontoId: number | '' = '';
  betrag: number | null = null;
  beschreibung: string = '';
  laden: boolean = false;
  erfolgsMeldung: string = '';
  fehlerMeldung: string = '';
  transaktion: TransaktionRequest | null = null;

  constructor(
    private kontoService: KontoControllerService,
    private transaktionService: TransaktionControllerService
  ) {}

  ngOnInit(): void {
    this.kontoService.getAllKonten('body', false).subscribe({
      next: (data) => this.konten = data,
      error: () => this.fehlerMeldung = 'Konten konnten nicht geladen werden.'
    });
  }

  get gleicheKontenAusgewaehlt(): boolean {
    return !!this.quelleKontoId && !!this.zielKontoId && Number(this.quelleKontoId) === Number(this.zielKontoId);
  }

  onSubmit(form: NgForm): void {
    if (
      !this.quelleKontoId || 
      !this.zielKontoId || 
      this.gleicheKontenAusgewaehlt ||
      this.betrag === null ||
      this.betrag <= 0 || 
      !this.beschreibung) 
    {
      if (this.gleicheKontenAusgewaehlt) {
        this.fehlerMeldung = 'Quellkonto und Zielkonto duerfen nicht identisch sein.';
      }
      return;
    }
    
    this.laden = true;
    this.erfolgsMeldung = '';
    this.fehlerMeldung = '';

    this.transaktion = {
      quelleKontoId: Number(this.quelleKontoId),
      zielKontoId: Number(this.zielKontoId),
      betrag: this.betrag,
      beschreibung: this.beschreibung
    };

    this.transaktionService.createTransaktion(this.transaktion, 'body', false).subscribe({
      next: (updated) => {
        this.laden = false;
        this.erfolgsMeldung = 'Transaktion erfolgreich.';
        this.betrag = null;
        this.beschreibung = '';
        form.resetForm();
      },
      error: () => {
        this.laden = false;
        this.fehlerMeldung = 'Transaktion fehlgeschlagen. Bitte erneut versuchen.';
      }
    });
  }

  zuruecksetzen(form: NgForm): void {
    form.resetForm();
    this.quelleKontoId = '';
    this.zielKontoId = '';
    this.betrag = null;
    this.beschreibung = '';
    this.erfolgsMeldung = '';
    this.fehlerMeldung = '';
  }

}
