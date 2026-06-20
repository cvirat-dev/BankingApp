package com.demo.benachrichtigung_service.benachrichtigung;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;

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
        log.info("Empfange Benachrichtigung: typ={}", request.getTyp());
        log.debug("Eingehender Benachrichtigungs-Request: {}", request);

        Benachrichtigung benachrichtigung = toEntity(request);
        benachrichtigung.setTimestamp(LocalDateTime.now());
        Benachrichtigung gespeichert = repository.save(benachrichtigung);
        log.info("Benachrichtigung gespeichert: id={}, typ={}", gespeichert.getId(), gespeichert.getTyp());
        log.debug("Persistierte Benachrichtigung: {}", gespeichert);

        BenachrichtigungEvent event = toEvent(gespeichert);
        log.info("Sende WebSocket-Event fuer Benachrichtigung id={}", gespeichert.getId());
        log.debug("WebSocket-Event Payload: {}", event);
        messagingTemplate.convertAndSend(
                "/topic/benachrichtigungen",
                event
        );
        log.info("WebSocket-Event erfolgreich versendet fuer Benachrichtigung id={}", gespeichert.getId());

        return gespeichert;
    }

        private Benachrichtigung toEntity(BenachrichtigungRequest request) {
                Objects.requireNonNull(request, "request must not be null");

                if (request instanceof KontoBenachrichtigungRequest kontoRequest) {
                        KontoBenachrichtigung benachrichtigung = new KontoBenachrichtigung();
                        benachrichtigung.setTyp(kontoRequest.getTyp());
                        benachrichtigung.setNachricht(kontoRequest.getNachricht());
                        benachrichtigung.setKontoId(kontoRequest.getKontoId());
                        benachrichtigung.setIban(kontoRequest.getIban());
                        benachrichtigung.setInhaber(kontoRequest.getInhaber());
                        benachrichtigung.setAktion(kontoRequest.getAktion());
                        return benachrichtigung;
                }

                if (request instanceof BuchungBenachrichtigungRequest buchungRequest) {
                        BuchungBenachrichtigung benachrichtigung = new BuchungBenachrichtigung();
                        benachrichtigung.setTyp(buchungRequest.getTyp());
                        benachrichtigung.setNachricht(buchungRequest.getNachricht());
                        benachrichtigung.setBuchungId(buchungRequest.getBuchungId());
                        benachrichtigung.setKontoId(buchungRequest.getKontoId());
                        benachrichtigung.setIban(buchungRequest.getIban());
                        benachrichtigung.setInhaber(buchungRequest.getInhaber());
                        benachrichtigung.setBetrag(buchungRequest.getBetrag());
                        return benachrichtigung;
                }

                if (request instanceof TransaktionBenachrichtigungRequest transaktionRequest) {
                        TransaktionBenachrichtigung benachrichtigung = new TransaktionBenachrichtigung();
                        benachrichtigung.setTyp(transaktionRequest.getTyp());
                        benachrichtigung.setNachricht(transaktionRequest.getNachricht());
                        benachrichtigung.setTransaktionId(transaktionRequest.getTransaktionId());
                        benachrichtigung.setQuelleKontoId(transaktionRequest.getQuelleKontoId());
                        benachrichtigung.setZielKontoId(transaktionRequest.getZielKontoId());
                        benachrichtigung.setQuelleIban(transaktionRequest.getQuelleIban());
                        benachrichtigung.setZielIban(transaktionRequest.getZielIban());
                        benachrichtigung.setQuelleInhaber(transaktionRequest.getQuelleInhaber());
                        benachrichtigung.setZielInhaber(transaktionRequest.getZielInhaber());
                        benachrichtigung.setBetrag(transaktionRequest.getBetrag());
                        return benachrichtigung;
                }

                throw new IllegalArgumentException("Unbekannter Benachrichtigungs-Request-Typ: " + request.getClass().getName());
        }

        private BenachrichtigungEvent toEvent(Benachrichtigung benachrichtigung) {
                Objects.requireNonNull(benachrichtigung, "benachrichtigung must not be null");

                if (benachrichtigung instanceof KontoBenachrichtigung kontoBenachrichtigung) {
                        return new BenachrichtigungEvent(
                                        kontoBenachrichtigung.getInhaber(),
                                        kontoBenachrichtigung.getIban(),
                                        kontoBenachrichtigung.getNachricht(),
                                        kontoBenachrichtigung.getTimestamp(),
                                        kontoBenachrichtigung.getTyp()
                        );
                }

                if (benachrichtigung instanceof BuchungBenachrichtigung buchungBenachrichtigung) {
                        return new BenachrichtigungEvent(
                                        buchungBenachrichtigung.getInhaber(),
                                        buchungBenachrichtigung.getIban(),
                                        buchungBenachrichtigung.getNachricht(),
                                        buchungBenachrichtigung.getTimestamp(),
                                        buchungBenachrichtigung.getTyp()
                        );
                }

                if (benachrichtigung instanceof TransaktionBenachrichtigung transaktionBenachrichtigung) {
                        return new BenachrichtigungEvent(
                                        transaktionBenachrichtigung.getQuelleInhaber(),
                                        transaktionBenachrichtigung.getQuelleIban(),
                                        transaktionBenachrichtigung.getNachricht(),
                                        transaktionBenachrichtigung.getTimestamp(),
                                        transaktionBenachrichtigung.getTyp()
                        );
                }

                throw new IllegalArgumentException("Unbekannter Benachrichtigungs-Typ: " + benachrichtigung.getClass().getName());
        }

    public List<Benachrichtigung> all(
        BenachrichtigungTyp typ,
        Long kontoId,
        Long buchungId,
        Long transaktionId,
        Long quelleKontoId,
        Long zielKontoId,
        String iban,
        String quelleIban,
        String zielIban,
        String inhaber,
        String quelleInhaber,
        String zielInhaber,
        AktionTyp aktion,
        Double betrag,
        LocalDateTime von,
        LocalDateTime bis
        ) {
        log.info(
                "Lese Benachrichtigungen mit Filtern: typ={}, kontoId={}, buchungId={}, transaktionId={}, quelleKontoId={}, zielKontoId={}, iban={}, quelleIban={}, zielIban={}, inhaber={}, quelleInhaber={}, zielInhaber={}, aktion={}, betrag={}, von={}, bis={}",
                typ,
                kontoId,
                buchungId,
                transaktionId,
                quelleKontoId,
                zielKontoId,
                iban,
                quelleIban,
                zielIban,
                inhaber,
                quelleInhaber,
                zielInhaber,
                aktion,
                betrag,
                von,
                bis
        );
        List<Benachrichtigung> gefiltert = repository.findAll(
                BenachrichtigungSpecs.mitFiltern(
                                typ,
                                kontoId,
                                buchungId,
                                transaktionId,
                                quelleKontoId,
                                zielKontoId,
                                iban,
                                quelleIban,
                                zielIban,
                                inhaber,
                                quelleInhaber,
                                zielInhaber,
                                aktion,
                                betrag,
                                von,
                                bis
                                )
        );

        log.info("Benachrichtigungen geladen: anzahl={}", gefiltert.size());
        log.debug("Gefilterte Benachrichtigungen: {}", gefiltert);

        return gefiltert;
    }

}
