import { Component, EventEmitter, Input, Output } from '@angular/core';
import { BenachrichtigungTyp } from '../../models/benachrichtigung.model';

export interface BenachrichtigungsTab {
  typ: BenachrichtigungTyp;
  label: string;
  anzahl: number;
}

@Component({
  selector: 'app-benachrichtigungs-tabs',
  templateUrl: './benachrichtigungs-tabs.component.html',
  styleUrl: './benachrichtigungs-tabs.component.css'
})
export class BenachrichtigungsTabsComponent {
  @Input() tabs: BenachrichtigungsTab[] = [];
  @Input() aktiv!: BenachrichtigungTyp;
  @Output() typGewaehlt = new EventEmitter<BenachrichtigungTyp>();

  select(typ: BenachrichtigungTyp): void {
    this.typGewaehlt.emit(typ);
  }
}
