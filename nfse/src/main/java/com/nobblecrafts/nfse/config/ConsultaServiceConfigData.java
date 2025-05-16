package com.nobblecrafts.nfse.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "consulta-service")
public class ConsultaServiceConfigData {
    private String consultaTopicName;
}
