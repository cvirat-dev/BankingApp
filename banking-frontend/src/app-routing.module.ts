import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { KontoListeComponent } from './app/components/konto-liste/konto-liste.component';
import { KontoKarteComponent } from './app/components/konto-karte/konto-karte.component';
import { KontoErstellenComponent } from './app/components/konto-erstellen/konto-erstellen.component';
import { BuchungFormularComponent } from './app/components/buchung-formular/buchung-formular.component';
import { BenachrichtigungsLogComponent } from './app/components/benachrichtigungen/log/benachrichtigungs-log.component';
import { TransaktionFormularComponent } from './app/components/transaktion-formular/transaktion-formular.component';

const routes: Routes = [
  { path: '', redirectTo: 'konten', pathMatch: 'full' },
  { path: 'konten', component: KontoListeComponent },
  { path: 'konten/:id', component: KontoKarteComponent },
  { path: 'konto-erstellen', component: KontoErstellenComponent },
  { path: 'buchung', component: BuchungFormularComponent },
  { path: 'transaktion', component: TransaktionFormularComponent },
  { path: 'benachrichtigungen', component: BenachrichtigungsLogComponent },
  { path: '**', redirectTo: 'konten' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
