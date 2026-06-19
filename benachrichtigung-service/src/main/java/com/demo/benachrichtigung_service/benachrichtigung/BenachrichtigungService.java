package com.demo.benachrichtigung_service.benachrichtigung;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class BenachrichtigungService {

    @Autowired
    private BenachrichtigungRepository repository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public Benachrichtigung receive(BenachrichtigungRequest request) {
        log.info(
                "Empfange Benachrichtigung: typ={}, kontoId={}, iban={}",
                request.getTyp(),
                request.getKontoId(),
                request.getIban()
        );
        log.debug("Eingehender Benachrichtigungs-Request: {}", request);

        Benachrichtigung benachrichtigung = new Benachrichtigung();
        benachrichtigung.setTyp(request.getTyp());
        benachrichtigung.setKontoId(request.getKontoId());
        benachrichtigung.setIban(request.getIban());
        benachrichtigung.setInhaber(request.getInhaber());
        benachrichtigung.setNachricht(request.getNachricht());

        benachrichtigung.setTimestamp(LocalDateTime.now());
        Benachrichtigung gespeichert = repository.save(benachrichtigung);
        log.info(
                "Benachrichtigung gespeichert: id={}, typ={}, kontoId={}",
                gespeichert.getId(),
                gespeichert.getTyp(),
                gespeichert.getKontoId()
        );
        log.debug("Persistierte Benachrichtigung: {}", gespeichert);

        BenachrichtigungEvent event = new BenachrichtigungEvent(
                gespeichert.getInhaber(),
                gespeichert.getIban(),
                gespeichert.getNachricht(),
                gespeichert.getTimestamp(),
                gespeichert.getTyp()
        );
        log.info("Sende WebSocket-Event fuer Benachrichtigung id={}", gespeichert.getId());
        log.debug("WebSocket-Event Payload: {}", event);
        messagingTemplate.convertAndSend(
                "/topic/benachrichtigungen",
                event
        );
        log.info("WebSocket-Event erfolgreich versendet fuer Benachrichtigung id={}", gespeichert.getId());

        return gespeichert;
    }

    public List<Benachrichtigung> all(
            BenachrichtigungTyp typ,
            String iban,
            LocalDateTime von,
            LocalDateTime bis
    ) {
        log.info("Lese Benachrichtigungen mit Filtern: typ={}, iban={}, von={}, bis={}", typ, iban, von, bis);
        List<Benachrichtigung> alleBenachrichtigungen = repository.findAll();
        log.debug("Ungelesene Gesamtmenge aus Repository: {}", alleBenachrichtigungen.size());

        List<Benachrichtigung> gefiltert = alleBenachrichtigungen.stream()
        .filter(b -> typ == null || b.getTyp() == typ)
        .filter(b -> iban == null || Objects.equals(b.getIban(), iban))
        .filter(b -> von == null || !b.getTimestamp().isBefore(von))
        .filter(b -> bis == null || !b.getTimestamp().isAfter(bis))
        .collect(Collectors.toList());

        log.info("Benachrichtigungen geladen: anzahl={}", gefiltert.size());
        log.debug("Gefilterte Benachrichtigungen: {}", gefiltert);

        return gefiltert;
    }

}
