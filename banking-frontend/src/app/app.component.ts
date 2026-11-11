import { Component, OnDestroy, OnInit, effect } from '@angular/core';
import { WebsocketService } from './services/websocket.service';
import { AuthService } from './services/auth.service';
import { Subscription } from 'rxjs/internal/Subscription';
import { Router, NavigationEnd, ActivatedRoute } from '@angular/router';
import { filter, map } from 'rxjs/operators';

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
    public websocketService: WebsocketService,
    public auth: AuthService,
    private router: Router,
    private route: ActivatedRoute
  ) {
    // WebSocket nur für eingeloggte Nutzer. Reagiert auch auf den Logout
    // durch den authInterceptor (401), nicht nur auf den Abmelden-Button.
    effect(() => {
      if (this.auth.isLoggedIn()) {
        this.websocketService.connect();
      } else {
        this.websocketService.disconnect();
        this.websocketService.resetUnread();
      }
    });
  }

  ngOnInit(): void {
    this.unreadSubscription = this.websocketService.unread$.subscribe(count => {
      this.unreadCount = count;
    });

    this.router.events.pipe(
      filter(e => e instanceof NavigationEnd),
      map(() => {
        let r = this.route.firstChild;
        while (r?.firstChild) r = r.firstChild;
        return r?.snapshot.data ?? {};
      })
    ).subscribe(data => {
      this.sideNavItems = data['sideNav'] ?? [];
      this.currentSection = data['section'] ?? null;
    });
  }

  ngOnDestroy(): void {
    this.websocketService.disconnect();
    this.unreadSubscription.unsubscribe();
  }

  onBenachrichtigungenClick(): void {
    this.websocketService.resetUnread();
  }

  logout(): void {
    this.auth.logout();
    this.router.navigate(['/login']);
  }

  sideNavItems: { label: string; link: string }[] = [];
  currentSection: string | null = null;
}
