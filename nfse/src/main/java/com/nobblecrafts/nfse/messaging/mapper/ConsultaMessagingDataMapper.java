package com.nobblecrafts.nfse.messaging.mapper;

import com.nobblecrafts.nfse.domain.core.event.ConsultaCreditoEvent;
import com.nobblecrafts.nfse.domain.core.objectvalue.TipoConsulta;
import com.nobblecrafts.nfse.infrastructure.kafka.model.avro.AvroTipoConsulta;
import com.nobblecrafts.nfse.infrastructure.kafka.model.avro.ConsultaCreditoAvroModel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.ZoneOffset;

@Slf4j
@Component
@NoArgsConstructor
public class ConsultaMessagingDataMapper {
    public ConsultaCreditoAvroModel consultaCreditoEventToAvroModel(ConsultaCreditoEvent event) {
        return ConsultaCreditoAvroModel.newBuilder()
                .setIdentificador(event.getIdentificador())
                .setTipoConsulta(tipoConsultaToAvroModel(event.getTipoConsulta()))
                .setTimestamp(event.getTimestamp().toInstant(ZoneOffset.UTC))
                .build();
    }

    public AvroTipoConsulta tipoConsultaToAvroModel(TipoConsulta tipoConsulta) {
        return AvroTipoConsulta.valueOf(tipoConsulta.getConsulta());
    }
}
