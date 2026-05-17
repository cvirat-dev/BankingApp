import { Injectable, OnDestroy } from '@angular/core';
import { BehaviorSubject, interval, Observable, Subscription } from 'rxjs';
import { startWith, switchMap } from 'rxjs/operators';
import { Benachrichtigung } from '../models/benachrichtigung.model';
import { HttpClient } from '@angular/common/http';

@Injectable({
  providedIn: 'root'
})
export class BenachrichtigungService implements OnDestroy {
  private apiUrl: string = 'http://localhost:8082/api/benachrichtigungen';
  private readonly STORAGE_KEY = 'lastSeenBenachrichtigung';

  private _unreadCount$ = new BehaviorSubject<number>(0);
  unreadCount$: Observable<number> = this._unreadCount$.asObservable();

  private pollSubscription?: Subscription;

  constructor(private http: HttpClient) {
    this.startPolling();
  }

  getAll(): Observable<Benachrichtigung[]> {
    return this.http.get<Benachrichtigung[]>(this.apiUrl);
  }

  markAllRead(): void {
    localStorage.setItem(this.STORAGE_KEY, new Date().toISOString());
    this._unreadCount$.next(0);
  }

  private startPolling(): void {
    this.pollSubscription = interval(30000).pipe(
      startWith(0),
      switchMap(() => this.http.get<Benachrichtigung[]>(this.apiUrl))
    ).subscribe({
      next: (items) => this.updateUnreadCount(items),
      error: () => {}
    });
  }

  private updateUnreadCount(items: Benachrichtigung[]): void {
    const lastSeen = localStorage.getItem(this.STORAGE_KEY);
    if (!lastSeen) {
      this._unreadCount$.next(items.length);
      return;
    }
    const lastSeenDate = new Date(lastSeen);
    const unread = items.filter(item => new Date(item.timestamp) > lastSeenDate).length;
    this._unreadCount$.next(unread);
  }

  ngOnDestroy(): void {
    this.pollSubscription?.unsubscribe();
  }
}
