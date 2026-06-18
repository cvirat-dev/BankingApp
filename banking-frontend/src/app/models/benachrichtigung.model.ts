export type BenachrichtigungTyp = 'KONTO' | 'TRANSAKTION';

export interface Benachrichtigung {
  inhaber: string;
  iban: string;
  nachricht: string;
  timestamp: Date;
  typ: BenachrichtigungTyp;
}
