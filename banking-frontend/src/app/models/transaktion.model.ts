export interface Transaktion {
  id?: number;
  kontoId?: number;
  betrag: number;
  beschreibung?: string;
  datum?: string;
}
