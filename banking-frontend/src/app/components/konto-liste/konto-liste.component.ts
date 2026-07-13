import { Component, OnInit } from '@angular/core';
import { Konto, KontoControllerService } from '../../api/konto-service';

@Component({
  selector: 'app-konto-liste',
  templateUrl: './konto-liste.component.html',
  styleUrl: './konto-liste.component.css'
})
export class KontoListeComponent implements OnInit {

  konten: Konto[] = [];
  ladevorgang: boolean = true;
  fehlerMeldung: string = '';

  constructor(private kontoService: KontoControllerService) { }

  ngOnInit(): void {
    this.kontoService.getAllKonten('body', false).subscribe({
      next: (data: Konto[]) => {
        this.konten = Array.isArray(data) ? data : [];
        this.ladevorgang = false;
      },
      error: (error) => {
        this.ladevorgang = false;
        this.fehlerMeldung = 'Konten konnten nicht geladen werden.';
        console.error('Fehler beim Laden der Konten:', error);
      }
    });
  }

}
