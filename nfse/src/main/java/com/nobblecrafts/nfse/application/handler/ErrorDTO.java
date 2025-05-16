package com.nobblecrafts.nfse.application.handler;

import lombok.Builder;

import java.time.LocalDateTime;

@Builder
public record ErrorDTO(String code, String message, LocalDateTime timestamp) {
}
