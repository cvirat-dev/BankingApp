import { Component, Input } from '@angular/core';
import { Konto } from '../../api/konto-service';

@Component({
  selector: 'app-konto-karte',
  templateUrl: './konto-karte.component.html',
  styleUrl: './konto-karte.component.css'
})
export class KontoKarteComponent {
  @Input() konto!: Konto;
}
