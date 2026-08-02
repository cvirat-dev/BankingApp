package com.demo.benachrichtigung_service;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.data.jpa.test.autoconfigure.DataJpaTest;
import org.springframework.data.jpa.domain.Specification;

import com.demo.benachrichtigung_service.benachrichtigung.AktionTyp;
import com.demo.benachrichtigung_service.benachrichtigung.Benachrichtigung;
import com.demo.benachrichtigung_service.benachrichtigung.BenachrichtigungRepository;
import com.demo.benachrichtigung_service.benachrichtigung.BenachrichtigungSpecs;
import com.demo.benachrichtigung_service.benachrichtigung.BenachrichtigungTyp;
import com.demo.benachrichtigung_service.benachrichtigung.BuchungBenachrichtigung;
import com.demo.benachrichtigung_service.benachrichtigung.KontoBenachrichtigung;

@DataJpaTest(properties = {
    "spring.jpa.hibernate.ddl-auto=none",
    "spring.sql.init.mode=always"
})
class BenachrichtigungSpecsDataJpaTest {

    @Autowired
    private BenachrichtigungRepository repository;

    @Test
    void mitFiltern_withBoundaryDates_shouldIncludeBoundaryValuesAndExcludeOutside() {
        LocalDateTime von = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime bis = LocalDateTime.of(2026, 1, 2, 0, 0);

        BuchungBenachrichtigung atVon = createBuchungBenachrichtigung(BenachrichtigungTyp.BUCHUNG, "DE555", von);
        BuchungBenachrichtigung atBis = createBuchungBenachrichtigung(BenachrichtigungTyp.BUCHUNG, "DE555", bis);
        BuchungBenachrichtigung outside = createBuchungBenachrichtigung(BenachrichtigungTyp.BUCHUNG, "DE555", bis.plusSeconds(1));

        repository.saveAll(List.of(atVon, atBis, outside));

        Specification<Benachrichtigung> spec = BenachrichtigungSpecs.mitFiltern(
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            von,
            bis
        );

        List<Benachrichtigung> result = repository.findAll(spec);

        assertEquals(2, result.size());
        assertTrue(result.stream().anyMatch(b -> von.equals(b.getTimestamp())));
        assertTrue(result.stream().anyMatch(b -> bis.equals(b.getTimestamp())));
    }

    @Test
    void mitFiltern_withTypAndIbanAndDateRange_shouldReturnOnlyMatchingEntries() {
        LocalDateTime base = LocalDateTime.now();

        BuchungBenachrichtigung match = createBuchungBenachrichtigung(BenachrichtigungTyp.BUCHUNG, "DE123", base.minusHours(1));
        BuchungBenachrichtigung wrongIban = createBuchungBenachrichtigung(BenachrichtigungTyp.BUCHUNG, "DE999", base.minusHours(1));
        BuchungBenachrichtigung tooOld = createBuchungBenachrichtigung(BenachrichtigungTyp.BUCHUNG, "DE123", base.minusDays(2));
        BuchungBenachrichtigung tooNew = createBuchungBenachrichtigung(BenachrichtigungTyp.BUCHUNG, "DE123", base.plusDays(2));

        repository.saveAll(List.of(match, wrongIban, tooOld, tooNew));

        Specification<Benachrichtigung> spec = BenachrichtigungSpecs.mitFiltern(
            BenachrichtigungTyp.BUCHUNG,
            null,
            null,
            null,
            null,
            null,
            "DE123",
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            base.minusDays(1),
            base.plusDays(1)
        );

        List<Benachrichtigung> result = repository.findAll(spec);

        assertEquals(1, result.size());
        assertEquals("DE123", ((BuchungBenachrichtigung) result.get(0)).getIban());
    }

    @Test
    void mitFiltern_withAktion_shouldReturnOnlyMatchingKontoEntries() {
        LocalDateTime base = LocalDateTime.now();

        KontoBenachrichtigung match = createKontoBenachrichtigung(BenachrichtigungTyp.KONTO, "DE123", base.minusHours(1));
        match.setAktion(AktionTyp.ERSTELLEN);
        KontoBenachrichtigung wrongAction = createKontoBenachrichtigung(BenachrichtigungTyp.KONTO, "DE123", base.minusHours(1));
        wrongAction.setAktion(AktionTyp.AKTUALISIEREN);

        repository.saveAll(List.of(match, wrongAction));

        Specification<Benachrichtigung> spec = BenachrichtigungSpecs.mitFiltern(
            BenachrichtigungTyp.KONTO,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            null,
            AktionTyp.ERSTELLEN,
            null,
            base.minusDays(1),
            base.plusDays(1)
        );

        List<Benachrichtigung> result = repository.findAll(spec);

        assertEquals(1, result.size());
        assertEquals(AktionTyp.ERSTELLEN, ((KontoBenachrichtigung) result.get(0)).getAktion());
    }

    private BuchungBenachrichtigung createBuchungBenachrichtigung(BenachrichtigungTyp typ, String iban, LocalDateTime timestamp) {
        BuchungBenachrichtigung benachrichtigung = new BuchungBenachrichtigung();
        benachrichtigung.setTyp(typ);
        benachrichtigung.setKontoId(100L);
        benachrichtigung.setBuchungId(200L);
        benachrichtigung.setIban(iban);
        benachrichtigung.setInhaber("Max Mustermann");
        benachrichtigung.setBetrag(10.0);
        benachrichtigung.setNachricht("Test Nachricht");
        benachrichtigung.setTimestamp(timestamp);
        return benachrichtigung;
    }

    private KontoBenachrichtigung createKontoBenachrichtigung(BenachrichtigungTyp typ, String iban, LocalDateTime timestamp) {
        KontoBenachrichtigung benachrichtigung = new KontoBenachrichtigung();
        benachrichtigung.setTyp(typ);
        benachrichtigung.setKontoId(100L);
        benachrichtigung.setIban(iban);
        benachrichtigung.setInhaber("Max Mustermann");
        benachrichtigung.setNachricht("Test Nachricht");
        benachrichtigung.setTimestamp(timestamp);
        return benachrichtigung;
    }
}
