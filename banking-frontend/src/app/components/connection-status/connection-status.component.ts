import { Component, OnInit } from '@angular/core';
import { ConnectionStatusService } from '../../services/connection-status.service';

@Component({
  selector: 'app-connection-status',
  templateUrl: './connection-status.component.html',
  styleUrl: './connection-status.component.css'
})
export class ConnectionStatusComponent implements OnInit {

  status$ = this.connectionStatusService.status$;

  constructor(private connectionStatusService: ConnectionStatusService) { }

  ngOnInit(): void {
  }

  onRefresh(): void {
    this.connectionStatusService.checkAll();
  }
}
