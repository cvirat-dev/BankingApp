import { Component } from '@angular/core';
import { NgForm } from '@angular/forms';
import { Router } from '@angular/router';
import { Konto } from '../../models/konto.model';
import { KontoService } from '../../services/konto.service';

@Component({
  selector: 'app-konto-erstellen.component',
  templateUrl: './konto-erstellen.component.html',
  styleUrl: './konto-erstellen.component.css'
})
export class KontoErstellenComponent {

  inhaber: string = '';
  kontostand: number | null = null;
  laden: boolean = false;
  erfolgsMeldung: string = '';
  fehlerMeldung: string = '';

  constructor(private kontoService: KontoService) {}

  onSubmit(): void {
    if (!this.inhaber || this.kontostand === null) return;
    this.laden = true;
    this.erfolgsMeldung = '';
    this.fehlerMeldung = '';

    const neuesKonto: Partial<Konto> = { inhaber: this.inhaber, kontostand: this.kontostand };

    this.kontoService.createKonto(neuesKonto).subscribe({
      next: (konto) => {
        this.laden = false;
        this.erfolgsMeldung = `Konto für „${konto.inhaber}" wurde erfolgreich erstellt.`;
      },
      error: () => {
        this.laden = false;
        this.fehlerMeldung = 'Konto konnte nicht erstellt werden. Bitte erneut versuchen.';
      }
    });
  }

  zuruecksetzen(form: NgForm): void {
    form.resetForm();
    this.inhaber = '';
    this.kontostand = null;
    this.erfolgsMeldung = '';
    this.fehlerMeldung = '';
  }
}
