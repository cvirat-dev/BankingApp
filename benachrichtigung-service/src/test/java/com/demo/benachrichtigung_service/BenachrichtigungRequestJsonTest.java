package com.demo.benachrichtigung_service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import org.junit.jupiter.api.Test;

import com.demo.benachrichtigung_service.benachrichtigung.BenachrichtigungTyp;
import com.demo.benachrichtigung_service.benachrichtigung.BuchungBenachrichtigungRequest;
import com.demo.benachrichtigung_service.benachrichtigung.KontoBenachrichtigungRequest;
import com.demo.benachrichtigung_service.benachrichtigung.TransaktionBenachrichtigungRequest;
import tools.jackson.databind.json.JsonMapper;

class BenachrichtigungRequestJsonTest {

    private final JsonMapper jsonMapper = JsonMapper.builder().build();

    @Test
    void kontoRequest_shouldIgnoreIncomingTyp() {
        KontoBenachrichtigungRequest request = jsonMapper.readValue(
            """
            {
              "typ": "BUCHUNG",
              "nachricht": "Kontodaten geaendert",
              "kontoId": 100,
              "iban": "DE12500105170648489890",
              "inhaber": "Max Mustermann",
              "aktion": "ERSTELLEN"
            }
            """,
            KontoBenachrichtigungRequest.class
        );

        assertEquals(BenachrichtigungTyp.KONTO, request.getTyp());
        assertEquals(100L, request.getKontoId());
        assertEquals("DE12500105170648489890", request.getIban());
    }

    @Test
    void buchungRequest_shouldIgnoreIncomingTyp() {
        BuchungBenachrichtigungRequest request = jsonMapper.readValue(
            """
            {
              "typ": "KONTO",
              "nachricht": "Buchung erstellt",
              "buchungId": 200,
              "kontoId": 100,
              "iban": "DE12500105170648489890",
              "inhaber": "Max Mustermann",
              "betrag": 42.5
            }
            """,
            BuchungBenachrichtigungRequest.class
        );

        assertEquals(BenachrichtigungTyp.BUCHUNG, request.getTyp());
        assertEquals(200L, request.getBuchungId());
        assertEquals(42.5, request.getBetrag());
    }

    @Test
    void transaktionRequest_shouldIgnoreIncomingTyp() {
        TransaktionBenachrichtigungRequest request = jsonMapper.readValue(
            """
            {
              "typ": "KONTO",
              "nachricht": "Transaktion ausgefuehrt",
              "transaktionId": 300,
              "quelleKontoId": 100,
              "zielKontoId": 101,
              "quelleIban": "DE12500105170648489890",
              "zielIban": "DE12500105170648489891",
              "quelleInhaber": "Max Mustermann",
              "zielInhaber": "Erika Musterfrau",
              "betrag": 99.99
            }
            """,
            TransaktionBenachrichtigungRequest.class
        );

        assertEquals(BenachrichtigungTyp.TRANSAKTION, request.getTyp());
        assertEquals(300L, request.getTransaktionId());
        assertEquals(101L, request.getZielKontoId());
    }
}
