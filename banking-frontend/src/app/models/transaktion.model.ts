export interface Transaktion {
  id?: number;
  quelleKontoId?: number;
  zielKontoId?: number;
  betrag: number;
  beschreibung: string;
  datum?: string;
}
