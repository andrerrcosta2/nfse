package com.nobblecrafts.nfse.domain.service.in;

import com.nobblecrafts.nfse.domain.core.model.Credito;

import java.util.List;

public interface ConsultaCreditoService {
    List<Credito> consultarPorNumeroNfse(String numeroNfse);
    Credito consultarPorNumeroCredito(String numeroCredito);
}
