import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { KontoListeComponent } from './components/konto-liste/konto-liste.component';
import { KontoKarteComponent } from './components/konto-karte/konto-karte.component';
import { KontoErstellenComponent } from './components/konto-erstellen/konto-erstellen.component';
import { BuchungFormularComponent } from './components/buchung-formular/buchung-formular.component';
import { BenachrichtigungsLogComponent } from './components/benachrichtigungs-log/benachrichtigungs-log.component';
import { TransaktionFormularComponent } from './components/transaktion-formular/transaktion-formular.component';

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
