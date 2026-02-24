package com.mockly.user.dto;

import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Getter
@Setter
@Component
@ConfigurationProperties(prefix = "app.audit")
public class AuditProperties {

    private String exchange = "audit.exchange";
    private String routingKey = "audit.event";
}
