package com.mockly.user.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static org.springframework.security.config.Customizer.withDefaults;

@Configuration
@EnableWebSecurity
public class KeycloakSecurityConfig {

    private List<GrantedAuthority> mapAuthorities(final Map<String, Object> attributes) {
        @SuppressWarnings("unchecked")
        final Map<String, Object> realmAccess = ((Map<String, Object>) attributes.getOrDefault("realm_access",
                Collections.emptyMap()));
        @SuppressWarnings("unchecked")
        final Collection<String> roles = ((Collection<String>) realmAccess.getOrDefault("roles",
                Collections.emptyList()));
        return roles.stream()
                .map(role -> ((GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + role)))
                .toList();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        final JwtAuthenticationConverter converter = new JwtAuthenticationConverter();
        converter.setJwtGrantedAuthoritiesConverter(source -> mapAuthorities(source.getClaims()));
        return converter;
    }

    @Bean
    public SecurityFilterChain oauthFilterChain(final HttpSecurity http) throws Exception {
        return http.cors(withDefaults())
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(authorize -> authorize
                        .requestMatchers("/actuator/health/**", "/v3/api-docs/**", "/swagger-ui/**").permitAll()
                        .requestMatchers("/api/v1/users/admin/**").hasRole("ADMIN")
                        .requestMatchers("/api/v1/users/**").authenticated()
                        .anyRequest().authenticated())
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(
                        jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter())))
                .build();
    }
}
