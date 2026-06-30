package com.demo.kontoservice.konto;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.doThrow;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(KontoController.class)
class KontoControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private KontoService kontoService;

    // ── GET /api/konten ──────────────────────────────────────────
    @Test
    void getAllKonten_should200_withList() throws Exception {
        Konto konto = new Konto();
        konto.setId(1L);
        konto.setKontostand(new BigDecimal("100.00"));
        when(kontoService.getAll()).thenReturn(List.of(konto));

        mockMvc.perform(get("/api/konten"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1))
                .andExpect(jsonPath("$[0].kontostand").value(100.00));
    }

    // ── POST /api/konten ─────────────────────────────────────────
    @Test
    void createKonto_should201_withSavedKonto() throws Exception {
        Konto konto = new Konto();
        konto.setKontostand(new BigDecimal("500.00"));
        when(kontoService.create(any())).thenReturn(konto);

        KontoRequest kontoRequest = new KontoRequest();
        kontoRequest.setInhaber("Max Mustermann");
        kontoRequest.setIban("DE12345678901234567890");
        kontoRequest.setKontostand(new BigDecimal("500.00"));

        mockMvc.perform(post("/api/konten")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(kontoRequest)))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.kontostand").value(500.00));
    }

    // ── DELETE /api/konten/{id} ──────────────────────────────────
    @Test
    void deleteKonto_should204() throws Exception {
        doNothing().when(kontoService).delete(1L);

        mockMvc.perform(delete("/api/konten/1"))
                .andExpect(status().isNoContent());
    }

    @Test
    void deleteKonto_should404_ifNotFound() throws Exception {
        doThrow(new RuntimeException("Konto nicht gefunden"))
                .when(kontoService).delete(99L);

        mockMvc.perform(delete("/api/konten/99"))
                .andExpect(status().isNotFound());
    }
}