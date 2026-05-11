import { Component, OnInit } from '@angular/core';
import { ConnectionStatusService } from '../../../services/connection-status.service.service';

@Component({
  selector: 'app-connection-status',
  templateUrl: './connection-status.component.component.html',
  styleUrl: './connection-status.component.component.css'
})
export class ConnectionStatusComponent implements OnInit {

  status$ = this.connectionStatusService.status$;

  constructor(private connectionStatusService: ConnectionStatusService) { }

  ngOnInit(): void {
    this.status$ = this.connectionStatusService.status$;
  }

  onRefresh(): void {
    this.connectionStatusService.checkAll();
  }
}
