package com.smxplore.proto.infrastructure.config;

import com.smxplore.proto.domain.model.user.UserRole;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.security.config.web.server.ServerHttpSecurity;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.ReactiveJwtAuthenticationConverter;
import org.springframework.security.web.server.SecurityWebFilterChain;
import org.springframework.security.web.server.ServerAuthenticationEntryPoint;
import org.springframework.security.web.server.authorization.ServerAccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsConfigurationSource;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Arrays;
import java.util.List;

/**
 * Single security chain for every profile: stateless JWT authentication with
 * role-based authorization. The endpoint-to-role matrix below follows the
 * use-case diagrams (destination managers manage the offer, guides manage
 * activities, providers manage services, tourists operate on their own
 * reservations, admins administer users and see everything).
 *
 * <p>Note: this chain enforces roles; resource ownership (owner/provider/guide/
 * staff scoping) is enforced in the web controllers. See the README
 * ("Security notes") for the details.
 */
@Configuration(proxyBeanMethods = false)
public class SecurityConfiguration {

    @Bean
    public SecurityWebFilterChain securityWebFilterChain(ServerHttpSecurity http,
                                                         CorsConfigurationSource corsConfigurationSource,
                                                         ReactiveJwtAuthenticationConverter jwtAuthenticationConverter,
                                                         ServerAuthenticationEntryPoint apiAuthenticationEntryPoint,
                                                         ServerAccessDeniedHandler apiAccessDeniedHandler) {
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
                        // Public catalog (no session): listings, details and public profiles.
                        // Profile contact data (email/phone) is only returned to authenticated callers (controller-level).
                        .pathMatchers(HttpMethod.GET, "/api/v1/events", "/api/v1/events/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/activities", "/api/v1/activities/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/services", "/api/v1/services/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/zones", "/api/v1/zones/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/sites", "/api/v1/sites/**").permitAll()
                        .pathMatchers(HttpMethod.GET, "/api/v1/users/guides/*", "/api/v1/users/providers/*").permitAll()
                        // Events: managed by destination managers
                        .pathMatchers(HttpMethod.POST, "/api/v1/events")
                        .hasAnyRole(UserRole.DESTINATION_MANAGER.name(), UserRole.ADMIN.name())
                        .pathMatchers(HttpMethod.PATCH, "/api/v1/events/*/status", "/api/v1/events/*/attendance")
                        .hasAnyRole(UserRole.DESTINATION_MANAGER.name(), UserRole.ADMIN.name())
                        .pathMatchers(HttpMethod.PUT, "/api/v1/events/*")
                        .hasAnyRole(UserRole.DESTINATION_MANAGER.name(), UserRole.ADMIN.name())
                        // Sites & zones: reads are public (catalog above); management is manager/admin
                        .pathMatchers(HttpMethod.POST, "/api/v1/zones", "/api/v1/sites")
                        .hasAnyRole(UserRole.DESTINATION_MANAGER.name(), UserRole.ADMIN.name())
                        .pathMatchers(HttpMethod.PATCH, "/api/v1/zones/*", "/api/v1/sites/*")
                        .hasAnyRole(UserRole.DESTINATION_MANAGER.name(), UserRole.ADMIN.name())
                        // Activities: managed by destination managers and guides
                        .pathMatchers(HttpMethod.POST, "/api/v1/activities", "/api/v1/activities/*/cancel")
                        .hasAnyRole(UserRole.DESTINATION_MANAGER.name(), UserRole.GUIDE.name(), UserRole.ADMIN.name())
                        .pathMatchers(HttpMethod.PATCH, "/api/v1/activities/*", "/api/v1/activities/*/availability",
                                "/api/v1/activities/*/slots", "/api/v1/activities/*/status")
                        .hasAnyRole(UserRole.DESTINATION_MANAGER.name(), UserRole.GUIDE.name(), UserRole.ADMIN.name())
                        // Services: managed by their providers
                        .pathMatchers(HttpMethod.POST, "/api/v1/services", "/api/v1/services/*/photos",
                                "/api/v1/services/*/attributes")
                        .hasAnyRole(UserRole.SERVICE_PROVIDER.name(), UserRole.ADMIN.name())
                        .pathMatchers(HttpMethod.PATCH, "/api/v1/services/*/price")
                        .hasAnyRole(UserRole.SERVICE_PROVIDER.name(), UserRole.ADMIN.name())
                        .pathMatchers(HttpMethod.PUT, "/api/v1/services/*/available", "/api/v1/services/*/unavailable")
                        .hasAnyRole(UserRole.SERVICE_PROVIDER.name(), UserRole.ADMIN.name())
                        // Security reports: everyone reports/comments; listing all and closing is admin/manager
                        .pathMatchers(HttpMethod.GET, "/api/v1/security-reports")
                        .hasAnyRole(UserRole.ADMIN.name(), UserRole.DESTINATION_MANAGER.name())
                        .pathMatchers(HttpMethod.HEAD, "/api/v1/security-reports")
                        .hasAnyRole(UserRole.ADMIN.name(), UserRole.DESTINATION_MANAGER.name())
                        .pathMatchers(HttpMethod.POST, "/api/v1/security-reports/*/close",
                                "/api/v1/security-reports/*/solve")
                        .hasAnyRole(UserRole.ADMIN.name(), UserRole.DESTINATION_MANAGER.name())
                        // Reservations: tourists create and cancel; providers/guides/managers operate them
                        .pathMatchers(HttpMethod.POST, "/api/v1/reservations")
                        .hasAnyRole(UserRole.TOURIST.name(), UserRole.ADMIN.name())
                        .pathMatchers(HttpMethod.POST, "/api/v1/reservations/*/items",
                                "/api/v1/reservations/*/items/batch")
                        .hasAnyRole(UserRole.TOURIST.name(), UserRole.ADMIN.name(),
                                UserRole.DESTINATION_MANAGER.name())
                        .pathMatchers(HttpMethod.GET, "/api/v1/reservations")
                        .hasAnyRole(UserRole.ADMIN.name(), UserRole.DESTINATION_MANAGER.name())
                        .pathMatchers(HttpMethod.HEAD, "/api/v1/reservations")
                        .hasAnyRole(UserRole.ADMIN.name(), UserRole.DESTINATION_MANAGER.name())
                        .pathMatchers(HttpMethod.GET, "/api/v1/reservation-items")
                        .hasAnyRole(UserRole.ADMIN.name(), UserRole.DESTINATION_MANAGER.name())
                        .pathMatchers(HttpMethod.HEAD, "/api/v1/reservation-items")
                        .hasAnyRole(UserRole.ADMIN.name(), UserRole.DESTINATION_MANAGER.name())
                        .pathMatchers(HttpMethod.GET, "/api/v1/reservations/providers/*",
                                "/api/v1/reservations/guides/*", "/api/v1/reservations/by-item")
                        .hasAnyRole(UserRole.SERVICE_PROVIDER.name(), UserRole.GUIDE.name(), UserRole.ADMIN.name(),
                                UserRole.DESTINATION_MANAGER.name())
                        .pathMatchers(HttpMethod.PATCH, "/api/v1/reservations/*/cancel",
                                "/api/v1/reservations/*/request-cancellation")
                        .hasAnyRole(UserRole.TOURIST.name(), UserRole.ADMIN.name())
                        .pathMatchers(HttpMethod.PATCH, "/api/v1/reservations/*/confirm",
                                "/api/v1/reservations/*/reject", "/api/v1/reservations/*/attendance",
                                "/api/v1/reservations/*/no-show")
                        .hasAnyRole(UserRole.SERVICE_PROVIDER.name(), UserRole.GUIDE.name(),
                                UserRole.DESTINATION_MANAGER.name(), UserRole.ADMIN.name())
                        // User administration: admins only — own profile/account endpoints are guarded in the controller
                        .pathMatchers(HttpMethod.GET, "/api/v1/users", "/api/v1/users/search")
                        .hasRole(UserRole.ADMIN.name())
                        .pathMatchers(HttpMethod.HEAD, "/api/v1/users", "/api/v1/users/search")
                        .hasRole(UserRole.ADMIN.name())
                        .pathMatchers(HttpMethod.PATCH, "/api/v1/users/*/role", "/api/v1/users/*/status")
                        .hasRole(UserRole.ADMIN.name())
                        .pathMatchers(HttpMethod.POST, "/api/v1/users/*/reset-password",
                                "/api/v1/users/*/approve-provider", "/api/v1/users/*/approve-guide",
                                "/api/v1/users/*/approve-manager", "/api/v1/users/*/reject-provider")
                        .hasRole(UserRole.ADMIN.name())
                        .pathMatchers(HttpMethod.DELETE, "/api/v1/users/*/admin")
                        .hasRole(UserRole.ADMIN.name())
                        // Everything else: a valid JWT is enough
                        .anyExchange().authenticated())
                .exceptionHandling(exceptions -> exceptions
                        .authenticationEntryPoint(apiAuthenticationEntryPoint)
                        .accessDeniedHandler(apiAccessDeniedHandler))
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter)))
                .build();
    }

    @Bean
    public ReactiveJwtAuthenticationConverter jwtAuthenticationConverter() {
        var converter = new ReactiveJwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(jwt -> {
            var role = jwt.getClaimAsString(JwtConfiguration.ROLE_CLAIM);
            return role == null
                    ? Flux.empty()
                    : Flux.just(new SimpleGrantedAuthority("ROLE_" + role));
        });
        return converter;
    }

    @Bean
    public ServerAuthenticationEntryPoint apiAuthenticationEntryPoint() {
        return (exchange, exception) -> {
            exchange.getResponse().getHeaders().set(HttpHeaders.WWW_AUTHENTICATE, "Bearer");
            return writeError(exchange, HttpStatus.UNAUTHORIZED, "Authentication required or token invalid.");
        };
    }

    @Bean
    public ServerAccessDeniedHandler apiAccessDeniedHandler() {
        return (exchange, exception) -> writeError(exchange, HttpStatus.FORBIDDEN,
                "Insufficient role for this operation.");
    }

    @Bean
    public CorsConfigurationSource corsConfigurationSource(
            @Value("${app.security.cors.allowed-origins}") String allowedOrigins) {
        var configuration = new CorsConfiguration();
        configuration.setAllowedOriginPatterns(Arrays.stream(allowedOrigins.split(","))
                .map(String::trim)
                .filter(origin -> !origin.isBlank())
                .toList());
        configuration.setAllowedMethods(List.of("GET", "POST", "PUT", "PATCH", "DELETE", "OPTIONS"));
        configuration.setAllowedHeaders(List.of("Authorization", "Content-Type"));
        var source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return source;
    }

    private static Mono<Void> writeError(ServerWebExchange exchange, HttpStatus status, String message) {
        var response = exchange.getResponse();
        response.setStatusCode(status);
        response.getHeaders().setContentType(MediaType.APPLICATION_JSON);
        // Same JSON shape as ApiErrorResponse (keep in sync) — built manually so the
        // filter chain does not depend on any JSON mapper bean.
        var json = "{\"timestamp\":\"%s\",\"status\":%d,\"error\":\"%s\",\"message\":\"%s\",\"path\":\"%s\"}"
                .formatted(Instant.now(), status.value(), status.getReasonPhrase(), message,
                        exchange.getRequest().getPath().value());
        var buffer = response.bufferFactory().wrap(json.getBytes(StandardCharsets.UTF_8));
        return response.writeWith(Mono.just(buffer));
    }
}
