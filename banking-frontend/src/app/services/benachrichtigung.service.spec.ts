import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { BenachrichtigungService } from './benachrichtigung.service';
import { provideApi as provideBenachrichtigungApi } from '../api/benachrichtigung-service/provide-api';

describe('BenachrichtigungService', () => {
  let service: BenachrichtigungService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [provideBenachrichtigungApi('http://localhost:8082')]
    });
    service = TestBed.inject(BenachrichtigungService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
