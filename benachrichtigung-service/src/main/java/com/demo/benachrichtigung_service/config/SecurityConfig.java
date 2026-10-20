package com.demo.benachrichtigung_service.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.core.annotation.Order;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.factory.PasswordEncoderFactories;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.www.BasicAuthenticationFilter;

import com.demo.benachrichtigung_service.security.FailedLoginRateLimitFilter;
import com.demo.benachrichtigung_service.security.LoginRateLimitProperties;

@Configuration
@EnableWebSecurity
@EnableConfigurationProperties(LoginRateLimitProperties.class)
public class SecurityConfig {

    // Nur lokal: H2-Konsole ohne Login erreichbar, Frames vom selben Origin erlaubt.
    @Bean
    @Profile("dev")
    @Order(1)
    SecurityFilterChain h2ConsoleFilterChain(HttpSecurity http) throws Exception {
        http
            .securityMatcher("/h2-console/**")
            .csrf(csrf -> csrf.disable())
            .headers(headers -> headers.frameOptions(frame -> frame.sameOrigin()))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/h2-console/**").permitAll());
        return http.build();
    }

    @Bean
    @Order(2)
    SecurityFilterChain securityFilterChain(HttpSecurity http, LoginRateLimitProperties limits) throws Exception {
        http
            .cors(Customizer.withDefaults())
            .csrf(csrf -> csrf.disable())
            .sessionManagement(session -> session
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .authorizeHttpRequests(auth -> auth
                .requestMatchers("/actuator/health", "/actuator/health/**").permitAll()
                .requestMatchers("/error").permitAll()
                .requestMatchers("/ws", "/ws/**").permitAll()
                .anyRequest().authenticated())
            .httpBasic(basic -> basic.authenticationEntryPoint(
                (request, response, ex) -> response.setStatus(401)))
            .addFilterBefore(new FailedLoginRateLimitFilter(limits.maxFailures(), limits.window()),
                BasicAuthenticationFilter.class);
        return http.build();
    }

    @Bean
    UserDetailsService userDetailsService(PasswordEncoder encoder,
        @Value("${demo.auth.username}") String username,
        @Value("${demo.auth.password}") String password) {
            return new InMemoryUserDetailsManager(
                User.withUsername(username)
                    .password(encoder.encode(password))
                    .roles("DEMO")
                    .build()
            );
    }

    @Bean
    PasswordEncoder passwordEncoder() {
        return PasswordEncoderFactories.createDelegatingPasswordEncoder();
    }
}
