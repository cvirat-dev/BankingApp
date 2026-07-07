package com.demo.kontoservice.konto;

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

import com.demo.kontoservice.benachrichtigung.Aktion;
import com.demo.kontoservice.benachrichtigung.BenachrichtigungTyp;
import com.demo.kontoservice.benachrichtigung.KontoBenachrichtigungRequest;

@ExtendWith(MockitoExtension.class)
class KontoServiceTest {

    @Mock
    private KontoRepository kontoRepository;

    @Mock
    private KontoDbService kontoDbService;

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
        when(kontoDbService.erstelleKontoInDb(any(KontoCreateRequest.class))).thenReturn(input);

        // Act
        KontoCreateRequest kontoCreateRequest = new KontoCreateRequest();
        kontoCreateRequest.setInhaber(name);
        Konto result = kontoService.create(kontoCreateRequest);

        // Assert: result comes from KontoDbService
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(1L);
        assertThat(result.getInhaber()).isEqualTo(name);
        assertThat(result.getIban()).isEqualTo(iban);

        // Assert: DB write path is delegated
        verify(kontoDbService).erstelleKontoInDb(kontoCreateRequest);

        // Assert: notification payload is correct
        ArgumentCaptor<KontoBenachrichtigungRequest> requestCaptor = 
            ArgumentCaptor.forClass(KontoBenachrichtigungRequest.class);

        verify(restTemplate).postForObject(
            ArgumentMatchers.eq("http://benachrichtigung-service:8082/api/benachrichtigungen/konto"),
            requestCaptor.capture(), 
            ArgumentMatchers.eq(Void.class)
        );

        KontoBenachrichtigungRequest req = requestCaptor.getValue();
        assertThat(req.getTyp()).isEqualTo(BenachrichtigungTyp.KONTO);
        assertThat(req.getAktion()).isEqualTo(Aktion.ERSTELLEN);
        assertThat(req.getKontoId()).isEqualTo(1L);
        assertThat(req.getIban()).isEqualTo(iban);
        assertThat(req.getInhaber()).isEqualTo(name);
        assertThat(req.getNachricht()).contains("Neues Konto erstellt");
    }

    @Test
    void deleteById_sollteKontoLoeschen() {
        Konto konto = new Konto();
        konto.setId(1L);
        when(kontoRepository.findById(1L)).thenReturn(Optional.of(konto));

        kontoService.delete(1L);

        verify(kontoRepository).deleteById(1L);
    }

    @Test
    void deleteById_sollteException_wennKontoNichtGefunden() {
        when(kontoRepository.findById(42L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> kontoService.delete(42L))
                .isInstanceOf(RuntimeException.class)
                .hasMessage("Konto nicht gefunden");
    }

}
