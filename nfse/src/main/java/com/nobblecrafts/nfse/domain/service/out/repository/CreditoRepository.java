package com.nobblecrafts.nfse.domain.service.out.repository;

import com.nobblecrafts.nfse.domain.core.model.Credito;

import java.util.List;
import java.util.Optional;

public interface CreditoRepository {
    List<Credito> buscarPorNumeroNfse(String numeroNfse);
    Optional<Credito> buscarPorNumeroCredito(String numeroCredito);
}
