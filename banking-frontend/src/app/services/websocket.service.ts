import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Client } from '@stomp/stompjs';
import { BehaviorSubject, Observable, firstValueFrom } from 'rxjs';
import { LoggerService } from './logger.service';
import { Benachrichtigung } from '../models/benachrichtigung.model';
import { environment } from '../../environments/environment';

const RECONNECT_DELAY_MS = 5000;

@Injectable({
  providedIn: 'root'
})
export class WebsocketService {

  private readonly ticketUrl = `${environment.benachrichtigungServiceUrl}/api/ws-ticket`;
  private readonly wsUrl = `${environment.benachrichtigungServiceUrl.replace(/^http/, 'ws')}/ws`;

  private unreadCount$ = new BehaviorSubject<number>(0);
  private client: Client | null = null;

  unread$: Observable<number> = this.unreadCount$.asObservable();

  constructor(private http: HttpClient, private logger: LoggerService) {}

  connect(): void {
    if (this.client?.active) {
      return;
    }

    const client: Client = new Client({
      // Der Handshake verlangt ein Einmal-Ticket (?ticket=…), das per Basic-Auth
      // geholt wird. beforeConnect läuft auch vor jedem Reconnect, so dass jeder
      // Verbindungsversuch ein frisches Ticket bekommt.
      beforeConnect: (stompClient: Client) => this.prepareTicket(stompClient),

      onConnect: () => {
        this.logger.log('WebSocket connected');

        client.subscribe('/topic/benachrichtigungen', (message) => {
          const event: Benachrichtigung = JSON.parse(message.body);
          this.logger.log('Neue Benachrichtigung:', event);
          this.unreadCount$.next(this.unreadCount$.value + 1);
        });
      },

      reconnectDelay: RECONNECT_DELAY_MS
    });

    this.client = client;
    client.activate();
  }

  resetUnread(): void {
    this.unreadCount$.next(0);
  }

  disconnect(): void {
    this.client?.deactivate();
    this.client = null;
  }

  // Wirft nie: Ein Fehler in beforeConnect würde stompjs ohne Reconnect stehen lassen.
  // Daher selbst erneut versuchen, bis ein Ticket da ist oder der Client deaktiviert wurde
  // (z. B. Logout nach 401 durch den authInterceptor).
  private async prepareTicket(client: Client): Promise<void> {
    while (client.active) {
      try {
        const { ticket } = await firstValueFrom(this.http.get<{ ticket: string }>(this.ticketUrl));
        client.brokerURL = `${this.wsUrl}?ticket=${encodeURIComponent(ticket)}`;
        return;
      } catch (error) {
        this.logger.warn('WebSocket-Ticket konnte nicht geholt werden, neuer Versuch folgt', error);
        await new Promise(resolve => setTimeout(resolve, RECONNECT_DELAY_MS));
      }
    }
  }
}
