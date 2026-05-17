import { Component, OnDestroy, OnInit } from '@angular/core';
import { Subscription } from 'rxjs';
import { BenachrichtigungService } from './services/benachrichtigung.service';

@Component({
  selector: 'app-root',
  templateUrl: './app.component.html',
  styleUrl: './app.component.css'
})
export class AppComponent implements OnInit, OnDestroy {
  title = 'banking-frontend';
  unreadCount = 0;
  private unreadSub?: Subscription;

  constructor(private benachrichtigungService: BenachrichtigungService) {}

  ngOnInit(): void {
    this.unreadSub = this.benachrichtigungService.unreadCount$.subscribe(count => {
      this.unreadCount = count;
    });
  }

  ngOnDestroy(): void {
    this.unreadSub?.unsubscribe();
  }
}
