import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { KontoListeComponent } from './components/konto-liste/konto-liste.component';
import { BuchungFormularComponent } from './components/buchung-formular/buchung-formular.component';
import { TransaktionFormularComponent } from './components/transaktion-formular/transaktion-formular.component';
import { BenachrichtigungsLogComponent } from './components/benachrichtigungs-log/benachrichtigungs-log.component';
import { KontoErstellenComponent } from './components/konto-erstellen/konto-erstellen.component';
import { KontoKarteComponent } from './components/konto-karte/konto-karte.component';
import { ConnectionStatusComponent } from './components/connection-status/connection-status.component';
import { BenachrichtigungsTabsComponent } from './components/benachrichtigungs-tabs/benachrichtigungs-tabs.component';
import { KontoBenachrichtigungenComponent } from './components/konto-benachrichtigungen/konto-benachrichtigungen.component';
import { BuchungBenachrichtigungenComponent } from './components/buchung-benachrichtigungen/buchung-benachrichtigungen.component';
import { TransaktionBenachrichtigungenComponent } from './components/transaktion-benachrichtigungen/transaktion-benachrichtigungen.component';
import { ApiModule as KontoApiModule, Configuration as KontoConfiguration } from './api/konto-service';
import { ApiModule as BenachrichtigungApiModule, Configuration as BenachrichtigungConfiguration } from './api/benachrichtigung-service';

@NgModule({
  declarations: [
    AppComponent,
    KontoListeComponent,
    BuchungFormularComponent,
    BenachrichtigungsLogComponent,
    KontoErstellenComponent,
    KontoKarteComponent,
    ConnectionStatusComponent,
    BenachrichtigungsTabsComponent,
    KontoBenachrichtigungenComponent,
    BuchungBenachrichtigungenComponent,
    TransaktionBenachrichtigungenComponent,
    TransaktionFormularComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule,
    KontoApiModule.forRoot(() => new KontoConfiguration({ basePath: 'http://localhost:8081' })),
    BenachrichtigungApiModule.forRoot(() => new BenachrichtigungConfiguration({ basePath: 'http://localhost:8082' }))
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
