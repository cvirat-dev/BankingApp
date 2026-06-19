import { Component, OnInit } from '@angular/core';
import { Konto } from '../../models/konto.model';
import { KontoService } from '../../services/konto.service';
import { NgForm } from '@angular/forms';
import { Transaktion } from '../../models/transaktion.model';

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
  transaktion: Transaktion | null = null;

  constructor(private kontoService: KontoService) {}

  ngOnInit(): void {
    this.kontoService.getAll().subscribe({
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
      quelleKontoId: this.quelleKontoId,
      zielKontoId: this.zielKontoId,
      betrag: this.betrag,
      beschreibung: this.beschreibung
    };

    this.kontoService.createTransaktion(this.transaktion).subscribe({
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
