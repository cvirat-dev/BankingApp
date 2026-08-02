package com.demo.kontoservice.transaktion;

import java.math.BigDecimal;
import java.util.List;

import static org.hamcrest.Matchers.closeTo;
import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.fasterxml.jackson.databind.ObjectMapper;

@WebMvcTest(TransaktionController.class)
class TransaktionControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @MockitoBean
    private TransaktionService transaktionService;

    @Test
    void getAll_shouldReturnTransaktionen() throws Exception {
        Transaktion transaktion = new Transaktion();
        transaktion.setId(1L);
        transaktion.setQuelleKontoId(10L);
        transaktion.setZielKontoId(20L);
        transaktion.setBetrag(new BigDecimal("15.00"));
        when(transaktionService.getAll()).thenReturn(List.of(transaktion));

        mockMvc.perform(get("/api/transaktionen"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].id").value(1L))
                .andExpect(jsonPath("$[0].quelleKontoId").value(10L))
                .andExpect(jsonPath("$[0].zielKontoId").value(20L))
            .andExpect(jsonPath("$[0].betrag").value(closeTo(15.00, 0.0001)));
    }

    @Test
    void get_shouldReturnTransaktionenForKonto() throws Exception {
        Transaktion transaktion = new Transaktion();
        transaktion.setQuelleKontoId(10L);
        transaktion.setZielKontoId(20L);
        when(transaktionService.getByKontoId(10L)).thenReturn(List.of(transaktion));

        mockMvc.perform(get("/api/transaktionen/konto/10"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].quelleKontoId").value(10L))
                .andExpect(jsonPath("$[0].zielKontoId").value(20L));
    }

    @Test
    void getById_shouldReturnTransaktion() throws Exception {
        Transaktion transaktion = new Transaktion();
        transaktion.setId(5L);
        transaktion.setQuelleKontoId(10L);
        transaktion.setZielKontoId(20L);
        when(transaktionService.get(5L)).thenReturn(transaktion);

        mockMvc.perform(get("/api/transaktionen/5"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.quelleKontoId").value(10L))
                .andExpect(jsonPath("$.zielKontoId").value(20L));
    }

    @Test
    void create_shouldReturnCreatedTransaktion() throws Exception {
        TransaktionRequest eingabe = new TransaktionRequest();
        eingabe.setQuelleKontoId(10L);
        eingabe.setZielKontoId(20L);
        eingabe.setBetrag(new BigDecimal("15.00"));
        eingabe.setBeschreibung("Test");

        Transaktion gespeichert = new Transaktion();
        gespeichert.setId(5L);
        gespeichert.setQuelleKontoId(10L);
        gespeichert.setZielKontoId(20L);
        gespeichert.setBetrag(new BigDecimal("15.00"));
        gespeichert.setBeschreibung("Test");

        when(transaktionService.create(any(TransaktionRequest.class))).thenReturn(gespeichert);

        mockMvc.perform(post("/api/transaktionen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eingabe)))
            .andExpect(status().isCreated())
                .andExpect(jsonPath("$.id").value(5L))
                .andExpect(jsonPath("$.quelleKontoId").value(10L))
                .andExpect(jsonPath("$.zielKontoId").value(20L))
                .andExpect(jsonPath("$.betrag").value(closeTo(15.00, 0.0001)))
                .andExpect(jsonPath("$.beschreibung").value("Test"));
    }

    @Test
    void create_shouldReturnBadRequest_whenQuelleAndZielkontoAreEqual() throws Exception {
        TransaktionRequest eingabe = new TransaktionRequest();
        eingabe.setQuelleKontoId(10L);
        eingabe.setZielKontoId(10L);
        eingabe.setBetrag(new BigDecimal("15.00"));
        eingabe.setBeschreibung("Test");

        mockMvc.perform(post("/api/transaktionen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(eingabe)))
                .andExpect(status().isBadRequest());

        verifyNoInteractions(transaktionService);
    }
}
