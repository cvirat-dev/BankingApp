package com.demo.benachrichtigung_service.benachrichtigung;

import java.time.LocalDateTime;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/benachrichtigungen")
@CrossOrigin(origins = "*")
public class BenachrichtigungController {

    @Autowired
    private BenachrichtigungRepository repository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public Benachrichtigung receive(@Valid @RequestBody BenachrichtigungRequest request) {
        Benachrichtigung benachrichtigung = new Benachrichtigung();
        benachrichtigung.setTyp(request.getTyp());
        benachrichtigung.setKontoId(request.getKontoId());
        benachrichtigung.setInhaber(request.getInhaber());
        benachrichtigung.setNachricht(request.getNachricht());
        benachrichtigung.setTimestamp(LocalDateTime.now());
        Benachrichtigung gespeichert = repository.save(benachrichtigung);

        messagingTemplate.convertAndSend(
                "/topic/benachrichtigungen",
                new BenachrichtigungEvent(
                        gespeichert.getKontoId(),
                        gespeichert.getInhaber(),
                        gespeichert.getNachricht(),
                        gespeichert.getTimestamp()
                )
        );

        return gespeichert;
    }

    @GetMapping
    public List<Benachrichtigung> all() { return repository.findAll(); }
}
