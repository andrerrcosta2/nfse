package com.nobblecrafts.nfse.messaging.publisher.kafka;

import com.nobblecrafts.nfse.config.ConsultaServiceConfigData;
import com.nobblecrafts.nfse.config.supplier.ModelSupplier;
import com.nobblecrafts.nfse.domain.core.event.ConsultaCreditoEvent;
import com.nobblecrafts.nfse.infrastructure.kafka.model.avro.AvroTipoConsulta;
import com.nobblecrafts.nfse.infrastructure.kafka.model.avro.ConsultaCreditoAvroModel;
import com.nobblecrafts.nfse.infrastructure.kafka.producer.service.KafkaProducer;
import com.nobblecrafts.nfse.messaging.mapper.ConsultaMessagingDataMapper;
import lombok.extern.slf4j.Slf4j;
import nl.altindag.log.LogCaptor;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.ZoneOffset;
import java.util.List;
import java.util.concurrent.CompletableFuture;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.mockito.Mockito.*;

@Slf4j
@ExtendWith(MockitoExtension.class)
public class ConsultaCreditoEventKafkaPublisherUnitTest {

    @InjectMocks
    private ConsultaCreditoEventKafkaPublisher publisher;

    @Mock
    private ConsultaMessagingDataMapper messagingDataMapper;

    @Mock
    private KafkaProducer<String, ConsultaCreditoAvroModel> kafkaProducer;

    @Mock
    private ConsultaServiceConfigData configData;

    private final String topicName = "consulta-topic";

    @Test
    @DisplayName("Should publish event successfully")
    void shouldPublishEventSuccessfully() {
        // Given
        ConsultaCreditoEvent event = ModelSupplier.ConsultaCreditoEvent();
        ConsultaCreditoAvroModel avro = ConsultaCreditoAvroModel.newBuilder()
                .setIdentificador(event.getIdentificador())
                .setTimestamp(event.getTimestamp().toInstant(ZoneOffset.UTC))
                .setTipoConsulta(AvroTipoConsulta.valueOf(event.getTipoConsulta().getConsulta()))
                .build();

        when(configData.getConsultaTopicName()).thenReturn(topicName);
        when(messagingDataMapper.consultaCreditoEventToAvroModel(event)).thenReturn(avro);
        when(kafkaProducer.send(eq(topicName), eq(avro.getIdentificador()), eq(avro)))
                .thenReturn(CompletableFuture.completedFuture(null));

        // When
        assertThatCode(() -> publisher.publish(event))
                .doesNotThrowAnyException();

        // Then
        verify(messagingDataMapper, times(1)).consultaCreditoEventToAvroModel(event);
        verify(kafkaProducer, times(1)).send(eq(topicName), eq(avro.getIdentificador()), eq(avro));
    }

    @Test
    @DisplayName("Should register error when mapping fail")
    void shouldRegisterErrorWhenMappingFail() {
        // Given
        ConsultaCreditoEvent event = ModelSupplier.ConsultaCreditoEvent();
        ConsultaCreditoAvroModel avro = ConsultaCreditoAvroModel.newBuilder()
                .setIdentificador(event.getIdentificador())
                .setTimestamp(event.getTimestamp().toInstant(ZoneOffset.UTC))
                .setTipoConsulta(AvroTipoConsulta.valueOf(event.getTipoConsulta().getConsulta()))
                .build();

        when(messagingDataMapper.consultaCreditoEventToAvroModel(event)).thenThrow(new RuntimeException("Erro ao mapear"));

        // When
        assertThatCode(() -> publisher.publish(event))
                .doesNotThrowAnyException();

        // Then
        verify(messagingDataMapper, times(1)).consultaCreditoEventToAvroModel(event);
        verify(kafkaProducer, never()).send(anyString(), anyString(), any());
    }

    @Test
    void shouldRegisterErrorWhenAsynchronousPublishFail() {
        // Given
        ConsultaCreditoEvent event = ModelSupplier.ConsultaCreditoEvent();
        ConsultaCreditoAvroModel avro = ConsultaCreditoAvroModel.newBuilder()
                .setIdentificador(event.getIdentificador())
                .setTimestamp(event.getTimestamp().toInstant(ZoneOffset.UTC))
                .setTipoConsulta(AvroTipoConsulta.valueOf(event.getTipoConsulta().getConsulta()))
                .build();

        when(configData.getConsultaTopicName()).thenReturn(topicName);
        when(messagingDataMapper.consultaCreditoEventToAvroModel(event)).thenReturn(avro);
        when(kafkaProducer.send(eq(topicName), eq(event.getIdentificador()), eq(avro)))
                .thenReturn(CompletableFuture.failedFuture(new RuntimeException("Erro no envio")));

        // When
        assertThatCode(() -> publisher.publish(event))
                .doesNotThrowAnyException();

        // Then
        verify(messagingDataMapper, times(1)).consultaCreditoEventToAvroModel(event);
        verify(kafkaProducer, times(1)).
                send(eq(topicName), eq(event.getIdentificador()), eq(avro));
    }

    @Test
    void shouldRegisterSynchronousExceptionOnPublishing() {
        // Given
        ConsultaCreditoEvent event = ModelSupplier.ConsultaCreditoEvent();
        ConsultaCreditoAvroModel avro = ConsultaCreditoAvroModel.newBuilder()
                .setIdentificador(event.getIdentificador())
                .setTimestamp(event.getTimestamp().toInstant(ZoneOffset.UTC))
                .setTipoConsulta(AvroTipoConsulta.valueOf(event.getTipoConsulta().getConsulta()))
                .build();

        when(configData.getConsultaTopicName()).thenReturn(topicName);
        when(messagingDataMapper.consultaCreditoEventToAvroModel(event)).thenReturn(avro);
        when(kafkaProducer.send(eq(topicName), eq(event.getIdentificador()), eq(avro)))
                .thenThrow(new RuntimeException("Erro síncrono ao enviar"));

        // When
        assertThatCode(() -> publisher.publish(event))
                .doesNotThrowAnyException();

        // Then
        verify(messagingDataMapper, times(1)).consultaCreditoEventToAvroModel(event);
        verify(kafkaProducer, times(1)).
                send(topicName, event.getIdentificador(), avro);
    }

    // Edge Cases

    @Test
    @DisplayName("Should skip gracefully when event is null")
    void shouldSkipWhenEventIsNull() {
        // Given
        LogCaptor logCaptor = LogCaptor.forClass(ConsultaCreditoEventKafkaPublisher.class);
        ConsultaCreditoEventKafkaPublisher publisher = new ConsultaCreditoEventKafkaPublisher(
                messagingDataMapper, kafkaProducer, configData);

        // When
        publisher.publish(null);

        // Then
        List<String> warnLogs = logCaptor.getWarnLogs();
        assertThat(warnLogs)
                .anyMatch(log -> log.contains("Received null ConsultaCreditoEvent. Skipping publish."));
    }
}
