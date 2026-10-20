package com.demo.kontoservice.security;

import java.time.Duration;

import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.bind.DefaultValue;

@ConfigurationProperties(prefix = "security.login-rate-limit")
public record LoginRateLimitProperties(
        @DefaultValue("20") int maxFailures,
        @DefaultValue("1m") Duration window) {
}
