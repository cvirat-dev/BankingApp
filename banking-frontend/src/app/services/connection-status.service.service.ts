import { Injectable, OnDestroy } from '@angular/core';
import { BehaviorSubject, Subscription, interval } from 'rxjs';
import { ServiceStatus } from '../models/connection-status.model';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class ConnectionStatusService implements OnDestroy {

  private statusSubject = new BehaviorSubject<ServiceStatus[]>([
    { name: 'KontoService', url: 'http://localhost:8081', status: 'CHECKING' },
    { name: 'BenachrichtigungsService', url: 'http://localhost:8082', status: 'CHECKING' }
  ]);

  status$ = this.statusSubject.asObservable();

  private pollSubscription: Subscription;
  private readonly POLL_INTERVAL_MS = 15000; // 15 seconds

  constructor(private http: HttpClient) {
    this.checkAll();
    this.pollSubscription = interval(this.POLL_INTERVAL_MS)
      .subscribe(() => this.checkAll());
  }

  checkAll() {
    const currentStatus = this.statusSubject.getValue();
    currentStatus.forEach((service, index) => this.checkOne(service, index));
  }

  checkOne(service: ServiceStatus, index: number): void {
    this.http
      .get<{ status: string }>(`${service.url}/actuator/health`)
      .subscribe({
        next: (response) => this.updateStatus(index, response.status === 'UP' ? 'UP' : 'DOWN'),
        error: ()         => this.updateStatus(index, 'DOWN'),
      });
  }

  updateStatus(index: number, status: 'UP' | 'DOWN'): void {
    const updated = [...this.statusSubject.getValue()];
    updated[index] = { ...updated[index], status };
    this.statusSubject.next(updated);
  }

  ngOnDestroy(): void {
    if (this.pollSubscription) {
      this.pollSubscription.unsubscribe();
    }
  }
}
