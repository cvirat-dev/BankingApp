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
import org.springframework.messaging.simp.SimpMessagingTemplate;

import com.demo.benachrichtigung_service.benachrichtigung.Benachrichtigung;
import com.demo.benachrichtigung_service.benachrichtigung.BenachrichtigungEvent;
import com.demo.benachrichtigung_service.benachrichtigung.BenachrichtigungRepository;
import com.demo.benachrichtigung_service.benachrichtigung.BenachrichtigungRequest;
import com.demo.benachrichtigung_service.benachrichtigung.BenachrichtigungService;
import com.demo.benachrichtigung_service.benachrichtigung.BenachrichtigungTyp;

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
        BenachrichtigungRequest request = createRequest();

        when(benachrichtigungRepository.save(any(Benachrichtigung.class)))
                .thenAnswer(invocation -> {
                    Benachrichtigung saved = invocation.getArgument(0);
                    saved.setId(1L);
                    return saved;
                });

        Benachrichtigung result = service.receive(request);

        assertNotNull(result);
        assertEquals(1L, result.getId());
        assertEquals(request.getTyp(), result.getTyp());
        assertEquals(request.getKontoId(), result.getKontoId());
        assertEquals(request.getIban(), result.getIban());
        assertEquals(request.getInhaber(), result.getInhaber());
        assertEquals(request.getNachricht(), result.getNachricht());
        assertNotNull(result.getTimestamp());

        verify(benachrichtigungRepository).save(any(Benachrichtigung.class));

        ArgumentCaptor<BenachrichtigungEvent> eventCaptor = ArgumentCaptor.forClass(BenachrichtigungEvent.class);
        verify(messagingTemplate).convertAndSend(eq("/topic/benachrichtigungen"), eventCaptor.capture());

        BenachrichtigungEvent event = eventCaptor.getValue();
        assertEquals(result.getInhaber(), event.getInhaber());
        assertEquals(result.getIban(), event.getIban());
        assertEquals(result.getNachricht(), event.getNachricht());
        assertEquals(result.getTimestamp(), event.getTimestamp());
        assertEquals(result.getTyp(), event.getTyp());
    }

    @Test
    void all_withoutFilters_shouldReturnAllEntries() {
        LocalDateTime now = LocalDateTime.now();
        Benachrichtigung first = createBenachrichtigung(BenachrichtigungTyp.KONTO, "DE001", now.minusDays(1));
        Benachrichtigung second = createBenachrichtigung(BenachrichtigungTyp.TRANSAKTION, "DE002", now);

        when(benachrichtigungRepository.findAll()).thenReturn(List.of(first, second));

        List<Benachrichtigung> result = service.all(null, null, null, null);

        assertEquals(2, result.size());
        assertTrue(result.contains(first));
        assertTrue(result.contains(second));
    }

    @Test
    void all_withTypAndIbanAndDateRange_shouldFilterCorrectly() {
        LocalDateTime base = LocalDateTime.now();
        Benachrichtigung match = createBenachrichtigung(BenachrichtigungTyp.KONTO, "DE123", base.minusHours(1));
        Benachrichtigung wrongTyp = createBenachrichtigung(BenachrichtigungTyp.TRANSAKTION, "DE123", base.minusHours(1));
        Benachrichtigung wrongIban = createBenachrichtigung(BenachrichtigungTyp.KONTO, "DE999", base.minusHours(1));
        Benachrichtigung tooOld = createBenachrichtigung(BenachrichtigungTyp.KONTO, "DE123", base.minusDays(2));
        Benachrichtigung tooNew = createBenachrichtigung(BenachrichtigungTyp.KONTO, "DE123", base.plusDays(2));

        when(benachrichtigungRepository.findAll())
                .thenReturn(List.of(match, wrongTyp, wrongIban, tooOld, tooNew));

        List<Benachrichtigung> result = service.all(
                BenachrichtigungTyp.KONTO,
                "DE123",
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

        Benachrichtigung atVon = createBenachrichtigung(BenachrichtigungTyp.KONTO, "DE555", von);
        Benachrichtigung atBis = createBenachrichtigung(BenachrichtigungTyp.KONTO, "DE555", bis);
        Benachrichtigung outside = createBenachrichtigung(BenachrichtigungTyp.KONTO, "DE555", bis.plusSeconds(1));

        when(benachrichtigungRepository.findAll()).thenReturn(List.of(atVon, atBis, outside));

        List<Benachrichtigung> result = service.all(null, null, von, bis);

        assertEquals(2, result.size());
        assertTrue(result.contains(atVon));
        assertTrue(result.contains(atBis));
    }

    private BenachrichtigungRequest createRequest() {
        BenachrichtigungRequest request = new BenachrichtigungRequest();
        request.setTyp(BenachrichtigungTyp.KONTO);
        request.setKontoId(100L);
        request.setIban("DE12500105170648489890");
        request.setInhaber("Max Mustermann");
        request.setNachricht("Kontostand aktualisiert");
        return request;
    }

    private Benachrichtigung createBenachrichtigung(BenachrichtigungTyp typ, String iban, LocalDateTime timestamp) {
        Benachrichtigung benachrichtigung = new Benachrichtigung();
        benachrichtigung.setTyp(typ);
        benachrichtigung.setKontoId(100L);
        benachrichtigung.setIban(iban);
        benachrichtigung.setInhaber("Max Mustermann");
        benachrichtigung.setNachricht("Test Nachricht");
        benachrichtigung.setTimestamp(timestamp);
        return benachrichtigung;
    }

}
