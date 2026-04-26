import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { KontoListeComponent } from './components/konto-liste/konto-liste.component';
import { BuchungFormularComponent } from './components/buchung-formular/buchung-formular.component';
import { BenachrichtigungsLogComponent } from './components/benachrichtigungs-log/benachrichtigungs-log.component';

@NgModule({
  declarations: [
    AppComponent,
    KontoListeComponent,
    BuchungFormularComponent,
    BenachrichtigungsLogComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
