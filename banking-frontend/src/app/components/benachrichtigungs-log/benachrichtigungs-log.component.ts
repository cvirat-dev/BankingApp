import { Component, EventEmitter, Output } from '@angular/core';
import { BenachrichtigungTyp } from '../../models/benachrichtigung.model';
import { BenachrichtigungsTab } from '../benachrichtigungs-tabs/benachrichtigungs-tabs.component';

@Component({
  selector: 'app-benachrichtigungs-log',
  templateUrl: './benachrichtigungs-log.component.html',
  styleUrl: './benachrichtigungs-log.component.css'
})
export class BenachrichtigungsLogComponent {

  aktuellerTyp: BenachrichtigungTyp = 'KONTO';

  get tabs(): BenachrichtigungsTab[] {
    return [
      { typ: 'KONTO', label: 'Konto' },
      { typ: 'TRANSAKTION', label: 'Transaktion' }
    ];
  }

  get benachrichtigungTyp(): BenachrichtigungTyp {
    return this.aktuellerTyp;
  }

  get aktuellerTypLabel(): string {
    return this.aktuellerTyp === 'KONTO' ? 'Konto' : 'Transaktion';
  }

  onBenachrichtigungTypChanged(typ: BenachrichtigungTyp): void {
    this.aktuellerTyp = typ;
  }

}
