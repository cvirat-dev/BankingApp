import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { FormsModule } from '@angular/forms';
import { HttpClientModule } from '@angular/common/http';
import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { KontoListeComponent } from './components/konto-liste/konto-liste.component';
import { BuchungFormularComponent } from './components/buchung-formular/buchung-formular.component';
import { BenachrichtigungsLogComponent } from './components/benachrichtigungs-log/benachrichtigungs-log.component';
import { KontoErstellenComponent } from './components/konto-erstellen/konto-erstellen.component';
import { KontoKarteComponent } from './components/konto-karte/konto-karte.component';
import { ConnectionStatusComponent } from './components/connection-status/connection-status.component';
import { BenachrichtigungItemComponent } from './components/benachrichtigung-item/benachrichtigung-item.component';

@NgModule({
  declarations: [
    AppComponent,
    KontoListeComponent,
    BuchungFormularComponent,
    BenachrichtigungsLogComponent,
    KontoErstellenComponent,
    KontoKarteComponent,
    ConnectionStatusComponent,
    BenachrichtigungItemComponent
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
