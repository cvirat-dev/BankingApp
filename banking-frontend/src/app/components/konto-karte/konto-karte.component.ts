import { Component, Input } from '@angular/core';
import { Konto } from '../../models/konto.model';

@Component({
  selector: 'app-konto-karte',
  templateUrl: './konto-karte.component.html',
  styleUrl: './konto-karte.component.css'
})
export class KontoKarteComponent {
  @Input() konto!: Konto;
}
