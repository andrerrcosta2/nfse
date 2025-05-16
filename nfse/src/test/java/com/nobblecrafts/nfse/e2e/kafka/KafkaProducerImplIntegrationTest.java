package com.nobblecrafts.nfse.e2e.kafka;

import com.nobblecrafts.nfse.config.AbstractEmbeddedKafkaTest;
import com.nobblecrafts.nfse.config.supplier.ModelSupplier;
import com.nobblecrafts.nfse.domain.core.event.ConsultaCreditoEventFactory;
import com.nobblecrafts.nfse.domain.core.model.Credito;
import com.nobblecrafts.nfse.infrastructure.kafka.model.avro.ConsultaCreditoAvroModel;
import com.nobblecrafts.nfse.infrastructure.kafka.producer.exception.KafkaProducerException;
import com.nobblecrafts.nfse.infrastructure.kafka.producer.service.impl.KafkaProducerImpl;
import com.nobblecrafts.nfse.messaging.mapper.ConsultaMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import org.apache.kafka.clients.consumer.ConsumerRecords;
import org.apache.kafka.common.KafkaException;
import org.apache.kafka.common.errors.TimeoutException;
import org.apache.kafka.common.errors.TopicAuthorizationException;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Profile;
import org.springframework.kafka.support.SendResult;
import org.springframework.kafka.test.utils.KafkaTestUtils;
import org.springframework.test.annotation.DirtiesContext;
import org.springframework.test.context.ActiveProfiles;

import java.time.Duration;
import java.util.Collections;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.awaitility.Awaitility.await;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@Profile("test")
@ActiveProfiles("test")
@TestMethodOrder(MethodOrderer.MethodName.class)
@DirtiesContext
class KafkaProducerImplIntegrationTest extends AbstractEmbeddedKafkaTest<ConsultaCreditoAvroModel> {
    @Autowired
    private ConsultaMessagingDataMapper mapper;
    @Autowired
    private KafkaProducerImpl<String, ConsultaCreditoAvroModel> producer;

    @AfterEach
    void tearDown() {
        producer.close();
    }

    @Test
    @DisplayName("Should send and receive kafka message successfully")
    void A00_shouldSendAndReceiveKafkaMessageSuccessfully() {
        Credito credito = ModelSupplier.Credito().build();
        // given
        var event = ConsultaCreditoEventFactory.fromNumeroCredito(credito.getNumeroCredito());
        var topic = config.getTopics().get(0);

        // when
        producer.send(topic, event.getIdentificador(), mapper.consultaCreditoEventToAvroModel(event));

        // then
        await().atMost(Duration.ofSeconds(5))
                .untilAsserted(() -> {
                    var record = KafkaTestUtils.getSingleRecord(consumer, topic);
                    assertThat(record).isNotNull();
                    assertThat(record.key()).isEqualTo(event.getIdentificador());
                    assertThat(record.value()).isNotNull();
                    assertThat(record.value().getIdentificador()).isEqualTo(event.getIdentificador());
                });
    }

    @Test
    @DisplayName("Should throw KafkaProducerException when sending null value")
    void A01_shouldThrowKafkaProducerExceptionWhenValueIsNull() {
        Credito credito = ModelSupplier.Credito().build();
        // Given
        String topic = config.getTopics().get(0);
        var event = ConsultaCreditoEventFactory.fromNumeroCredito(credito.getNumeroCredito());

        // When / Then
        assertThatThrownBy(() -> producer.send(topic, event.getIdentificador(), null))
                .isInstanceOf(KafkaProducerException.class)
                .hasMessageContaining("Message cannot be null");
    }

    @Test
    @DisplayName("Should throw KafkaProducerException if message is null")
    void A02_shouldThrowKafkaProducerExceptionIfMessageIsNull() {
        // given
        Credito credito = ModelSupplier.Credito().build();
        String topic = config.getTopics().get(0);
        var event = ConsultaCreditoEventFactory.fromNumeroCredito(credito.getNumeroCredito());
        var avro = mapper.consultaCreditoEventToAvroModel(event);

        // when / then
        assertThatThrownBy(() -> producer.send(topic, null, avro))
                .isInstanceOf(KafkaProducerException.class)
                .hasMessageContaining("Key cannot be null");
    }

    @Test
    @DisplayName("Should not send message if topic does not exist")
    void A03_shouldNotSendMessageIfTopicDoesNotExist() {
        Credito credito = ModelSupplier.Credito().build();
        String invalidTopic = "invalid-topic";
        var event = ConsultaCreditoEventFactory.fromNumeroCredito(credito.getNumeroCredito());
        var avroModel = mapper.consultaCreditoEventToAvroModel(event);

        log.info("\n\nPRODUCER-SEND\n\n");
        assertThatThrownBy(() -> producer.send(invalidTopic, event.getIdentificador(), avroModel))
                .isInstanceOf(KafkaProducerException.class)
                .hasMessageContaining("Error sending Kafka message");
    }
}
