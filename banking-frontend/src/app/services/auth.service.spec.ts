import { TestBed } from '@angular/core/testing';
import { HttpClientTestingModule, HttpTestingController } from '@angular/common/http/testing';

import { AuthService } from './auth.service';
import { environment } from '../../environments/environment';

describe('AuthService', () => {
  const STORAGE_KEY = 'banking-auth';
  const ME_URL = `${environment.kontoServiceUrl}/api/me`;

  let service: AuthService;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    sessionStorage.removeItem(STORAGE_KEY);
    TestBed.configureTestingModule({ imports: [HttpClientTestingModule] });
    service = TestBed.inject(AuthService);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => {
    httpMock.verify();
    sessionStorage.removeItem(STORAGE_KEY);
  });

  it('should be created', () => {
    expect(service).toBeTruthy();
    expect(service.isLoggedIn()).toBeFalse();
  });

  it('stores the session after a successful login', () => {
    service.login('user', 'secret').subscribe();

    const request = httpMock.expectOne(ME_URL);
    expect(request.request.headers.get('Authorization')).toBe(`Basic ${btoa('user:secret')}`);
    request.flush({});

    expect(service.isLoggedIn()).toBeTrue();
    expect(service.authorizationHeader()).toBe(`Basic ${btoa('user:secret')}`);
    expect(sessionStorage.getItem(STORAGE_KEY)).toBe(btoa('user:secret'));
  });

  it('encodes non-Latin-1 credentials as UTF-8', () => {
    service.login('user', 'Mü€').subscribe();

    const request = httpMock.expectOne(ME_URL);
    // UTF-8-Bytes von "user:Mü€", Base64-kodiert
    expect(request.request.headers.get('Authorization')).toBe('Basic dXNlcjpNw7zigqw=');
    request.flush({});
  });

  it('does not store a session after a failed login', () => {
    let failed = false;
    service.login('user', 'wrong').subscribe({ error: () => failed = true });

    httpMock.expectOne(ME_URL).flush(null, { status: 401, statusText: 'Unauthorized' });

    expect(failed).toBeTrue();
    expect(service.isLoggedIn()).toBeFalse();
    expect(sessionStorage.getItem(STORAGE_KEY)).toBeNull();
  });

  it('clears the session on logout', () => {
    service.login('user', 'secret').subscribe();
    httpMock.expectOne(ME_URL).flush({});

    service.logout();

    expect(service.isLoggedIn()).toBeFalse();
    expect(service.authorizationHeader()).toBeNull();
    expect(sessionStorage.getItem(STORAGE_KEY)).toBeNull();
  });
});
