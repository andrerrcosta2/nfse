package com.nobblecrafts.nfse.application.rest;

import com.nobblecrafts.nfse.domain.core.model.Credito;
import com.nobblecrafts.nfse.domain.service.in.ConsultaCreditoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping("/api/creditos")
public class CreditoRestController {

    private final ConsultaCreditoService consultaCreditoService;

    @GetMapping(value = "/{numeroNfse}")
    public ResponseEntity<List<Credito>> consultarPorNumeroNfse(@PathVariable(name = "numeroNfse") String numeroNfse) {
        log.info("Consulta de créditos por número de NFS-e: {}", numeroNfse);
        var creditos = consultaCreditoService.consultarPorNumeroNfse(numeroNfse);
        return ResponseEntity.ok(creditos);
    }

    @GetMapping("/credito/{numeroCredito}")
    public ResponseEntity<Credito> consultarPorNumeroCredito(@PathVariable(name = "numeroCredito") String numeroCredito) {
        log.info("Consulta de crédito por número de crédito: {}", numeroCredito);
        var credito = consultaCreditoService.consultarPorNumeroCredito(numeroCredito);
        return ResponseEntity.ok(credito);
    }
}
