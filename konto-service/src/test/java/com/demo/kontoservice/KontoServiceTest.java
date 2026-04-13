package com.demo.kontoservice;

import com.demo.kontoservice.konto.*;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.math.BigDecimal;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KontoServiceTest {

    @Mock
    KontoRepository kontoRepository;

    @Mock
    private TransaktionRepository transaktionRepository;

    @InjectMocks
    KontoService kontoService;

    // ── buchung() ────────────────────────────────────────────────
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

    // ── getTransaktionen() ───────────────────────────────────────
    @Test
    void getTransaktionen_shouldReturnList() {
        Transaktion t = new Transaktion();
        t.setKontoId(1L);
        when(transaktionRepository.findByKontoId(1L)).thenReturn(List.of(t));

        List<Transaktion> result = kontoService.getTransaktionen(1L);

        assertThat(result).hasSize(1);
        assertThat(result.getFirst().getKontoId()).isEqualTo(1L);
    }

    // ── deleteById() ─────────────────────────────────────────────
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
