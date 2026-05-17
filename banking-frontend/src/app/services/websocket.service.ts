import { Injectable } from '@angular/core';
import { Client } from '@stomp/stompjs';
import { BehaviorSubject, Observable } from 'rxjs';
import { LoggerService } from './logger.service';

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {

  private unreadCount$ = new BehaviorSubject<number>(0);
  private client!: Client;

  unread$: Observable<number> = this.unreadCount$.asObservable();

  constructor(private logger: LoggerService) {}

  connect(): void {
    this.client = new Client({
      brokerURL: 'ws://localhost:8082/ws',

      onConnect: () => {
        this.logger.log('WebSocket connected');

        this.client.subscribe('/topic/benachrichtigungen', (message) => {
          const benachrichtigung = JSON.parse(message.body);
          this.logger.log('Neue Benachrichtigung:', benachrichtigung);
          this.unreadCount$.next(this.unreadCount$.value + 1);
        });
      },

      reconnectDelay: 5000
    });

    this.client.activate();
  }

  resetUnread(): void {
    this.unreadCount$.next(0);
  }

  disconnect(): void {
    this.client?.deactivate();
  }
}
