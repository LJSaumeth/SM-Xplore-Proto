package com.smxplore.proto.infrastructure.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.web.server.SecurityWebFilterChain;

/**
 * Configuración temporal, únicamente para el perfil "docker" (docker compose).
 * La seguridad definitiva se configurará más adelante; mientras tanto esta
 * cadena deja la API abierta para poder probar desde Swagger o un cliente
 * frontend sin credenciales. Al definir una cadena propia, la cadena por
 * defecto de Spring Boot (Basic Auth con usuario generado) queda desactivada.
 */
@Configuration(proxyBeanMethods = false)
@Profile("docker")
public class DockerSecurityConfiguration {

    @Bean
    public SecurityWebFilterChain localTestSecurityWebFilterChain(ServerHttpSecurity http) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .authorizeExchange(exchanges -> exchanges.anyExchange().permitAll())
                .build();
    }
}
