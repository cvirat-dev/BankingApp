package com.demo.benachrichtigung_service;

import java.time.LocalDateTime;

import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.MediaType;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.test.context.junit.jupiter.SpringExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.validation.beanvalidation.LocalValidatorFactoryBean;

import com.demo.benachrichtigung_service.benachrichtigung.Benachrichtigung;
import com.demo.benachrichtigung_service.benachrichtigung.BenachrichtigungController;
import com.demo.benachrichtigung_service.benachrichtigung.BenachrichtigungRepository;
import com.demo.benachrichtigung_service.benachrichtigung.BenachrichtigungService;
import com.demo.benachrichtigung_service.benachrichtigung.BuchungBenachrichtigung;
import com.demo.benachrichtigung_service.benachrichtigung.KontoBenachrichtigung;
import com.demo.benachrichtigung_service.benachrichtigung.TransaktionBenachrichtigung;
import com.demo.benachrichtigung_service.exception.GlobalExceptionHandler;

@ExtendWith({SpringExtension.class, MockitoExtension.class})
class BenachrichtigungControllerTest {

    private MockMvc mockMvc;

    @Mock
    private BenachrichtigungRepository benachrichtigungRepository;

    @Mock
    private SimpMessagingTemplate messagingTemplate;

    @BeforeEach
    void setUp() {
        BenachrichtigungService benachrichtigungService = new BenachrichtigungService();
        ReflectionTestUtils.setField(benachrichtigungService, "repository", benachrichtigungRepository);
        ReflectionTestUtils.setField(benachrichtigungService, "messagingTemplate", messagingTemplate);

        BenachrichtigungController controller = new BenachrichtigungController();
        ReflectionTestUtils.setField(controller, "benachrichtigungService", benachrichtigungService);

        LocalValidatorFactoryBean validator = new LocalValidatorFactoryBean();
        validator.afterPropertiesSet();

        mockMvc = MockMvcBuilders.standaloneSetup(controller)
                .setControllerAdvice(new GlobalExceptionHandler())
                .setValidator(validator)
                .build();
    }

    @Test
    void postKonto_shouldForceTypAndReturnKontoResponse() throws Exception {
        when(benachrichtigungRepository.save(any(Benachrichtigung.class))).thenAnswer(invocation -> {
            Benachrichtigung saved = invocation.getArgument(0);
            saved.setId(11L);
            saved.setTimestamp(LocalDateTime.of(2026, 7, 2, 10, 0));
            return saved;
        });

        mockMvc.perform(post("/api/benachrichtigungen/konten")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "typ": "KONTO",
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

        ArgumentCaptor<Benachrichtigung> captor = ArgumentCaptor.forClass(Benachrichtigung.class);
        verify(benachrichtigungRepository).save(captor.capture());
        assertTrue(captor.getValue() instanceof KontoBenachrichtigung);
    }

    @Test
    void postBuchung_shouldForceTypAndReturnBuchungResponse() throws Exception {
        when(benachrichtigungRepository.save(any(Benachrichtigung.class))).thenAnswer(invocation -> {
            Benachrichtigung saved = invocation.getArgument(0);
            saved.setId(22L);
            saved.setTimestamp(LocalDateTime.of(2026, 7, 2, 10, 15));
            return saved;
        });

        mockMvc.perform(post("/api/benachrichtigungen/buchungen")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "typ": "BUCHUNG",
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

        ArgumentCaptor<Benachrichtigung> captor = ArgumentCaptor.forClass(Benachrichtigung.class);
        verify(benachrichtigungRepository).save(captor.capture());
        assertTrue(captor.getValue() instanceof BuchungBenachrichtigung);
    }

    @Test
    void postTransaktion_shouldForceTypAndReturnTransaktionResponse() throws Exception {
        when(benachrichtigungRepository.save(any(Benachrichtigung.class))).thenAnswer(invocation -> {
            Benachrichtigung saved = invocation.getArgument(0);
            saved.setId(33L);
            saved.setTimestamp(LocalDateTime.of(2026, 7, 2, 10, 30));
            return saved;
        });

        mockMvc.perform(post("/api/benachrichtigungen/transaktionen")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "typ": "TRANSAKTION",
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

        ArgumentCaptor<Benachrichtigung> captor = ArgumentCaptor.forClass(Benachrichtigung.class);
        verify(benachrichtigungRepository).save(captor.capture());
        assertTrue(captor.getValue() instanceof TransaktionBenachrichtigung);
    }

    @Test
    void postKonto_withInvalidPayload_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/benachrichtigungen/konten")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "typ": "KONTO",
                          "nachricht": "Kontodaten geaendert",
                          "iban": "DE12500105170648489890",
                          "inhaber": "Max Mustermann",
                          "aktion": "ERSTELLEN"
                        }
                        """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.kontoId").exists());
    }

    @Test
    void postBuchung_withInvalidPayload_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/benachrichtigungen/buchungen")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "typ": "BUCHUNG",
                          "nachricht": "Buchung erstellt",
                          "buchungId": 200,
                          "kontoId": 100,
                          "iban": "DE12500105170648489890",
                          "inhaber": "Max Mustermann"
                        }
                        """))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath("$.betrag").exists());
    }

    @Test
    void postTransaktion_withInvalidPayload_shouldReturnBadRequest() throws Exception {
        mockMvc.perform(post("/api/benachrichtigungen/transaktionen")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                        {
                          "typ": "TRANSAKTION",
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
    }
}
