import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { KontoService } from './konto.service';
import { provideApi as provideKontoApi } from '../api/konto-service/provide-api';

describe('KontoService', () => {
  let service: KontoService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule],
      providers: [provideKontoApi('http://localhost:8081')]
    });
    service = TestBed.inject(KontoService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
