import { TestBed, fakeAsync, flushMicrotasks, tick } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';
import { Client } from '@stomp/stompjs';

import { WebsocketService } from './websocket.service';
import { environment } from '../../environments/environment';

describe('WebsocketService', () => {
  const TICKET_URL = `${environment.benachrichtigungServiceUrl}/api/ws-ticket`;
  const WS_URL = `${environment.benachrichtigungServiceUrl.replace(/^http/, 'ws')}/ws`;

  let service: WebsocketService;
  let httpMock: HttpTestingController;

  const currentClient = (): Client => (service as any).client;

  beforeEach(() => {
    TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });
    service = TestBed.inject(WebsocketService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    service.disconnect();
    httpMock.verify();
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });

  it('holt vor dem Verbinden ein Ticket und hängt es an die Broker-URL', fakeAsync(() => {
    service.connect();
    const client = currentClient();

    const req = httpMock.expectOne(TICKET_URL);
    expect(req.request.method).toBe('GET');

    // Vor dem Öffnen des Sockets deaktivieren, damit der Test keine echte Verbindung aufbaut.
    service.disconnect();
    req.flush({ ticket: 'abc 123' });
    flushMicrotasks();

    expect(client.brokerURL).toBe(`${WS_URL}?ticket=abc%20123`);
  }));

  it('versucht es nach einem fehlgeschlagenen Ticket-Abruf erneut', fakeAsync(() => {
    service.connect();
    httpMock.expectOne(TICKET_URL).flush('down', { status: 503, statusText: 'Service Unavailable' });
    flushMicrotasks();

    tick(4999);
    httpMock.expectNone(TICKET_URL);

    tick(1);
    const retry = httpMock.expectOne(TICKET_URL);

    service.disconnect();
    retry.flush({ ticket: 't' });
    flushMicrotasks();
  }));

  it('stoppt die Ticket-Versuche nach disconnect', fakeAsync(() => {
    service.connect();
    httpMock.expectOne(TICKET_URL).flush('nope', { status: 401, statusText: 'Unauthorized' });
    flushMicrotasks();

    service.disconnect();
    tick(5000);

    httpMock.expectNone(TICKET_URL);
  }));
});
