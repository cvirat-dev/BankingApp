import { Component, Input, OnInit } from '@angular/core';
import { Benachrichtigung, BenachrichtigungTyp } from '../../models/benachrichtigung.model';
import { BenachrichtigungService } from '../../services/benachrichtigung.service';

@Component({
  selector: 'app-benachrichtigungs-liste',
  templateUrl: './benachrichtigungs-liste.component.html',
  styleUrl: './benachrichtigungs-liste.component.css'
})
export class BenachrichtigungsListeComponent implements OnInit {

  private _typFilter: BenachrichtigungTyp = 'KONTO';

  @Input()
  set typFilter(value: BenachrichtigungTyp) {
    const nextTyp = value ?? 'KONTO';
    const hasChanged = this._typFilter !== nextTyp;
    this._typFilter = nextTyp;

    if (hasChanged) {
      this.laden();
    }
  }

  get typFilter(): BenachrichtigungTyp {
    return this._typFilter;
  }

  @Input() ibanFilter: string = '';
  @Input() vonFilter: string = '';
  @Input() bisFilter: string = '';

  benachrichtigungen: Benachrichtigung[] = [];

  constructor(private benachrichtigungsService: BenachrichtigungService) {}

  ngOnInit() {
    this.laden();
  }

  laden() {

    this.benachrichtigungsService.getAll({
      typ: this.typFilter,
      iban: this.ibanFilter,
      von: this.vonFilter,
      bis: this.bisFilter
    }).subscribe(benachrichtigungen => {
      this.benachrichtigungen = benachrichtigungen;
    });
  }

  onFilterParamsChanged(params: { iban: string; von: string; bis: string }) {
    this.ibanFilter = params.iban;
    this.vonFilter = params.von;
    this.bisFilter = params.bis;
    this.laden();
  }

}
