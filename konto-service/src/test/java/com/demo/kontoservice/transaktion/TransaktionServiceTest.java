package com.demo.kontoservice.transaktion;

import java.math.BigDecimal;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import com.demo.kontoservice.buchung.BuchungRequest;
import com.demo.kontoservice.buchung.BuchungService;
import com.demo.kontoservice.konto.Konto;
import com.demo.kontoservice.konto.KontoService;

@ExtendWith(MockitoExtension.class)
class TransaktionServiceTest {

    @Mock
    private TransaktionRepository transaktionRepository;

    @Mock
    private KontoService kontoService;

    @Mock
    private BuchungService buchungService;

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private TransaktionService transaktionService;

    @Test
    void getAll_shouldReturnAllTransaktionen() {
        Transaktion transaktion = new Transaktion();
        transaktion.setId(1L);
        when(transaktionRepository.findAll()).thenReturn(List.of(transaktion));

        List<Transaktion> result = transaktionService.getAll();

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getId()).isEqualTo(1L);
    }

    @Test
    void get_shouldReturnTransaktionenForKontoId() {
        Transaktion transaktion = new Transaktion();
        transaktion.setQuelleKontoId(1L);
        transaktion.setZielKontoId(2L);
        when(transaktionRepository.findByQuelleKontoIdOrZielKontoId(1L, 1L)).thenReturn(List.of(transaktion));

        List<Transaktion> result = transaktionService.getByKontoId(1L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getQuelleKontoId()).isEqualTo(1L);
        assertThat(result.getFirst().getZielKontoId()).isEqualTo(2L);
    }

    @Test
    void get_shouldReturnTransaktion() {
        Transaktion transaktion = new Transaktion();
        transaktion.setId(7L);
        when(transaktionRepository.findById(7L)).thenReturn(java.util.Optional.of(transaktion));

        Transaktion result = transaktionService.get(7L);

        assertThat(result.getId()).isEqualTo(7L);
    }

    @Test
    void create_shouldCreateDebitAndCreditBookings_thenPersistTransaktion() {
        TransaktionRequest transaktion = new TransaktionRequest();
        transaktion.setQuelleKontoId(10L);
        transaktion.setZielKontoId(20L);
        transaktion.setBetrag(new BigDecimal("42.50"));
        transaktion.setBeschreibung("Miete");

        Konto quellKonto = new Konto();
        quellKonto.setId(10L);
        quellKonto.setIban("DE00123456789012345678");
        quellKonto.setInhaber("Max Mustermann");

        Konto zielKonto = new Konto();
        zielKonto.setId(20L);
        zielKonto.setIban("DE00987654321098765432");
        zielKonto.setInhaber("Erika Musterfrau");

        Transaktion gespeicherteTransaktion = new Transaktion();
        gespeicherteTransaktion.setId(99L);
        gespeicherteTransaktion.setQuelleKontoId(10L);
        gespeicherteTransaktion.setZielKontoId(20L);
        gespeicherteTransaktion.setBetrag(new BigDecimal("42.50"));
        gespeicherteTransaktion.setBeschreibung("Miete");

        when(transaktionRepository.save(any(Transaktion.class))).thenReturn(gespeicherteTransaktion);
        when(kontoService.get(10L)).thenReturn(quellKonto);
        when(kontoService.get(20L)).thenReturn(zielKonto);

        Transaktion result = transaktionService.create(transaktion);

        ArgumentCaptor<BuchungRequest> buchungCaptor = ArgumentCaptor.forClass(BuchungRequest.class);
        verify(buchungService, times(2)).create(buchungCaptor.capture());

        List<BuchungRequest> buchungen = buchungCaptor.getAllValues();
        assertThat(buchungen).hasSize(2);

        BuchungRequest abbuchung = buchungen.stream()
            .filter(b -> Long.valueOf(10L).equals(b.getKontoId()))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Abbuchung fuer Quelle-Konto 10 nicht gefunden"));
        assertThat(abbuchung.getKontoId()).isEqualTo(10L);
        assertThat(abbuchung.getBetrag()).isEqualByComparingTo("-42.50");
        assertThat(abbuchung.getBeschreibung()).isEqualTo("Überweisung an Konto 20: Miete");

        BuchungRequest gutschrift = buchungen.stream()
            .filter(b -> Long.valueOf(20L).equals(b.getKontoId()))
            .findFirst()
            .orElseThrow(() -> new AssertionError("Gutschrift fuer Ziel-Konto 20 nicht gefunden"));
        assertThat(gutschrift.getKontoId()).isEqualTo(20L);
        assertThat(gutschrift.getBetrag()).isEqualByComparingTo("42.50");
        assertThat(gutschrift.getBeschreibung()).isEqualTo("Überweisung von Konto 10: Miete");

        verify(transaktionRepository).save(any(Transaktion.class));
        verify(restTemplate).postForObject(any(String.class), any(), org.mockito.ArgumentMatchers.eq(Void.class));
        assertThat(result.getId()).isEqualTo(99L);
    }

    @Test
    void create_shouldRejectNullTransaktion() {
        assertThatThrownBy(() -> transaktionService.create(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessage("Transaktion darf nicht null sein.");

        verifyNoInteractions(transaktionRepository, buchungService, restTemplate);
    }
}
