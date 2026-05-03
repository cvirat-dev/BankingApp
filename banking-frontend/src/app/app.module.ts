import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { HttpClientModule } from '@angular/common/http'; 
import { FormsModule } from '@angular/forms';

import { AppRoutingModule } from './app-routing.module';
import { AppComponent } from './app.component';
import { KontoListeComponent } from './components/konto-liste/konto-liste.component';
import { BuchungFormularComponent } from './components/buchung-formular/buchung-formular.component';
import { BenachrichtigungsLogComponent } from './components/benachrichtigungs-log/benachrichtigungs-log.component';
import { ConnectionStatusComponent } from './components/connection-status/connection-status.component/connection-status.component.component';

@NgModule({
  declarations: [
    AppComponent,
    KontoListeComponent,
    BuchungFormularComponent,
    BenachrichtigungsLogComponent,
    ConnectionStatusComponent
  ],
  imports: [
    BrowserModule,
    AppRoutingModule,
    HttpClientModule,
    FormsModule
  ],
  providers: [],
  bootstrap: [AppComponent]
})
export class AppModule { }
