import { TestBed } from '@angular/core/testing';

import { BenachrichtigungService } from './benachrichtigung.service';

describe('BenachrichtigungService', () => {
  let service: BenachrichtigungService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(BenachrichtigungService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
