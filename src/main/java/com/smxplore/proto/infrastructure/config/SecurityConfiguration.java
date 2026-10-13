package com.smxplore.proto.infrastructure.config;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import reactor.core.publisher.Flux;

import java.util.List;

/**
 * Single security chain for every profile: stateless JWT authentication with
 * role-based authorization. The endpoint-to-role matrix below follows the
 * use-case diagrams (destination managers manage the offer, guides manage
 * activities, providers manage services, tourists operate on their own
 * reservations, admins administer users and see everything).
 */
@Configuration(proxyBeanMethods = false)
public class SecurityConfiguration {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         CorsConfigurationSource corsConfigurationSource,
                                                         ReactiveJwtAuthenticationConverter jwtAuthenticationConverter) {
        return http
                .csrf(ServerHttpSecurity.CsrfSpec::disable)
                .httpBasic(ServerHttpSecurity.HttpBasicSpec::disable)
                .formLogin(ServerHttpSecurity.FormLoginSpec::disable)
                .cors(cors -> cors.configurationSource(corsConfigurationSource))
                .authorizeExchange(exchanges -> exchanges
                        // Public: CORS preflight, authentication, API docs and health
                        .pathMatchers(HttpMethod.OPTIONS, "/**").permitAll()
                        .pathMatchers("/api/v1/auth/register", "/api/v1/auth/login",
                                "/api/v1/auth/recover-password").permitAll()
                        .pathMatchers("/actuator/health", "/actuator/health/**").permitAll()
                        .pathMatchers("/v3/api-docs/**", "/swagger-ui.html", "/swagger-ui/**", "/webjars/**")
                        .permitAll()
                        // Events: managed by destination managers
                        .pathMatchers(HttpMethod.POST, "/api/v1/events")
                        .hasAnyRole("DESTINATION_MANAGER", "ADMIN")
                        .pathMatchers(HttpMethod.PATCH, "/api/v1/events/*/status", "/api/v1/events/*/attendance")
                        .hasAnyRole("DESTINATION_MANAGER", "ADMIN")
                        // Activities: managed by destination managers and guides
                        .pathMatchers(HttpMethod.POST, "/api/v1/activities", "/api/v1/activities/*/cancel")
                        .hasAnyRole("DESTINATION_MANAGER", "GUIDE", "ADMIN")
                        .pathMatchers(HttpMethod.PATCH, "/api/v1/activities/*", "/api/v1/activities/*/availability",
                                "/api/v1/activities/*/slots", "/api/v1/activities/*/status")
                        .hasAnyRole("DESTINATION_MANAGER", "GUIDE", "ADMIN")
                        // Services: managed by their providers
                        .pathMatchers(HttpMethod.POST, "/api/v1/services", "/api/v1/services/*/photos",
                                "/api/v1/services/*/attributes")
                        .hasAnyRole("SERVICE_PROVIDER", "ADMIN")
                        .pathMatchers(HttpMethod.PATCH, "/api/v1/services/*/price")
                        .hasAnyRole("SERVICE_PROVIDER", "ADMIN")
                        .pathMatchers(HttpMethod.PUT, "/api/v1/services/*/available", "/api/v1/services/*/unavailable")
                        .hasAnyRole("SERVICE_PROVIDER", "ADMIN")
                        // Security reports: everyone can report; only admins/managers browse and close
                        .pathMatchers(HttpMethod.GET, "/api/v1/security-reports")
                        .hasAnyRole("ADMIN", "DESTINATION_MANAGER")
                        .pathMatchers(HttpMethod.POST, "/api/v1/security-reports/*/close")
                        .hasAnyRole("ADMIN", "DESTINATION_MANAGER")
                        // Reservations: tourists create and cancel; providers/managers operate them
                        .pathMatchers(HttpMethod.POST, "/api/v1/reservations", "/api/v1/reservations/*/items",
                                "/api/v1/reservations/*/items/batch")
                        .hasAnyRole("TOURIST", "ADMIN")
                        .pathMatchers(HttpMethod.PATCH, "/api/v1/reservations/*/cancel",
                                "/api/v1/reservations/*/request-cancellation")
                        .hasAnyRole("TOURIST", "ADMIN")
                        .pathMatchers(HttpMethod.PATCH, "/api/v1/reservations/*/confirm",
                                "/api/v1/reservations/*/reject", "/api/v1/reservations/*/attendance",
                                "/api/v1/reservations/*/no-show")
                        .hasAnyRole("SERVICE_PROVIDER", "DESTINATION_MANAGER", "ADMIN")
                        // User administration: admins only
                        .pathMatchers(HttpMethod.GET, "/api/v1/users", "/api/v1/users/search").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.PATCH, "/api/v1/users/*/role", "/api/v1/users/*/status")
                        .hasRole("ADMIN")
                        .pathMatchers(HttpMethod.POST, "/api/v1/users/*/reset-password").hasRole("ADMIN")
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/users/**").hasRole("ADMIN")
                        // Everything else: a valid JWT is enough
                        .anyExchange().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
                .build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
        var converter = new ReactiveJwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            var role = jwt.getClaimAsString("role");
            return role == null
                    ? Flux.empty()
                    : Flux.just(new SimpleGrantedAuthority("ROLE_" + role));
        });
        return converter;
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.security.cors.allowed-origins}") String allowedOrigins) {
        var configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(List.of(allowedOrigins.split(",")));
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }
}
