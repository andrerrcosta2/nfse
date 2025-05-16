package com.nobblecrafts.nfse.domain.core.event;

import com.nobblecrafts.nfse.domain.core.objectvalue.TipoConsulta;

public class ConsultaCreditoEventFactory {

    public static ConsultaCreditoEvent fromNumeroCredito(String numeroCredito) {
        return new ConsultaCreditoEvent(TipoConsulta.CREDITO, numeroCredito);
    }

    public static ConsultaCreditoEvent fromNfse(String numeroNfse) {
        return new ConsultaCreditoEvent(TipoConsulta.NFSE, numeroNfse);
    }
}
