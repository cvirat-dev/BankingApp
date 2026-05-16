import { Component, OnInit } from '@angular/core';
import { Konto } from '../../models/konto.model';
import { KontoService } from '../../services/konto.service';

@Component({
  selector: 'app-konto-liste',
  templateUrl: './konto-liste.component.html',
  styleUrl: './konto-liste.component.css'
})
export class KontoListeComponent implements OnInit {

  konten: Konto[] = [];
  ladevorgang: boolean = true;

  constructor(private kontoService: KontoService) { }

  ngOnInit(): void {
    this.kontoService.getAll().subscribe({
      next: (data: Konto[]) => {
        this.konten = data;
        this.ladevorgang = false;
      },
      error: (error) => {
        this.ladevorgang = false;
        console.error('Fehler beim Laden der Konten:', error);
      }
    });
  }

}
