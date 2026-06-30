package com.demo.kontoservice.buchung;

import java.math.BigDecimal;
import java.util.List;

import org.junit.jupiter.api.Test;
import static org.mockito.ArgumentMatchers.any;
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

@WebMvcTest(BuchungController.class)
class BuchungControllerTest {

    @MockitoBean
    private BuchungService buchungService;

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private ObjectMapper objectMapper;

    @Test
    void createBuchung_should200_withBuchung() throws Exception {
        BuchungRequest buchungRequest = new BuchungRequest();
        buchungRequest.setKontoId(1L);
        buchungRequest.setBetrag(new BigDecimal("75.00"));
        buchungRequest.setBeschreibung("Miete");

        Buchung result = new Buchung();
        result.setKontoId(1L);
        result.setBetrag(new BigDecimal("75.00"));

        when(buchungService.create(any(BuchungRequest.class), org.mockito.ArgumentMatchers.eq(true))).thenReturn(result);

        mockMvc.perform(post("/api/buchungen")
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectMapper.writeValueAsString(buchungRequest)))
            .andExpect(status().isCreated())
                .andExpect(jsonPath("$.kontoId").value(1L))
                .andExpect(jsonPath("$.betrag").value(75.00));
    }

    @Test
    void getBuchungen_should200_withListe() throws Exception {
        Buchung buchung = new Buchung();
        buchung.setKontoId(1L);
        when(buchungService.getBuchungen(1L)).thenReturn(List.of(buchung));

        mockMvc.perform(get("/api/buchungen/konto/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].kontoId").value(1));
    }
}
