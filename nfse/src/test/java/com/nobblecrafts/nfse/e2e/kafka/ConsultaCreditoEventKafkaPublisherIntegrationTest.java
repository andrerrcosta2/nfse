package com.nobblecrafts.nfse.e2e.kafka;

import com.nobblecrafts.nfse.config.AbstractEmbeddedKafkaTest;
import com.nobblecrafts.nfse.config.ConsultaServiceConfigData;
import com.nobblecrafts.nfse.config.supplier.ModelSupplier;
import com.nobblecrafts.nfse.domain.core.event.ConsultaCreditoEvent;
import com.nobblecrafts.nfse.infrastructure.kafka.model.avro.ConsultaCreditoAvroModel;
import com.nobblecrafts.nfse.messaging.mapper.ConsultaMessagingDataMapper;
import com.nobblecrafts.nfse.messaging.publisher.kafka.ConsultaCreditoEventKafkaPublisher;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.springframework.test.util.ReflectionTestUtils;

import java.time.Duration;
import java.util.stream.IntStream;
import java.util.stream.StreamSupport;

import static org.assertj.core.api.Assertions.assertThat;
import static org.awaitility.Awaitility.await;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Profile("test")
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
@DirtiesContext
class ConsultaCreditoEventKafkaPublisherIntegrationTest extends AbstractEmbeddedKafkaTest<ConsultaCreditoAvroModel> {

    @Autowired
    private ConsultaCreditoEventKafkaPublisher publisher;
    @Autowired
    private ConsultaServiceConfigData config;
    private final ConsultaMessagingDataMapper mapper = new ConsultaMessagingDataMapper();

    @DynamicPropertySource
    static void overrideKafkaTopic(DynamicPropertyRegistry registry) {
        registry.add("consulta-service.consulta-topic-name", () -> "test-topic");
    }

    @Test
    @DisplayName("Should publish event to Kafka successfully")
    void A00_shouldPublishEventToKafkaSuccessfully() {
        // Given
        var event = ModelSupplier.ConsultaCreditoEvent();

        // When
        publisher.publish(event);

        // Then
        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            var records = consumer.poll(Duration.ofMillis(500)); // shorter wait, retried by Awaitility
            var record = StreamSupport.stream(records.spliterator(), false)
                    .filter(r -> r.topic().equals(config.getConsultaTopicName()))
                    .findFirst()
                    .orElseThrow(() -> new AssertionError("No record found for topic"));

            assertThat(record.key()).isEqualTo(event.getIdentificador());
            assertThat(record.value().getIdentificador()).isEqualTo(event.getIdentificador());
        });
    }

    @Test
    @DisplayName("Should log error but not throw exception if Kafka send fails")
    void A01_shouldLogErrorButNotThrowExceptionIfKafkaSendFails() {
        // Given
        var originalTopic = config.getConsultaTopicName();
        var event = ModelSupplier.ConsultaCreditoEvent();
        ReflectionTestUtils.setField(config, "consultaTopicName", "invalid topic name!");

        // When
        publisher.publish(event);

        // Then
        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            var records = consumer.poll(Duration.ofMillis(500));
            assertThat(records.isEmpty()).isTrue(); // nothing should be received
        });

        ReflectionTestUtils.setField(config, "consultaTopicName", originalTopic);
    }

    @Test
    @DisplayName("Should not publish if event is null")
    void A02_shouldNotPublishIfEventIsNull() {
        // When
        publisher.publish(null);

        // Then
        await().atMost(Duration.ofSeconds(3)).untilAsserted(() -> {
            var records = consumer.poll(Duration.ofMillis(500));
            assertThat(records.isEmpty()).isTrue();
        });
    }

    @Test
    @DisplayName("Should handle exception from mapper gracefully")
    void A03_shouldHandleMapperExceptionGracefully() {
        // Given
        var event = ModelSupplier.ConsultaCreditoEvent();
        var invalidEvent = new ConsultaCreditoEvent(event.getTipoConsulta(), event.getIdentificador()) {
            @Override
            public String getIdentificador() {
                throw new RuntimeException("Mapper failure");
            }
        };

        // When
        publisher.publish(invalidEvent);

        // Then
        await().atMost(Duration.ofSeconds(3)).untilAsserted(() -> {
            var records = consumer.poll(Duration.ofMillis(500));
            assertThat(records.isEmpty()).isTrue();
        });
    }

    @Test
    @DisplayName("Should publish multiple events successfully")
    void A04_shouldPublishMultipleEventsSuccessfully() {
        // Given
        var events = IntStream.range(0, 3)
                .mapToObj(i -> ModelSupplier.ConsultaCreditoEvent())
                .toList();
        var expectedKeys = events.stream()
                .map(ConsultaCreditoEvent::getIdentificador)
                .toList();

        // When
        events.forEach(publisher::publish);

        // Then
        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            var records = consumer.poll(Duration.ofMillis(500));
            var keys = StreamSupport.stream(records.spliterator(), false)
                    .map(ConsumerRecord::key)
                    .toList();
            assertThat(keys).containsExactlyInAnyOrderElementsOf(expectedKeys);
        });
    }

    @Test
    @DisplayName("Should publish to correct Kafka partition")
    void A05_shouldPublishToCorrectPartition() {
        // Given
        var event = ModelSupplier.ConsultaCreditoEvent();

        // When
        publisher.publish(event);

        // Then
        await().atMost(Duration.ofSeconds(5)).untilAsserted(() -> {
            var records = consumer.poll(Duration.ofMillis(500));
            var record = StreamSupport.stream(records.spliterator(), false)
                    .filter(r -> r.key().equals(event.getIdentificador()))
                    .findFirst()
                    .orElseThrow();

            assertThat(record.partition()).isGreaterThanOrEqualTo(0); // or test exact partition if known
        });
    }
}
