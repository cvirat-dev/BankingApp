package com.demo.benachrichtigung_service;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.demo.benachrichtigung_service.benachrichtigung.AktionTyp;
import com.demo.benachrichtigung_service.benachrichtigung.Benachrichtigung;
import com.demo.benachrichtigung_service.benachrichtigung.BenachrichtigungEvent;
import com.demo.benachrichtigung_service.benachrichtigung.BenachrichtigungRepository;
import com.demo.benachrichtigung_service.benachrichtigung.BenachrichtigungService;
import com.demo.benachrichtigung_service.benachrichtigung.BenachrichtigungTyp;
import com.demo.benachrichtigung_service.benachrichtigung.KontoBenachrichtigung;
import com.demo.benachrichtigung_service.benachrichtigung.KontoBenachrichtigungRequest;

@ExtendWith(MockitoExtension.class)
public class BenachrichtigungServiceTest {

    @Mock
    private BenachrichtigungRepository benachrichtigungRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @InjectMocks
    private BenachrichtigungService service;

    @Test
    void receive_shouldSaveAndPublishEvent() {
        KontoBenachrichtigungRequest request = createRequest();

        when(benachrichtigungRepository.save(any(Benachrichtigung.class)))
                .thenAnswer(invocation -> {
                    Benachrichtigung saved = invocation.getArgument(0);
                    saved.setId(1L);
                    return saved;
                });

        Benachrichtigung result = service.receive(request);

        assertNotNull(result);
        assertTrue(result instanceof KontoBenachrichtigung);
        KontoBenachrichtigung gespeicherteBenachrichtigung = (KontoBenachrichtigung) result;
        assertEquals(1L, result.getId());
        assertEquals(request.getTyp(), gespeicherteBenachrichtigung.getTyp());
        assertEquals(request.getKontoId(), gespeicherteBenachrichtigung.getKontoId());
        assertEquals(request.getIban(), gespeicherteBenachrichtigung.getIban());
        assertEquals(request.getInhaber(), gespeicherteBenachrichtigung.getInhaber());
        assertEquals(request.getNachricht(), gespeicherteBenachrichtigung.getNachricht());
        assertNotNull(gespeicherteBenachrichtigung.getTimestamp());

        verify(benachrichtigungRepository).save(any(Benachrichtigung.class));

        ArgumentCaptor<BenachrichtigungEvent> eventCaptor = ArgumentCaptor.forClass(BenachrichtigungEvent.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/benachrichtigungen"), eventCaptor.capture());

        BenachrichtigungEvent event = eventCaptor.getValue();
        assertEquals(gespeicherteBenachrichtigung.getInhaber(), event.getInhaber());
        assertEquals(gespeicherteBenachrichtigung.getIban(), event.getIban());
        assertEquals(gespeicherteBenachrichtigung.getNachricht(), event.getNachricht());
        assertEquals(gespeicherteBenachrichtigung.getTimestamp(), event.getTimestamp());
        assertEquals(gespeicherteBenachrichtigung.getTyp(), event.getTyp());
    }

    @Test
    void all_withoutFilters_shouldReturnAllEntries() {
        LocalDateTime now = LocalDateTime.now();
        Benachrichtigung first = createKontoBenachrichtigung(BenachrichtigungTyp.KONTO, "DE001", now.minusDays(1));
        Benachrichtigung second = createKontoBenachrichtigung(BenachrichtigungTyp.TRANSAKTION, "DE002", now);

        when(benachrichtigungRepository.findAll(any(Specification.class))).thenReturn(List.of(first, second));

        List<Benachrichtigung> result = service.all(
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
            null,
            null
        );

        assertEquals(2, result.size());
        assertTrue(result.contains(first));
        assertTrue(result.contains(second));
    }

    @Test
    void all_withTypAndIbanAndDateRange_shouldFilterCorrectly() {
        LocalDateTime base = LocalDateTime.now();
        Benachrichtigung match = createKontoBenachrichtigung(BenachrichtigungTyp.KONTO, "DE123", base.minusHours(1));
        Benachrichtigung wrongTyp = createKontoBenachrichtigung(BenachrichtigungTyp.TRANSAKTION, "DE123", base.minusHours(1));
        Benachrichtigung wrongIban = createKontoBenachrichtigung(BenachrichtigungTyp.KONTO, "DE999", base.minusHours(1));
        Benachrichtigung tooOld = createKontoBenachrichtigung(BenachrichtigungTyp.KONTO, "DE123", base.minusDays(2));
        Benachrichtigung tooNew = createKontoBenachrichtigung(BenachrichtigungTyp.KONTO, "DE123", base.plusDays(2));

        when(benachrichtigungRepository.findAll(any(Specification.class)))
            .thenReturn(List.of(match, wrongTyp, wrongIban, tooOld, tooNew));

        List<Benachrichtigung> result = service.all(
            BenachrichtigungTyp.KONTO,
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

        assertEquals(1, result.size());
        assertEquals(match, result.get(0));
    }

    @Test
    void all_withBoundaryDates_shouldIncludeBoundaryValues() {
        LocalDateTime von = LocalDateTime.of(2026, 1, 1, 0, 0);
        LocalDateTime bis = LocalDateTime.of(2026, 1, 2, 0, 0);

        Benachrichtigung atVon = createKontoBenachrichtigung(BenachrichtigungTyp.KONTO, "DE555", von);
        Benachrichtigung atBis = createKontoBenachrichtigung(BenachrichtigungTyp.KONTO, "DE555", bis);
        Benachrichtigung outside = createKontoBenachrichtigung(BenachrichtigungTyp.KONTO, "DE555", bis.plusSeconds(1));

        when(benachrichtigungRepository.findAll(any(Specification.class))).thenReturn(List.of(atVon, atBis, outside));

        List<Benachrichtigung> result = service.all(
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

        assertEquals(2, result.size());
        assertTrue(result.contains(atVon));
        assertTrue(result.contains(atBis));
    }

    private KontoBenachrichtigungRequest createRequest() {
        KontoBenachrichtigungRequest request = new KontoBenachrichtigungRequest();
        request.setTyp(BenachrichtigungTyp.KONTO);
        request.setKontoId(100L);
        request.setIban("DE12500105170648489890");
        request.setInhaber("Max Mustermann");
        request.setNachricht("Kontostand aktualisiert");
        request.setAktion(AktionTyp.ERSTELLEN);
        return request;
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
