import { Component, Input } from '@angular/core';
import { Benachrichtigung } from '../../models/benachrichtigung.model';

@Component({
  selector: 'app-benachrichtigungs-liste',
  templateUrl: './benachrichtigungs-liste.component.html',
  styleUrl: './benachrichtigungs-liste.component.css'
})
export class BenachrichtigungsListeComponent {
  @Input() benachrichtigungen: Benachrichtigung[] = [];
  @Input() typLabel: string = '';
}
