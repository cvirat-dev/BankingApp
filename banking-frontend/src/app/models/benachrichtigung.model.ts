export type BenachrichtigungTyp = 'KONTO' | 'BUCHUNG' | 'TRANSAKTION';

export interface Benachrichtigung {
  inhaber: string;
  iban: string;
  nachricht: string;
  timestamp: Date;
  typ: BenachrichtigungTyp;
}
