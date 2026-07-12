import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule } from '@angular/common/http/testing';

import { ConnectionStatusService } from './connection-status.service';

describe('ConnectionStatusService', () => {
  let service: ConnectionStatusService;

  beforeEach(() => {
    TestBed.configureTestingModule({
      imports: [HttpClientTestingModule]
    });
    service = TestBed.inject(ConnectionStatusService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
