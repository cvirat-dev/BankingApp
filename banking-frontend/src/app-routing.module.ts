import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { KontoListeComponent } from './app/components/konto/konto-liste/konto-liste.component';
import { KontoErstellenComponent } from './app/components/konto/konto-erstellen/konto-erstellen.component';
import { KontoEditComponent } from './app/components/konto/konto-edit/konto-edit.component';
import { BuchungFormularComponent } from './app/components/buchung/buchung-formular/buchung-formular.component';
import { BenachrichtigungsLogComponent } from './app/components/benachrichtigungen/log/benachrichtigungs-log.component';
import { TransaktionFormularComponent } from './app/components/transaktion/transaktion-formular/transaktion-formular.component';
import { AuthLoginComponent } from './app/components/auth-login/auth-login.component';
import { authGuard } from './app/services/auth.guard';

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
  { path: 'login', component: AuthLoginComponent },
  {
    path: 'konten', component: KontoListeComponent,
    canActivate: [authGuard],
    data: { sideNav: KONTO_SIDE_NAV, section: 'konten' }
  },
  {
    path: 'konto-erstellen', component: KontoErstellenComponent,
    canActivate: [authGuard],
    data: { sideNav: KONTO_SIDE_NAV, section: 'konten' }
  },
  {
    path: 'konto-bearbeiten',
    component: KontoEditComponent,
    canActivate: [authGuard],
    data: { sideNav: KONTO_SIDE_NAV, section: 'konten' }
  },
  {
    path: 'konto-bearbeiten/:id', component: KontoEditComponent,
    canActivate: [authGuard],
    data: { sideNav: KONTO_SIDE_NAV, section: 'konten' }
  },
  { path: 'buchung', component: BuchungFormularComponent, canActivate: [authGuard], data: { sideNav: BUCHUNG_SIDE_NAV, section: 'buchung' } },
  { path: 'transaktion', component: TransaktionFormularComponent, canActivate: [authGuard], data: { sideNav: TRANSKATION_SIDE_NAV, section: 'transaktion' } },
  { path: 'benachrichtigungen', component: BenachrichtigungsLogComponent, canActivate: [authGuard], data: { section: 'benachrichtigungen' } },
  { path: '**', redirectTo: 'konten' }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }
