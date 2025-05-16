package com.nobblecrafts.nfse.infrastructure.kafka.producer.service.impl;

import com.nobblecrafts.nfse.config.supplier.ModelSupplier;
import com.nobblecrafts.nfse.infrastructure.kafka.model.avro.ConsultaCreditoAvroModel;
import com.nobblecrafts.nfse.infrastructure.kafka.producer.exception.KafkaProducerException;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.kafka.KafkaException;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.SendResult;

import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class KafkaProducerImplUnitTest {
    @Mock
    private KafkaTemplate<String, ConsultaCreditoAvroModel> kafkaTemplate;

    @InjectMocks
    private KafkaProducerImpl<String, ConsultaCreditoAvroModel> kafkaProducer;

    private final String topic = "consulta-topic";

    @Test
    @DisplayName("Should send message successfully")
    void shouldSendMessageSuccessfully() {

        ConsultaCreditoAvroModel avro = ModelSupplier.ConsultaCreditoAvro().build();
        // Given
        @SuppressWarnings("unchecked")
        SendResult<String, ConsultaCreditoAvroModel> sendResult = mock(SendResult.class);
        CompletableFuture<SendResult<String, ConsultaCreditoAvroModel>> future = CompletableFuture.
                completedFuture(sendResult);

        when(kafkaTemplate.send(eq(topic), eq(avro.getIdentificador()), eq(avro))).thenReturn(future);

        // When
        CompletableFuture<SendResult<String, ConsultaCreditoAvroModel>> result = kafkaProducer.send(topic, avro.
                getIdentificador(), avro);

        // Then
        assertThat(result).isCompletedWithValue(sendResult);
        verify(kafkaTemplate, times(1)).send(eq(topic), eq(avro.getIdentificador()), eq(avro));
    }

    @Test
    @DisplayName("Should throw a synchronous exception on failure")
    void shouldThrowSynchronousExceptionOnFailure() {
        // Given
        KafkaException expectedError = new KafkaException("Broker not available");
        ConsultaCreditoAvroModel avro = ModelSupplier.ConsultaCreditoAvro().build();

        when(kafkaTemplate.send(eq(topic), eq(avro.getIdentificador()), eq(avro))).thenThrow(expectedError);

        // When / Then
        assertThatThrownBy(() -> kafkaProducer.send(topic, avro.getIdentificador(), avro))
                .isInstanceOf(KafkaProducerException.class)
                .hasMessageContaining("Error sending Kafka message");

        verify(kafkaTemplate, times(1)).send(eq(topic), eq(avro.getIdentificador()), eq(avro));
    }

    @Test
    @DisplayName("Should throw asynchronous exception on future failure")
    void shouldThrowAsynchronousExceptionOnFutureFailure() {
        // Given
        @SuppressWarnings("unchecked")
        SendResult<String, ConsultaCreditoAvroModel> sendResult = mock(SendResult.class);
        Exception asyncError = new RuntimeException("Async Kafka error");
        CompletableFuture<SendResult<String, ConsultaCreditoAvroModel>> futureWithError = CompletableFuture.
                failedFuture(asyncError);
        ConsultaCreditoAvroModel avro = ModelSupplier.ConsultaCreditoAvro().build();

        when(kafkaTemplate.send(eq(topic), eq(avro.getIdentificador()), eq(avro))).thenReturn(futureWithError);

        // When
        CompletableFuture<SendResult<String, ConsultaCreditoAvroModel>> result = kafkaProducer.
                send(topic, avro.getIdentificador(), avro);

        // Then
        assertThatThrownBy(result::join) // .join() wraps exceptions in a CompletionException
                .isInstanceOf(CompletionException.class)
                .hasCause(asyncError);

        verify(kafkaTemplate, times(1)).send(eq(topic), eq(avro.getIdentificador()), eq(avro));
    }

    @Test
    void deveChamarDestroyAoFecharProdutor() {
        // When
        kafkaProducer.close();

        // Then
        verify(kafkaTemplate, times(1)).destroy();
    }
}
