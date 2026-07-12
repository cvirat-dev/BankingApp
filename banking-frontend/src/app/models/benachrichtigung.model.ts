import type { Benachrichtigung as BenachrichtigungBase } from '../api/benachrichtigung-service/model/benachrichtigung';
import type { BuchungBenachrichtigung } from '../api/benachrichtigung-service/model/buchungBenachrichtigung';
import type { KontoBenachrichtigung } from '../api/benachrichtigung-service/model/kontoBenachrichtigung';
import type { TransaktionBenachrichtigung } from '../api/benachrichtigung-service/model/transaktionBenachrichtigung';

export type Benachrichtigung = 
| KontoBenachrichtigung 
| BuchungBenachrichtigung 
| TransaktionBenachrichtigung;

export type { KontoBenachrichtigung, BuchungBenachrichtigung, TransaktionBenachrichtigung };
export type BenachrichtigungTyp = BenachrichtigungBase.TypEnum;
