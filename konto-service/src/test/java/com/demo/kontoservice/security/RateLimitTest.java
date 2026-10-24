package com.demo.kontoservice.security;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.webmvc.test.autoconfigure.WebMvcTest;
import org.springframework.context.annotation.Import;
import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.httpBasic;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import com.demo.kontoservice.config.SecurityConfig;
import com.demo.kontoservice.konto.KontoController;
import com.demo.kontoservice.konto.KontoService;

@WebMvcTest(KontoController.class)
@Import(SecurityConfig.class)
@TestPropertySource(properties = {
    "demo.auth.username=test",
    "demo.auth.password=test",
    "security.login-rate-limit.max-failures=2"
})
class RateLimitTest {

    @Autowired
    private MockMvc mockMvc;

    @MockitoBean
    private KontoService kontoService;

    @Test
    void returns429after2AuthFailures() throws Exception {
        mockMvc.perform(get("/api/konten").with(httpBasic("test", "falsch")))
            .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/konten").with(httpBasic("test", "falsch")))
            .andExpect(status().isUnauthorized());
        mockMvc.perform(get("/api/konten").with(httpBasic("test", "falsch")))
            .andExpect(status().isTooManyRequests());
    }
}
