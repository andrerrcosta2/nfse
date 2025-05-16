package com.nobblecrafts.nfse.application.handler;

import com.nobblecrafts.nfse.domain.core.exception.DomainException;
import com.nobblecrafts.nfse.domain.core.exception.DomainNotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DataAccessException;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.LocalDateTime;
import java.util.stream.Collectors;

@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler     {
    @ExceptionHandler(DomainException.class)
    public ResponseEntity<ErrorDTO> handleDomainException(DomainException ex) {
        log.error("Erro de domínio: {}", ex.getMessage(), ex);

        ErrorDTO error = ErrorDTO.builder()
                .code("DOMAIN_ERROR")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(DomainNotFoundException.class)
    public ResponseEntity<ErrorDTO> handleDomainNotFoundException(DomainNotFoundException ex) {
        log.error("Domain Not Found: {}", ex.getMessage(), ex);

        ErrorDTO error = ErrorDTO.builder()
                .code("DOMAIN_NOT_FOUND_ERROR")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorDTO> handleValidationExceptions(MethodArgumentNotValidException ex) {
        log.warn("Method argument not valid exception: {}", ex.getMessage());

        String messages = ex.getBindingResult().getFieldErrors().stream()
                .map(error -> error.getField() + ": " + error.getDefaultMessage())
                .collect(Collectors.joining(", "));

        ErrorDTO error = ErrorDTO.builder()
                .code("VALIDATION_ERROR")
                .message("Erros de validação: " + messages)
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(IllegalArgumentException.class)
    public ResponseEntity<ErrorDTO> handleIllegalArgument(IllegalArgumentException ex) {
        log.warn("Illegal argument exception: {}", ex.getMessage());

        ErrorDTO error = ErrorDTO.builder()
                .code("INVALID_ARGUMENT")
                .message(ex.getMessage())
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(error, HttpStatus.BAD_REQUEST);
    }

    @ExceptionHandler(NoResourceFoundException.class)
    public ResponseEntity<ErrorDTO> handleNoResourceFound(NoResourceFoundException ex) {
        log.warn("Resource not found: {}", ex.getMessage());

        ErrorDTO error = ErrorDTO.builder()
                .code("RESOURCE_NOT_FOUND")
                .message("O recurso solicitado não foi encontrado.")
                .timestamp(LocalDateTime.now())
                .build();

        return new ResponseEntity<>(error, HttpStatus.NOT_FOUND);
    }

    @ExceptionHandler(DataAccessException.class)
    public ResponseEntity<ErrorDTO> handleDatabaseException(DataAccessException ex) {
        log.error("Erro no acesso aos dados", ex);
        ErrorDTO error = ErrorDTO.builder()
                .code("INTERNAL_ERROR")
                .message("Ocorreu um erro interno. Tente novamente mais tarde")
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorDTO> handleGenericException(Exception ex) {
        log.error("Erro genérico", ex);
        ErrorDTO error = ErrorDTO.builder()
                .code("INTERNAL_ERROR")
                .message("Ocorreu um erro interno. Tente novamente mais tarde")
                .timestamp(LocalDateTime.now())
                .build();
        return new ResponseEntity<>(error, HttpStatus.INTERNAL_SERVER_ERROR);
    }
}
