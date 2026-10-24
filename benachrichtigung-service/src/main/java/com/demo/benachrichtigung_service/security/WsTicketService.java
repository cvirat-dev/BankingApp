package com.demo.benachrichtigung_service.security;

import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class WsTicketService {

    private static final Duration DURATION = Duration.ofSeconds(30);
    private final Map<String, Instant> tickets = new ConcurrentHashMap<>();

    public String issue() {
        Instant now = Instant.now();
        tickets.values().removeIf(expiry -> expiry.isBefore(now));
        String ticket = UUID.randomUUID().toString();
        tickets.put(ticket, now.plus(DURATION));
        return ticket;
    }

    public boolean consume(String ticket) {
        if (ticket == null) {
            return false;
        }
        Instant expiry = tickets.remove(ticket);
        return expiry != null && Instant.now().isBefore(expiry);
    }
}
