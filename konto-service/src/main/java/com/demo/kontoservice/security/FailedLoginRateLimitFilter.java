package com.demo.kontoservice.security;

import java.io.IOException;
import java.time.Duration;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

/**
 * Begrenzt fehlgeschlagene Basic-Auth-Logins pro Client-IP.
 * Nach {@code maxFailures} Fehlversuchen innerhalb von {@code window}
 * wird der Client bis zum Ende des Zeitfensters mit HTTP 429 abgewiesen.
 */
public class FailedLoginRateLimitFilter extends OncePerRequestFilter {

    private final int maxFailures;
    private final long windowMillis;

    private final Map<String, LoginAttemptWindow> clientLoginAttemptWindows = new ConcurrentHashMap<>();

    public FailedLoginRateLimitFilter(int maxFailures, Duration window) {
        this.maxFailures = maxFailures;
        this.windowMillis = window.toMillis();
    }

    @Override
    protected void doFilterInternal(
            HttpServletRequest request, 
            HttpServletResponse response,
            FilterChain filterChain) throws ServletException, IOException {

        String header = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (header == null || !header.regionMatches(true, 0, "Basic ", 0, 6)) {
            filterChain.doFilter(request, response);
            return;
        }

        String clientKey = request.getRemoteAddr();
        long now = System.currentTimeMillis();

        LoginAttemptWindow loginAttemptWindow = clientLoginAttemptWindows.get(clientKey);
        if (loginAttemptWindow != null && !isExpired(loginAttemptWindow, now) && loginAttemptWindow.fehlversuche() >= maxFailures) {
            long retryAfterSeconds = (loginAttemptWindow.start() + windowMillis - now) / 1000 + 1;
            response.setHeader(HttpHeaders.RETRY_AFTER, String.valueOf(retryAfterSeconds));
            response.setStatus(HttpStatus.TOO_MANY_REQUESTS.value());
            return;
        }

        filterChain.doFilter(request, response);

        if (response.getStatus() == HttpStatus.UNAUTHORIZED.value()) {
            clientLoginAttemptWindows.compute(clientKey, (key, current) ->
                    (current == null || isExpired(current, now))
                            ? new LoginAttemptWindow(now, 1)
                            : current.mitFehlversuch());
        } else {
            clientLoginAttemptWindows.remove(clientKey);
        }
    }

    private boolean isExpired(LoginAttemptWindow fenster, long now) {
        return now - fenster.start() >= windowMillis;
    }

    private record LoginAttemptWindow(long start, int fehlversuche) {
        LoginAttemptWindow mitFehlversuch() {
            return new LoginAttemptWindow(start, fehlversuche + 1);
        }
    }
}
