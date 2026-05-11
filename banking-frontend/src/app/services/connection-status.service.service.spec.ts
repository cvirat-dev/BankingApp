import { TestBed } from '@angular/core/testing';

import { ConnectionStatusServiceService } from './connection-status.service.service';

describe('ConnectionStatusServiceService', () => {
  let service: ConnectionStatusServiceService;

  beforeEach(() => {
    TestBed.configureTestingModule({});
    service = TestBed.inject(ConnectionStatusServiceService);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
  });
});
