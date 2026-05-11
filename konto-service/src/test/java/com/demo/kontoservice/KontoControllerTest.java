package com.demo.kontoservice;

import com.demo.kontoservice.konto.*;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import java.math.BigDecimal;
import java.util.List;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@WebMvcTest(KontoController.class)
class KontoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private KontoService kontoService;

    @MockitoBean
    private KontoRepository kontoRepository;

    // ── GET /api/konten ──────────────────────────────────────────
    @Test
    void getAllKonten_should200_withList() throws Exception {
        Konto konto = new Konto();
        konto.setId(1L);
        konto.setKontostand(new BigDecimal("100.00"));
        when(kontoRepository.findAll()).thenReturn(List.of(konto));

        mockMvc.perform(get("/api/konten"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].kontostand").value(100.00));
    }

    // ── POST /api/konten ─────────────────────────────────────────
    @Test
    void createKonto_should200_withSavedKonto() throws Exception {
        Konto konto = new Konto();
        konto.setKontostand(new BigDecimal("500.00"));
        when(kontoService.createKonto(any())).thenReturn(konto);

        mockMvc.perform(post("/api/konten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(konto)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kontostand").value(500.00));
    }

    // ── POST /api/konten/{id}/buchung ────────────────────────────
    @Test
    void createBuchung_should200_withTransaktion() throws Exception {
        Transaktion transaktion = new Transaktion();
        transaktion.setBetrag(new BigDecimal("75.00"));
        transaktion.setBeschreibung("Miete");

        Transaktion result = new Transaktion();
        result.setKontoId(1L);
        result.setBetrag(new BigDecimal("75.00"));

        when(kontoService.buchung(1L, new BigDecimal("75.00"), "Miete"))
                .thenReturn(result);

        mockMvc.perform(post("/api/konten/1/buchung")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(transaktion)))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.kontoId").value(1))
                .andExpect(jsonPath("$.betrag").value(75.00));
    }

    // ── GET /api/konten/{id}/transaktionen ───────────────────────
    @Test
    void getTransaktionen_should200_withListe() throws Exception {
        Transaktion t = new Transaktion();
        t.setKontoId(1L);
        when(kontoService.getTransaktionen(1L)).thenReturn(List.of(t));

        mockMvc.perform(get("/api/konten/1/transaktionen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].kontoId").value(1));
    }

    // ── DELETE /api/konten/{id} ──────────────────────────────────
    @Test
    void deleteKonto_should200() throws Exception {
        doNothing().when(kontoService).deleteById(1L);

        mockMvc.perform(delete("/api/konten/1"))
                .andExpect(status().isOk());
    }

    @Test
    void deleteKonto_should404_ifNotFound() throws Exception {
        doThrow(new RuntimeException("Konto nicht gefunden"))
                .when(kontoService).deleteById(99L);

        mockMvc.perform(delete("/api/konten/99"))
                .andExpect(status().isNotFound());
    }
}