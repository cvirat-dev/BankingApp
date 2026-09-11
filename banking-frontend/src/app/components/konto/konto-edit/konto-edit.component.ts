import { Component, OnInit } from '@angular/core';
import { ActivatedRoute, Router } from '@angular/router';
import { NgForm } from '@angular/forms';
import { Konto, KontoControllerService, KontoUpdateRequest } from '../../../api/konto-service';
import { KontoRequest } from '../../../api/konto-service/model/kontoRequest';

@Component({
  selector: 'app-konto-edit',
  templateUrl: './konto-edit.component.html',
  styleUrl: './konto-edit.component.css'
})
export class KontoEditComponent implements OnInit {

  konten: Konto[] = [];

  konto!: KontoRequest;
  id!: number;
  iban: string = '';
  inhaber: string = '';
  kontostand: number | null = null;

  load: boolean = true;
  speichern: boolean = false;
  ladeFehler: string = '';
  ladeHinweis: string = '';
  erfolgsMeldung: string = '';
  fehlerMeldung: string = '';
  
  selectedKontoId: any;

  constructor(
    private route: ActivatedRoute,
    private router: Router,
    private kontoService: KontoControllerService
  ) {}

  ngOnInit(): void {

    this.kontoService.getAllKonten('body', false).subscribe({
      next: (data: Konto[]) => {
        this.konten = Array.isArray(data) ? data : [];
        this.load = false;
      },
      error: (error) => {
        this.load = false;
        this.fehlerMeldung = 'Konten konnten nicht geladen werden.';
        console.error('Fehler beim Laden der Konten:', error);
      }
    });

    this.id = Number(this.route.snapshot.paramMap.get('id'));

    if (!this.id || this.id <= 0) {
      this.load = false;
      this.ladeHinweis = 'Kein Konto ausgewählt.';
      return;
    }

    this.selectedKontoId = this.id;
    this.loadKonto(this.id);
  }
  
  loadKonto(id: number) {
    if (!id) return;
    this.load = true;
    this.ladeFehler = '';
    this.ladeHinweis = '';

    this.kontoService.getKontoById(id, 'body', false).subscribe({
      next: (konto) => {
        this.id = konto.id ?? 0;
        this.iban = konto.iban ?? '';
        this.inhaber = konto.inhaber ?? '';
        this.kontostand = konto.kontostand ?? null;
        this.load = false;
      },
      error: () => {
        this.load = false;
        this.ladeFehler = 'Konto konnte nicht geladen werden.';
      }
    });
  }

  onSubmit(): void {
    if (!this.inhaber || this.kontostand === null) return;
    this.speichern = true;
    this.erfolgsMeldung = '';
    this.fehlerMeldung = '';

    const request: KontoUpdateRequest = { id: this.id, inhaber: this.inhaber };

    this.kontoService.updateKonto(this.id, request, 'body', false).subscribe({
      next: (konto) => {
        this.speichern = false;
        this.erfolgsMeldung = `Konto für „${konto.inhaber}" wurde erfolgreich aktualisiert.`;
      },
      error: () => {
        this.speichern = false;
        this.fehlerMeldung = 'Konto konnte nicht aktualisiert werden. Bitte erneut versuchen.';
      }
    });
  }

  abbrechen(form: NgForm): void {
    form.resetForm();
    this.router.navigate(['/konten']);
  }
}
