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
import { BenachrichtigungItemComponent } from './components/benachrichtigung-item/benachrichtigung-item.component';
import { BenachrichtigungsTabsComponent } from './components/benachrichtigungs-tabs/benachrichtigungs-tabs.component';
import { BenachrichtigungsListeComponent } from './components/benachrichtigungs-liste/benachrichtigungs-liste.component';
import { FilterLeisteComponent } from './components/filter-leiste/filter-leiste.component';

@NgModule({
  declarations: [
    AppComponent,
    KontoListeComponent,
    BuchungFormularComponent,
    BenachrichtigungsLogComponent,
    KontoErstellenComponent,
    KontoKarteComponent,
    ConnectionStatusComponent,
    BenachrichtigungItemComponent,
    BenachrichtigungsTabsComponent,
    BenachrichtigungsListeComponent,
    FilterLeisteComponent,
    TransaktionFormularComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    FormsModule,
    HttpClientModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
