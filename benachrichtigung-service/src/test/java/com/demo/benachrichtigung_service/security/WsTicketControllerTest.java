package com.demo.benachrichtigung_service.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithAnonymousUser;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.demo.benachrichtigung_service.config.SecurityConfig;

@WebMvcTest(WsTicketController.class)
@Import({SecurityConfig.class, WsTicketService.class})
@TestPropertySource(properties = "demo.auth.password=test")
@WithMockUser
class WsTicketControllerTest {

    @Autowired
    private MockMvc mockMvc;

    @Test
    void getTicket_should200_withTicket() throws Exception {
        mockMvc.perform(get("/api/ws-ticket"))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.ticket").isNotEmpty());
    }

    @Test
    @WithAnonymousUser
    void ohneAnmeldungGibt401() throws Exception {
        mockMvc.perform(get("/api/ws-ticket"))
            .andExpect(status().isUnauthorized());
    }
}
