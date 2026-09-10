// Persistiert Filterwerte pro Tab in sessionStorage, damit sie Tabwechsel (Komponente wird neu erstellt) und Seiten-Reloads innerhalb der Browser-Session überstehen.

export function filterSpeichern(key: string, state: object): void {
  try {
    sessionStorage.setItem(key, JSON.stringify(state));
  } catch {
    // sessionStorage evtl. nicht verfügbar (z.B. privater Modus)
  }
}

export function getStoredFilter<T extends object>(key: string): Partial<T> | null {
  try {
    const gespeichert = sessionStorage.getItem(key);
    return gespeichert ? (JSON.parse(gespeichert) as Partial<T>) : null;
  } catch {
    return null;
  }
}

export function saveFilterVisibility(key: string, isVisible: boolean): void {
  try {
    sessionStorage.setItem(key, JSON.stringify(isVisible));
  } catch {
    // sessionStorage evtl. nicht verfügbar (z.B. privater Modus)
  }
}

export function getFilterVisibility(key: string): boolean | null {
  try {
    const gespeichert = sessionStorage.getItem(key);
    return gespeichert ? JSON.parse(gespeichert) as boolean : null;
  } catch {
    return null;
  }
}
