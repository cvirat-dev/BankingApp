package com.demo.kontoservice.buchung;

import java.math.BigDecimal;
import java.util.List;

import org.assertj.core.api.Assertions;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.assertj.core.api.AssertionsForClassTypes.assertThat;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.ArgumentMatchers;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import com.demo.kontoservice.benachrichtigung.BenachrichtigungTyp;
import com.demo.kontoservice.benachrichtigung.BuchungBenachrichtigungRequest;
import com.demo.kontoservice.konto.Konto;
import com.demo.kontoservice.konto.KontoService;

@ExtendWith(MockitoExtension.class)
class BuchungServiceTest {

    @Mock
    private KontoService kontoService;

    @Mock
    private BuchungRepository buchungRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private BuchungService buchungService;

    @Test
    void buchung_sollteKontostandErhoehen_undBuchungSpeichern() {
        BigDecimal kontostand = new BigDecimal("100");
        BigDecimal buchungsBetrag = new BigDecimal("50");
        String beschreibung = "Einzahlung";

        Konto konto = new Konto();
        konto.setId(1L);
        konto.setIban("DE445566778899");
        konto.setInhaber("Erika Musterfrau");
        konto.setKontostand(kontostand);

        BuchungRequest buchungRequest = new BuchungRequest();
        buchungRequest.setKontoId(1L);
        buchungRequest.setBetrag(buchungsBetrag);
        buchungRequest.setBeschreibung(beschreibung);

        when(kontoService.get(1L)).thenReturn(konto);
        when(buchungRepository.save(any())).thenAnswer(invocation -> invocation.getArgument(0));
        when(restTemplate.postForObject(any(String.class), any(), ArgumentMatchers.eq(Void.class))).thenReturn(null);

        Buchung result = buchungService.create(buchungRequest);

        ArgumentCaptor<Konto> kontoCaptor = ArgumentCaptor.forClass(Konto.class);
        verify(kontoService).save(kontoCaptor.capture());
        assertThat(kontoCaptor.getValue().getKontostand())
                .isEqualByComparingTo(kontostand.add(buchungsBetrag));

        assertThat(result.getKontoId()).isEqualTo(1L);
        assertThat(result.getBetrag()).isEqualByComparingTo(buchungsBetrag);
        assertThat(result.getBeschreibung()).isEqualTo(beschreibung);
        assertThat(result.getDatum()).isNotNull();

        ArgumentCaptor<BuchungBenachrichtigungRequest> requestCaptor =
                ArgumentCaptor.forClass(BuchungBenachrichtigungRequest.class);
        verify(restTemplate).postForObject(
                ArgumentMatchers.eq("http://benachrichtigung-service:8082/api/benachrichtigungen/buchungen"),
                requestCaptor.capture(),
                ArgumentMatchers.eq(Void.class)
        );

        BuchungBenachrichtigungRequest request = requestCaptor.getValue();
        assertThat(request.getTyp()).isEqualTo(BenachrichtigungTyp.BUCHUNG);
        assertThat(request.getKontoId()).isEqualTo(1L);
        assertThat(request.getIban()).isEqualTo("DE445566778899");
        assertThat(request.getInhaber()).isEqualTo("Erika Musterfrau");
        assertThat(request.getBetrag()).isEqualByComparingTo(buchungsBetrag);
        assertThat(request.getNachricht()).contains("Buchung");
        assertThat(request.getNachricht()).contains("Erika Musterfrau");
    }

    @Test
    void buchung_shouldThrowExceptionWhen_KontoNotFound() {
        BuchungRequest buchungRequest = new BuchungRequest();
        buchungRequest.setKontoId(99L);
        buchungRequest.setBetrag(BigDecimal.TEN);
        buchungRequest.setBeschreibung("Test");
        boolean benachrichtigen = true;

        when(kontoService.get(99L)).thenThrow(new RuntimeException("Konto nicht gefunden"));

        assertThatThrownBy(() -> buchungService.create(buchungRequest))
                .isInstanceOf(RuntimeException.class)
                .hasMessageContaining("Konto nicht gefunden");
    }

    @Test
    void getBuchungen_shouldReturnList() {
        Buchung buchung = new Buchung();
        buchung.setKontoId(1L);
        when(buchungRepository.findByKontoId(1L)).thenReturn(List.of(buchung));

        List<Buchung> result = buchungService.getBuchungen(1L);

        Assertions.assertThat(result).hasSize(1);
        Assertions.assertThat(result.getFirst().getKontoId()).isEqualTo(1L);
    }
}
