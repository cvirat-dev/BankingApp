package com.demo.benachrichtigung_service.benachrichtigung;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.stereotype.Service;

@Service
public class BenachrichtigungService {

    @Autowired
    private BenachrichtigungRepository repository;

    @Autowired
    private SimpMessagingTemplate messagingTemplate;

    public Benachrichtigung receive(BenachrichtigungRequest request) {
        Benachrichtigung benachrichtigung = new Benachrichtigung();
        benachrichtigung.setTyp(request.getTyp());
        benachrichtigung.setKontoId(request.getKontoId());
        benachrichtigung.setIban(request.getIban());
        benachrichtigung.setInhaber(request.getInhaber());
        benachrichtigung.setNachricht(request.getNachricht());

        benachrichtigung.setTimestamp(LocalDateTime.now());
        Benachrichtigung gespeichert = repository.save(benachrichtigung);

        messagingTemplate.convertAndSend(
                "/topic/benachrichtigungen",
                new BenachrichtigungEvent(
                        gespeichert.getInhaber(),
                        gespeichert.getIban(),
                        gespeichert.getNachricht(),
                        gespeichert.getTimestamp(),
                        gespeichert.getTyp()
                )
        );

        return gespeichert;
    }

    public List<Benachrichtigung> all(
            BenachrichtigungTyp typ,
            String iban,
            LocalDateTime von,
            LocalDateTime bis
    ) {
        return repository.findAll().stream()
                .filter(b -> typ == null || b.getTyp() == typ)
                .filter(b -> iban == null || Objects.equals(b.getIban(), iban))
                .filter(b -> von == null || !b.getTimestamp().isBefore(von))
                .filter(b -> bis == null || !b.getTimestamp().isAfter(bis))
                .collect(Collectors.toList());
    }

}
