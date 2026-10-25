package com.demo.benachrichtigung_service;

import java.time.LocalDateTime;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.demo.benachrichtigung_service.benachrichtigung.AktionTyp;
import com.demo.benachrichtigung_service.benachrichtigung.BenachrichtigungController;
import com.demo.benachrichtigung_service.benachrichtigung.BenachrichtigungService;
import com.demo.benachrichtigung_service.benachrichtigung.BenachrichtigungTyp;
import com.demo.benachrichtigung_service.benachrichtigung.BuchungBenachrichtigung;
import com.demo.benachrichtigung_service.benachrichtigung.BuchungBenachrichtigungRequest;
import com.demo.benachrichtigung_service.benachrichtigung.KontoBenachrichtigung;
import com.demo.benachrichtigung_service.benachrichtigung.KontoBenachrichtigungRequest;
import com.demo.benachrichtigung_service.benachrichtigung.TransaktionBenachrichtigung;
import com.demo.benachrichtigung_service.benachrichtigung.TransaktionBenachrichtigungRequest;
import com.demo.benachrichtigung_service.config.SecurityConfig;

@WebMvcTest(BenachrichtigungController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = "demo.auth.password=test")
@WithMockUser
class BenachrichtigungControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private BenachrichtigungService benachrichtigungService;

    @Test
    void postKonto_should201_withKontoResponse() throws Exception {
        KontoBenachrichtigung gespeichert = new KontoBenachrichtigung();
        gespeichert.setId(11L);
        gespeichert.setTyp(BenachrichtigungTyp.KONTO);
        gespeichert.setTimestamp(LocalDateTime.of(2026, 7, 2, 10, 0));
        gespeichert.setKontoId(100L);
        gespeichert.setAktion(AktionTyp.ERSTELLEN);
        when(benachrichtigungService.receive(any(KontoBenachrichtigungRequest.class))).thenReturn(gespeichert);

        mockMvc.perform(post("/api/benachrichtigungen/konten")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "nachricht": "Kontodaten geaendert",
                          "kontoId": 100,
                          "iban": "DE12500105170648489890",
                          "inhaber": "Max Mustermann",
                          "aktion": "ERSTELLEN"
                        }
                        """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(11))
            .andExpect(jsonPath("$.typ").value("KONTO"))
            .andExpect(jsonPath("$.kontoId").value(100))
            .andExpect(jsonPath("$.aktion").value("ERSTELLEN"))
            .andExpect(jsonPath("$.buchungId").doesNotExist())
            .andExpect(jsonPath("$.transaktionId").doesNotExist());
    }

    @Test
    void postBuchung_should201_withBuchungResponse() throws Exception {
        BuchungBenachrichtigung gespeichert = new BuchungBenachrichtigung();
        gespeichert.setId(22L);
        gespeichert.setTyp(BenachrichtigungTyp.BUCHUNG);
        gespeichert.setTimestamp(LocalDateTime.of(2026, 7, 2, 10, 15));
        gespeichert.setBuchungId(200L);
        gespeichert.setBetrag(42.5);
        when(benachrichtigungService.receive(any(BuchungBenachrichtigungRequest.class))).thenReturn(gespeichert);

        mockMvc.perform(post("/api/benachrichtigungen/buchungen")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "nachricht": "Buchung erstellt",
                          "buchungId": 200,
                          "kontoId": 100,
                          "iban": "DE12500105170648489890",
                          "inhaber": "Max Mustermann",
                          "betrag": 42.5
                        }
                        """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(22))
            .andExpect(jsonPath("$.typ").value("BUCHUNG"))
            .andExpect(jsonPath("$.buchungId").value(200))
            .andExpect(jsonPath("$.betrag").value(42.5))
            .andExpect(jsonPath("$.transaktionId").doesNotExist())
            .andExpect(jsonPath("$.aktion").doesNotExist());
    }

    @Test
    void postTransaktion_should201_withTransaktionResponse() throws Exception {
        TransaktionBenachrichtigung gespeichert = new TransaktionBenachrichtigung();
        gespeichert.setId(33L);
        gespeichert.setTyp(BenachrichtigungTyp.TRANSAKTION);
        gespeichert.setTimestamp(LocalDateTime.of(2026, 7, 2, 10, 30));
        gespeichert.setTransaktionId(300L);
        gespeichert.setQuelleKontoId(100L);
        gespeichert.setZielKontoId(101L);
        gespeichert.setBetrag(99.99);
        when(benachrichtigungService.receive(any(TransaktionBenachrichtigungRequest.class))).thenReturn(gespeichert);

        mockMvc.perform(post("/api/benachrichtigungen/transaktionen")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
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
                        """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.id").value(33))
            .andExpect(jsonPath("$.typ").value("TRANSAKTION"))
            .andExpect(jsonPath("$.transaktionId").value(300))
            .andExpect(jsonPath("$.quelleKontoId").value(100))
            .andExpect(jsonPath("$.zielKontoId").value(101))
            .andExpect(jsonPath("$.betrag").value(99.99))
            .andExpect(jsonPath("$.aktion").doesNotExist())
            .andExpect(jsonPath("$.buchungId").doesNotExist());
    }

    @Test
    void postKonto_withInvalidPayload_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/benachrichtigungen/konten")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "nachricht": "Kontodaten geaendert",
                          "iban": "DE12500105170648489890",
                          "inhaber": "Max Mustermann",
                          "aktion": "ERSTELLEN"
                        }
                        """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.kontoId").exists());

        verifyNoInteractions(benachrichtigungService);
    }

    @Test
    void postBuchung_withInvalidPayload_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/benachrichtigungen/buchungen")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "nachricht": "Buchung erstellt",
                          "buchungId": 200,
                          "kontoId": 100,
                          "iban": "DE12500105170648489890",
                          "inhaber": "Max Mustermann"
                        }
                        """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.betrag").exists());

        verifyNoInteractions(benachrichtigungService);
    }

    @Test
    void postTransaktion_withInvalidPayload_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/benachrichtigungen/transaktionen")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "nachricht": "Transaktion ausgefuehrt",
                          "transaktionId": 300,
                          "quelleKontoId": 100,
                          "zielKontoId": 101,
                          "quelleIban": "DE12500105170648489890",
                          "zielIban": "DE12500105170648489891",
                          "quelleInhaber": "Max Mustermann",
                          "betrag": 99.99
                        }
                        """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.zielInhaber").exists());

        verifyNoInteractions(benachrichtigungService);
    }

    @Test
    @WithAnonymousUser
    void ohneAnmeldungGibt401() throws Exception {
        mockMvc.perform(get("/api/benachrichtigungen"))
            .andExpect(status().isUnauthorized());
    }
}
