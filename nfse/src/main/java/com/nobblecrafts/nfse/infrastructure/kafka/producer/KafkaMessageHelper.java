package com.nobblecrafts.nfse.infrastructure.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nobblecrafts.nfse.domain.core.exception.DomainException;
import jakarta.websocket.SendResult;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaMessageHelper {

    private final ObjectMapper objectMapper;

    public <T> T getOrderEventPayload(String payload, Class<T> outputType) {

        if (payload == null) {
            throw new DomainException("Payload must not be null");
        }
        if (outputType == null) {
            throw new DomainException("Output type must not be null");
        }

        try {
            return objectMapper.readValue(payload, outputType);
        } catch (JsonProcessingException e) {
            log.error("Could not read {} object!", outputType.getName(), e);
            throw new DomainException("Could not read " + outputType.getName() + " object!", e);
        }
    }
}

