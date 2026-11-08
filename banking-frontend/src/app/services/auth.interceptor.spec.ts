import { TestBed } from '@angular/core/testing';
import { HttpClient, HttpErrorResponse, HttpHeaders, provideHttpClient, withInterceptors } from '@angular/common/http';
import { HttpTestingController, provideHttpClientTesting } from '@angular/common/http/testing';
import { Router } from '@angular/router';

import { authInterceptor } from './auth.interceptor';
import { AuthService } from './auth.service';
import { environment } from '../../environments/environment';

describe('authInterceptor', () => {
  const SESSION = 'Basic c2Vzc2lvbg==';
  const API_URL = `${environment.kontoServiceUrl}/api/konten`;
  const UNAUTHORIZED = { status: 401, statusText: 'Unauthorized' };

  let currentHeader: string | null;
  let logout: jasmine.Spy;
  let router: jasmine.SpyObj<Router>;
  let http: HttpClient;
  let httpMock: HttpTestingController;

  beforeEach(() => {
    currentHeader = SESSION;
    logout = jasmine.createSpy('logout');
    router = jasmine.createSpyObj<Router>('Router', ['navigate']);

    TestBed.configureTestingModule({
      providers: [
        provideHttpClient(withInterceptors([authInterceptor])),
        provideHttpClientTesting(),
        { provide: Router, useValue: router },
        { provide: AuthService, useValue: { logout, authorizationHeader: () => currentHeader } }
      ]
    });

    http = TestBed.inject(HttpClient);
    httpMock = TestBed.inject(HttpTestingController);
  });

  afterEach(() => httpMock.verify());

  function expectNoHeader(url: string): void {
    http.get(url).subscribe();
    const request = httpMock.expectOne(url);
    expect(request.request.headers.has('Authorization')).toBeFalse();
    request.flush({});
  }

  function expectNoLogout(): void {
    expect(logout).not.toHaveBeenCalled();
    expect(router.navigate).not.toHaveBeenCalled();
  }

  describe('Authorization header', () => {
    it('is added to requests for own API urls', () => {
      http.get(API_URL).subscribe();
      const request = httpMock.expectOne(API_URL);
      expect(request.request.headers.get('Authorization')).toBe(SESSION);
      request.flush({});
    });

    it('is added for every own service', () => {
      const url = `${environment.benachrichtigungServiceUrl}/api/benachrichtigungen`;
      http.get(url).subscribe();
      const request = httpMock.expectOne(url);
      expect(request.request.headers.get('Authorization')).toBe(SESSION);
      request.flush({});
    });

    it('does not replace an explicit header', () => {
      const headers = new HttpHeaders({ Authorization: 'Basic explicit' });
      http.get(API_URL, { headers }).subscribe();
      const request = httpMock.expectOne(API_URL);
      expect(request.request.headers.get('Authorization')).toBe('Basic explicit');
      request.flush({});
    });

    it('is not added without a session', () => {
      currentHeader = null;
      expectNoHeader(API_URL);
    });

    it('is not added to public health checks', () => {
      expectNoHeader(`${environment.kontoServiceUrl}/actuator/health`);
    });

    it('is not added to look-alike origins', () => {
      expectNoHeader(`${environment.kontoServiceUrl}@evil.example/api/konten`);
    });

    it('is not added to malformed urls', () => {
      expectNoHeader('http://localhost:99999/api/konten');
    });

    it('is not added to foreign origins', () => {
      expectNoHeader('https://example.com/api/konten');
    });
  });

  describe('401 handling', () => {
    function failWith401(url = API_URL, headers?: HttpHeaders): HttpErrorResponse | undefined {
      let received: HttpErrorResponse | undefined;
      http.get(url, { headers }).subscribe({ error: error => received = error });
      httpMock.expectOne(url).flush(null, UNAUTHORIZED);
      return received;
    }

    it('logs out and redirects when the current session is rejected', () => {
      const error = failWith401();

      expect(logout).toHaveBeenCalledTimes(1);
      expect(router.navigate).toHaveBeenCalledOnceWith(['/login']);
      expect(error?.status).toBe(401);
    });

    it('redirects to login when no session exists', () => {
      currentHeader = null;

      failWith401();

      expect(router.navigate).toHaveBeenCalledOnceWith(['/login']);
    });

    it('keeps the session when a login attempt with other credentials fails', () => {
      const error = failWith401(API_URL, new HttpHeaders({ Authorization: 'Basic wrong' }));

      expectNoLogout();
      expect(error?.status).toBe(401);
    });

    it('keeps a session started while an unauthenticated request was in flight', () => {
      currentHeader = null;
      let received: HttpErrorResponse | undefined;
      http.get(API_URL).subscribe({ error: error => received = error });
      const request = httpMock.expectOne(API_URL);

      currentHeader = SESSION;
      request.flush(null, UNAUTHORIZED);

      expectNoLogout();
      expect(received?.status).toBe(401);
    });

    it('ignores a 401 from public endpoints', () => {
      failWith401(`${environment.kontoServiceUrl}/actuator/health`);

      expectNoLogout();
    });

    it('does not log out on other errors', () => {
      let received: HttpErrorResponse | undefined;
      http.get(API_URL).subscribe({ error: error => received = error });
      httpMock.expectOne(API_URL).flush(null, { status: 500, statusText: 'Server Error' });

      expectNoLogout();
      expect(received?.status).toBe(500);
    });
  });
});
