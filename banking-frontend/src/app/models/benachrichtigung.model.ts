export type BenachrichtigungTyp = 'KONTO' | 'TRANSAKTION';

export interface Benachrichtigung {
  kontoId: number;
  inhaber: string;
  nachricht: string;
  timestamp: Date;
  typ: BenachrichtigungTyp;
}
