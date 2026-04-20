package com.demo.benachrichtigung_service.benachrichtigung;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/benachrichtigungen")
@CrossOrigin(origins = "*")
public class BenachrichtigungController {

    @Autowired
    private BenachrichtigungRepository benachrichtigungRepository;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Benachrichtigung empfangen(@Valid @RequestBody BenachrichtigungRequest request) {
        Benachrichtigung benachrichtigung = new Benachrichtigung();
        benachrichtigung.setNachricht(request.getNachricht());
        benachrichtigung.setTimestamp(LocalDateTime.now());
        return benachrichtigungRepository.save(benachrichtigung);
    }

    @GetMapping
    public List<Benachrichtigung> all() { return benachrichtigungRepository.findAll(); }
}
