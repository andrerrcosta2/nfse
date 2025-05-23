package com.nobblecrafts.nfse.config.data;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Data
@Configuration
@ConfigurationProperties(prefix = "spring.test.kafka")
public class KafkaTestConfigData {
    String brokers;
    List<String> topics;
}
