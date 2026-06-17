package com.demo.kontoservice;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
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

import com.demo.kontoservice.benachrichtigung.BenachrichtigungRequest;
import com.demo.kontoservice.konto.Konto;
import com.demo.kontoservice.konto.KontoDbService;
import com.demo.kontoservice.konto.KontoRepository;
import com.demo.kontoservice.konto.KontoService;
import com.demo.kontoservice.konto.Transaktion;
import com.demo.kontoservice.konto.TransaktionRepository;

@ExtendWith(MockitoExtension.class)
class KontoServiceTest {

    @Mock
    private KontoRepository kontoRepository;

    @Mock
    private KontoDbService kontoDbService;

    @Mock
    private TransaktionRepository transaktionRepository;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private KontoService kontoService;

    @Test
    void createKonto_sollteKontoErstellen_undBenachrichtigungSenden() {
        // Arrange
        Konto input = new Konto();
        input.setId(1L);
        String name = "Max Mustermann";
        input.setInhaber(name);
        String iban = "DE1234567890";
        input.setIban(iban);
        when(kontoDbService.erstelleKontoInDb(any(Konto.class))).thenReturn(input);

        // Act
        Konto result = kontoService.createKonto(input);

        // Assert: result comes from KontoDbService
        assertThat(result).isNotNull();
        assertThat(result.getInhaber()).isEqualTo(name);
        assertThat(result.getIban()).isEqualTo(iban);

        // Assert: DB write path is delegated
        verify(kontoDbService).erstelleKontoInDb(input);

        // Assert: notification payload is correct
        ArgumentCaptor<BenachrichtigungRequest> requestCaptor = 
            ArgumentCaptor.forClass(BenachrichtigungRequest.class);

        verify(restTemplate).postForObject(
            ArgumentMatchers.eq("http://benachrichtigung-service:8082/api/benachrichtigungen"),
            requestCaptor.capture(), 
            ArgumentMatchers.eq(Void.class)
        );

        BenachrichtigungRequest req = requestCaptor.getValue();
        assertThat(req.getTyp()).isEqualTo(com.demo.kontoservice.benachrichtigung.BenachrichtigungTyp.KONTO);
        assertThat(req.getKontoId()).isEqualTo(1L);
        assertThat(req.getIban()).isEqualTo(iban);
        assertThat(req.getInhaber()).isEqualTo(name);
        assertThat(req.getNachricht()).contains("Neues Konto erstellt");
    }

    @Test
    void buchung_sollteKontostandErhoehen_undTransaktionSpeichern() {
        BigDecimal kontostand = new BigDecimal("100");
        BigDecimal buchungsBetrag = new BigDecimal("50");
        String Einzahlung = "Einzahlung";

        // Arrange
        Konto konto = new Konto();
        konto.setId(1L);
        konto.setKontostand(kontostand);

        when(kontoRepository.findById(1L)).thenReturn(Optional.of(konto));
        when(kontoRepository.save(any())).thenReturn(konto);
        when(transaktionRepository.save(any())).thenAnswer(i -> i.getArgument(0));

        // Act
        Transaktion result = kontoService.buchung(1L, buchungsBetrag, Einzahlung);

        // Assert: Kontostand wurde korrekt erhöht
        ArgumentCaptor<Konto> kontoCaptor = ArgumentCaptor.forClass(Konto.class);
        verify(kontoRepository).save(kontoCaptor.capture());
        assertThat(kontoCaptor.getValue().getKontostand())
                .isEqualByComparingTo(kontostand.add(buchungsBetrag));

        // Assert: Transaktion wurde korrekt gespeichert
        assertThat(result.getKontoId()).isEqualTo(1L);
        assertThat(result.getBetrag()).isEqualByComparingTo(buchungsBetrag);
        assertThat(result.getBeschreibung()).isEqualTo(Einzahlung);
        assertThat(result.getDatum()).isNotNull();
    }

    @Test
    void buchung_shouldThrowExceptionWhen_KontoNotFound() {
        when(kontoRepository.findById(99L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> kontoService.buchung(99L, BigDecimal.TEN, "Test"))
                .isInstanceOf(RuntimeException.class);
    }

    @Test
    void getTransaktionen_shouldReturnList() {
        Transaktion t = new Transaktion();
        t.setKontoId(1L);
        when(transaktionRepository.findByKontoId(1L)).thenReturn(List.of(t));

        List<Transaktion> result = kontoService.getTransaktionen(1L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getKontoId()).isEqualTo(1L);
    }

    @Test
    void deleteById_sollteKontoUndTransaktionenLoeschen() {
        Konto konto = new Konto();
        konto.setId(1L);
        when(kontoRepository.findById(1L)).thenReturn(Optional.of(konto));

        kontoService.deleteById(1L);

        verify(transaktionRepository).deleteByKontoId(1L);
        verify(kontoRepository).deleteById(1L);
    }

    @Test
    void deleteById_sollteException_wennKontoNichtGefunden() {
        when(kontoRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> kontoService.deleteById(42L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Konto nicht gefunden");
    }

}
