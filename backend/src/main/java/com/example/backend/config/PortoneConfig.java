package com.example.backend.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

@Configuration
public class PortoneConfig {
    @Value("${portone.secret}")
    private String portoneSecret;

    public String getPortoneSecret() {
        return portoneSecret;
    }
}
