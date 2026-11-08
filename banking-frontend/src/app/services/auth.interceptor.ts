import { HttpErrorResponse, HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';
import { environment } from '../../environments/environment';

const OWN_ORIGINS = [environment.kontoServiceUrl, environment.benachrichtigungServiceUrl]
  .map(url => new URL(url).origin);

function isOwnApiRequest(url: string): boolean {
  try {
    const { origin, pathname } = new URL(url, window.location.origin);
    return OWN_ORIGINS.includes(origin) && pathname.startsWith('/api/');
  } catch {
    return false;
  }
}

export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);

  if (!isOwnApiRequest(req.url)) {
    return next(req);
  }

  const header = auth.authorizationHeader();
  const request = header && !req.headers.has('Authorization')
    ? req.clone({ setHeaders: { Authorization: header } })
    : req;

  return next(request).pipe(
    catchError((error: HttpErrorResponse) => {
      // Nur die aktuelle Session beenden: Login-Versuche und verspätete Antworten
      // tragen einen anderen Header und dürfen sie nicht löschen.
      if (error.status === 401 && request.headers.get('Authorization') === auth.authorizationHeader()) {
        auth.logout();
        router.navigate(['/login']);
      }
      return throwError(() => error);
    })
  );
};
