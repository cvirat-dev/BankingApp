package com.demo.benachrichtigung_service.security;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequiredArgsConstructor
public class WsTicketController {

    private final WsTicketService ticketService;

    @GetMapping("/api/ws-ticket")
    public Map<String, String> ticket() {
        return Map.of("ticket", ticketService.issue());
    }
}
