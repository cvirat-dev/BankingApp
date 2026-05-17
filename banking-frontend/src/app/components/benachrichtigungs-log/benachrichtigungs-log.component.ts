import { Component, OnInit } from '@angular/core';
import { Benachrichtigung } from '../../models/benachrichtigung.model';
import { BenachrichtigungService } from '../../services/benachrichtigung.service';

@Component({
  selector: 'app-benachrichtigungs-log',
  templateUrl: './benachrichtigungs-log.component.html',
  styleUrl: './benachrichtigungs-log.component.css'
})
export class BenachrichtigungsLogComponent implements OnInit {
  
  benachrichtigungen: Benachrichtigung[] = [];
  ladevorgang: boolean = true;

  constructor(private benachrichtigungsService: BenachrichtigungService) { }

  ngOnInit(): void {
    this.benachrichtigungsService.getAll().subscribe({
      next: (data: Benachrichtigung[]) => {
        this.benachrichtigungen = data;
        this.ladevorgang = false;
        this.benachrichtigungsService.markAllRead();
      },
      error: (error) => {
        this.ladevorgang = false;
        console.error('Fehler beim Laden der Benachrichtigungen:', error);
      }
    });
  }

}
