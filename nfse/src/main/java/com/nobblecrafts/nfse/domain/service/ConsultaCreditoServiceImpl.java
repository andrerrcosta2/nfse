package com.nobblecrafts.nfse.domain.service;

import com.nobblecrafts.nfse.domain.core.event.ConsultaCreditoEventFactory;
import com.nobblecrafts.nfse.domain.core.exception.DomainNotFoundException;
import com.nobblecrafts.nfse.domain.core.model.Credito;
import com.nobblecrafts.nfse.domain.service.in.ConsultaCreditoService;
import com.nobblecrafts.nfse.domain.service.out.message.publisher.ConsultaMessagePublisher;
import com.nobblecrafts.nfse.domain.service.out.repository.CreditoRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class ConsultaCreditoServiceImpl implements ConsultaCreditoService {

    private final CreditoRepository creditoRepository;
    private final ConsultaMessagePublisher consultaMessagePublisher;
    private final ConsultaCreditoValidator validator;

    @Override
    @Transactional(readOnly = true)
    public List<Credito> consultarPorNumeroNfse(String numeroNfse) {
        validator.validateNumeroNfse(numeroNfse);
        List<Credito> resultado = creditoRepository.buscarPorNumeroNfse(numeroNfse);
        log.info("Publishing query event for nfse");
        consultaMessagePublisher.publish(ConsultaCreditoEventFactory.fromNfse(numeroNfse));
        return resultado;
    }

    @Override
    @Transactional(readOnly = true)
    public Credito consultarPorNumeroCredito(String numeroCredito) {
        validator.validateNumeroCredito(numeroCredito);
        try {
            Credito credito = creditoRepository.buscarPorNumeroCredito(numeroCredito)
                    .orElseThrow(() -> new DomainNotFoundException("Crédito não encontrado"));

            consultaMessagePublisher.publish(ConsultaCreditoEventFactory.fromNumeroCredito(numeroCredito));
            return credito;
        } catch (DomainNotFoundException ex) {
            consultaMessagePublisher.publish(ConsultaCreditoEventFactory.fromNumeroCredito(numeroCredito));
            throw ex;
        }
    }
}
