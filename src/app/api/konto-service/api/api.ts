export * from './buchungController.service';
import { BuchungControllerService } from './buchungController.service';
export * from './kontoController.service';
import { KontoControllerService } from './kontoController.service';
export * from './transaktionController.service';
import { TransaktionControllerService } from './transaktionController.service';
export const APIS = [BuchungControllerService, KontoControllerService, TransaktionControllerService];
