import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { KontoListeComponent } from './app/components/konto/konto-liste/konto-liste.component';
import { KontoErstellenComponent } from './app/components/konto/konto-erstellen/konto-erstellen.component';
import { KontoEditComponent } from './app/components/konto/konto-edit/konto-edit.component';
import { BuchungFormularComponent } from './app/components/buchung/buchung-formular/buchung-formular.component';
import { BenachrichtigungsLogComponent } from './app/components/benachrichtigungen/log/benachrichtigungs-log.component';
import { TransaktionFormularComponent } from './app/components/transaktion/transaktion-formular/transaktion-formular.component';

const KONTO_SIDE_NAV = [
  { label: 'Kontoübersicht', link: '/konten' },
  { label: 'Konto Erstellen', link: '/konto-erstellen' },
  { label: 'Konto Bearbeiten', link: '/konto-bearbeiten' }
];

const BUCHUNG_SIDE_NAV = [
  { label: 'Buchung Formular', link: '/buchung' }
];

const TRANSKATION_SIDE_NAV = [
  { label: 'Transaktion Formular', link: '/transaktion' }
];

const routes: Routes = [
  { path: '', redirectTo: 'konten', pathMatch: 'full' },
  {
    path: 'konten', component: KontoListeComponent,
    data: { sideNav: KONTO_SIDE_NAV, section: 'konten' }
  },
  {
    path: 'konto-erstellen', component: KontoErstellenComponent,
    data: { sideNav: KONTO_SIDE_NAV, section: 'konten' }
  },
  {
    path: 'konto-bearbeiten',
    component: KontoEditComponent,
    data: { sideNav: KONTO_SIDE_NAV, section: 'konten' }
  },
  {
    path: 'konto-bearbeiten/:id', component: KontoEditComponent,
    data: { sideNav: KONTO_SIDE_NAV, section: 'konten' }
  },
  { path: 'buchung', component: BuchungFormularComponent, data: { sideNav: BUCHUNG_SIDE_NAV, section: 'buchung' } },
  { path: 'transaktion', component: TransaktionFormularComponent, data: { sideNav: TRANSKATION_SIDE_NAV, section: 'transaktion' } },
  { path: 'benachrichtigungen', component: BenachrichtigungsLogComponent, data: { section: 'benachrichtigungen' } },
  { path: '**', redirectTo: 'konten' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
