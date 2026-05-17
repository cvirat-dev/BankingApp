import { Injectable } from '@angular/core';
import { Client } from '@stomp/stompjs';
import { BehaviorSubject, Observable } from 'rxjs';
import SockJS from 'sockjs-client';

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {

  private unreadCount$ = new BehaviorSubject<number>(0);
  private client: Client;

  unread$: Observable<number> = this.unreadCount$.asObservable();

  connect(): void {
    this.client = new Client({
      webSocketFactory: () => new SockJS('ws://localhost:8082/ws'),

      onConnect: () => {
        console.log('WebSocket connected');

        this.client.subscribe('/topic/benachrichtigungen', (message) => {
          const benachrichtigung = JSON.parse(message.body);
          console.log('Neue Benachrichtigung:', benachrichtigung);
          this.unreadCount$.next(this.unreadCount$.value + 1);
        });
      }
    });
  }

}
