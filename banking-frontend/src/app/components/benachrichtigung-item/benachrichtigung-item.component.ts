import { Component, Input } from '@angular/core';
import { Benachrichtigung } from '../../models/benachrichtigung.model';

@Component({
  selector: 'app-benachrichtigung-item',
  templateUrl: './benachrichtigung-item.component.html',
  styleUrl: './benachrichtigung-item.component.css'
})
export class BenachrichtigungItemComponent {
  @Input() benachrichtigung!: Benachrichtigung;
}
