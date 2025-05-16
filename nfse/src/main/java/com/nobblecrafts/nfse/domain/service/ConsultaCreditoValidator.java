package com.nobblecrafts.nfse.domain.service;

import org.springframework.stereotype.Component;

@Component
public class ConsultaCreditoValidator {

    private static final int MAX_LENGTH_NUMERO_CREDITO = 30;
    private static final int MAX_LENGTH_NUMERO_NFSE = 30;

    public void validateNumeroCredito(String numeroCredito) {
        rejectNullValues(numeroCredito, "Número do crédito");
        validateOnlyDigits(numeroCredito, "Número do crédito");
        validateLength(numeroCredito, MAX_LENGTH_NUMERO_CREDITO, "Número do crédito");
    }

    public void validateNumeroNfse(String numeroNfse) {
        rejectNullValues(numeroNfse, "Número da NFSe");
        validateOnlyDigits(numeroNfse, "Número da NFSe");
        validateLength(numeroNfse, MAX_LENGTH_NUMERO_NFSE, "Número da NFSe");
    }

    private void rejectNullValues(String input, String fieldName) {
        if (input == null) {
            throw new IllegalArgumentException(fieldName + " cannot be null");
        }
    }

    private void validateOnlyDigits(String input, String fieldName) {
        if (!input.matches("\\d+")) {
            throw new IllegalArgumentException(fieldName + " deve conter apenas dígitos.");
        }
    }

    private void validateLength(String input, int maxLength, String fieldName) {
        if (input.length() > maxLength) {
            throw new IllegalArgumentException(fieldName + " excede o tamanho máximo de " + maxLength + " caracteres.");
        }
    }
}
