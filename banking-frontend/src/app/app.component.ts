import { Component, OnDestroy, OnInit } from '@angular/core';
import { WebsocketService } from './services/websocket.service';
import { Subscription } from 'rxjs/internal/Subscription';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'banking-frontend';
  unreadCount = 0;

  private unreadSubscription: Subscription = new Subscription();

  constructor(
    public websocketService: WebsocketService
  ) {}

  ngOnInit(): void {
    this.websocketService.connect();

    this.unreadSubscription = this.websocketService.unread$.subscribe(count => {
      this.unreadCount = count;
    });
  }

  ngOnDestroy(): void {
    this.websocketService.disconnect();
    this.unreadSubscription.unsubscribe();
  }

  onBenachrichtigungenClick(): void {
    this.websocketService.resetUnread();
  }
}
