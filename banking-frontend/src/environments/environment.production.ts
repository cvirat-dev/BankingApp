// Standard-Produktions-Build (ng build / Dockerfile ohne BUILD_CONFIG).
// Enthält bewusst keine eigenen URLs, sondern verweist auf das aktuelle Produktionsziel.
// Zum Wechseln des Ziels nur den Import ändern (z.B. './environment.openshift').
export { environment } from './environment.azure';
