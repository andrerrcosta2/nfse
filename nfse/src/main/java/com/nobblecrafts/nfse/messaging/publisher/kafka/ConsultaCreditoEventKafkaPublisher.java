package com.nobblecrafts.nfse.messaging.publisher.kafka;

import com.nobblecrafts.nfse.config.ConsultaServiceConfigData;
import com.nobblecrafts.nfse.domain.core.event.ConsultaCreditoEvent;
import com.nobblecrafts.nfse.domain.service.out.message.publisher.ConsultaMessagePublisher;
import com.nobblecrafts.nfse.infrastructure.kafka.model.avro.ConsultaCreditoAvroModel;
import com.nobblecrafts.nfse.infrastructure.kafka.producer.service.KafkaProducer;
import com.nobblecrafts.nfse.messaging.mapper.ConsultaMessagingDataMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ConsultaCreditoEventKafkaPublisher implements ConsultaMessagePublisher {
    private final ConsultaMessagingDataMapper messagingDataMapper;
    private final KafkaProducer<String, ConsultaCreditoAvroModel> producer;
    private final ConsultaServiceConfigData configData;

    @Override
    public void publish(ConsultaCreditoEvent event) {
        if (event == null) {
            log.warn("Received null ConsultaCreditoEvent. Skipping publish.");
            return;
        }
        try {
            log.info("Received ConsultaCreditoEvent for {}: {}",
                    event.getTipoConsulta(), event.getIdentificador());

            ConsultaCreditoAvroModel model = messagingDataMapper.
                    consultaCreditoEventToAvroModel(event);
            producer.send(configData.getConsultaTopicName(), model.getIdentificador(), model)
                    .whenComplete((result, ex) -> {
                        if (ex != null) {
                            log.error("Error while sending ConsultaCreditoEvent to Kafka for identificador={}, error={}",
                                    model.getIdentificador(), ex.getMessage(), ex);
                        } else {
                            var metadata = result.getRecordMetadata();
                            log.info("ConsultaCreditoEvent sent successfully. Topic: {}; Partition: {}; Offset: {}; Timestamp: {}",
                                    metadata.topic(), metadata.partition(), metadata.offset(), metadata.timestamp());
                        }
                    });

        } catch (Exception e) {
            String identificador = "unknown";
            try {
                identificador = event.getIdentificador();
            } catch (Exception ignored) {
                // No-ops
            }

            log.error("Exception thrown while processing ConsultaCreditoEvent for identificador={}, error={}",
                    identificador, e.getMessage(), e);
        }
    }
}
