package com.mockly.user.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "keycloak")
public class KeycloakClientProperties {
    private String url;
    private String realm;
    private String clientId;
    private String clientSecret;
}
