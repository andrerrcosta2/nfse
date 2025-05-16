package com.nobblecrafts.nfse.domain.service.out.message.publisher;

import com.nobblecrafts.nfse.domain.core.event.ConsultaCreditoEvent;

public interface ConsultaMessagePublisher {
    void publish(ConsultaCreditoEvent consultaCreditoEvent);
}
