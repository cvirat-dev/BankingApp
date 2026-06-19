export interface Buchung {
  id?: number;
  kontoId?: number;
  betrag: number;
  beschreibung?: string;
  datum?: string;
}
