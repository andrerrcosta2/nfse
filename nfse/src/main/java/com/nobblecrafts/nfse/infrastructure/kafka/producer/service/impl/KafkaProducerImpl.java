package com.nobblecrafts.nfse.infrastructure.kafka.producer.service.impl;

import com.nobblecrafts.nfse.infrastructure.kafka.producer.exception.KafkaProducerException;
import com.nobblecrafts.nfse.infrastructure.kafka.producer.service.KafkaProducer;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.avro.specific.SpecificRecordBase;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;
import org.springframework.stereotype.Component;

import java.io.Serializable;
import java.util.Objects;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Component
@RequiredArgsConstructor
public class KafkaProducerImpl<K extends Serializable, V extends SpecificRecordBase> implements KafkaProducer<K, V> {

    private final KafkaTemplate<K, V> kafkaTemplate;

    @Override
    public CompletableFuture<SendResult<K, V>> send(String topicName, K key, V message) {
        if (message == null) {
            throw new KafkaProducerException("Message cannot be null");
        }
        if (key == null) {
            throw new KafkaProducerException("Key cannot be null");
        }

        log.info("Sending message={} to topic={}", message, topicName);
        try {
            return kafkaTemplate.send(topicName, key, message)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Error sending Kafka message with key={}, message={}, exception={}",
                                    key, message, ex.getMessage(), ex);
                        } else if (result != null && result.getRecordMetadata() != null) {
                            var metadata = result.getRecordMetadata();
                            log.info("Message sent successfully: topic={}, partition={}, offset={}",
                                    metadata.topic(), metadata.partition(), metadata.offset());
                        }
                    });
        } catch (KafkaException e) {
            log.error("Exception thrown synchronously on kafkaTemplate.send(): key={}, message={}, error={}",
                    key, message, e.getMessage(), e);
            throw new KafkaProducerException("Error sending Kafka message");
        }
    }


    @PreDestroy
    public void close() {
        if (kafkaTemplate != null) {
            log.info("Closing kafka producer!");
            kafkaTemplate.destroy();
        }
    }
}

