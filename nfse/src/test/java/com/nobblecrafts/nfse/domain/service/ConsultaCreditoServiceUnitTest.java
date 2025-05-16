package com.nobblecrafts.nfse.domain.service;

import com.nobblecrafts.nfse.config.supplier.ModelSupplier;
import com.nobblecrafts.nfse.domain.core.event.ConsultaCreditoEvent;
import com.nobblecrafts.nfse.domain.core.exception.DomainNotFoundException;
import com.nobblecrafts.nfse.domain.core.model.Credito;
import com.nobblecrafts.nfse.domain.service.out.message.publisher.ConsultaMessagePublisher;
import com.nobblecrafts.nfse.domain.service.out.repository.CreditoRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ConsultaCreditoServiceUnitTest {

    @InjectMocks
    private ConsultaCreditoServiceImpl consultaCreditoService;
    @Mock
    private CreditoRepository creditoRepository;
    @Mock
    private ConsultaMessagePublisher consultaMessagePublisher;
    private ConsultaCreditoValidator validator;

    @BeforeEach
    void setUp() {
        validator = new ConsultaCreditoValidator();
        consultaCreditoService = new ConsultaCreditoServiceImpl(creditoRepository, consultaMessagePublisher, validator);
    }

    @Nested
    @DisplayName("Queries for numeroNfse")
    class NumeroNfseQueryTests {

        @Test
        @DisplayName("Should return credit list by numero nfse")
        void shouldReturnCreditList_WhenQueriedByNumeroNfse() {
            // Given
            Credito creditoA = ModelSupplier.Credito().build();
            Credito creditoB = ModelSupplier.Credito().numeroNfse(creditoA.getNumeroNfse()).build();
            Credito creditoC = ModelSupplier.Credito().numeroNfse(creditoA.getNumeroNfse()).build();
            String numeroNfse = creditoA.getNumeroNfse();

            when(creditoRepository.buscarPorNumeroNfse(numeroNfse)).thenReturn(List.of(creditoA, creditoB, creditoC));

            // When
            List<Credito> result = consultaCreditoService.consultarPorNumeroNfse(numeroNfse);

            // Then
            assertThat(result).hasSize(3);
            assertThat(result.get(0).getNumeroNfse()).isEqualTo(numeroNfse);
            assertThat(result.get(1).getNumeroNfse()).isEqualTo(numeroNfse);
            assertThat(result.get(2).getNumeroNfse()).isEqualTo(numeroNfse);
            verify(creditoRepository, times(1)).buscarPorNumeroNfse(numeroNfse);
            verify(consultaMessagePublisher, times(1)).publish(any(ConsultaCreditoEvent.class));
        }

        @Test
        @DisplayName("Should return empty credit list by non-existent numero nfse")
        void shouldReturnEmptyCreditList_WhenQueriedByNonExistentNumeroNfse() {
            Credito credito = ModelSupplier.Credito().build();

            // When
            when(creditoRepository.buscarPorNumeroNfse(any())).thenReturn(List.of());

            List<Credito> result = consultaCreditoService.consultarPorNumeroNfse(credito.getNumeroNfse());

            // Then
            assertThat(result).hasSize(0);
            verify(creditoRepository, times(1)).buscarPorNumeroNfse(credito.getNumeroNfse());
            verify(consultaMessagePublisher, times(1)).publish(any(ConsultaCreditoEvent.class));
        }
    }

    @Nested
    @DisplayName("Queries for NumeroCredito ")
    class NumeroCreditoQueryTests {
        @Test
        @DisplayName("Should return credit optional when queried by numero credito")
        void shouldReturnCreditOptional_WhenQueriedByNumeroCredito() {
            // Given
            Credito credito = ModelSupplier.Credito().build();
            String numeroCredito = credito.getNumeroCredito();

            when(creditoRepository.buscarPorNumeroCredito(numeroCredito)).thenReturn(Optional.of(credito));

            // When
            Credito result = consultaCreditoService.consultarPorNumeroCredito(numeroCredito);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getNumeroCredito()).isEqualTo(numeroCredito);
            verify(creditoRepository, times(1)).buscarPorNumeroCredito(numeroCredito);
            verify(consultaMessagePublisher, times(1)).publish(any(ConsultaCreditoEvent.class));
        }

        @Test
        @DisplayName("Should throw domain not found exception when no numero credito is found")
        void shouldThrowDomainNotFoundException_WhenNoNumeroCreditoIsFound() {
            Credito credito = ModelSupplier.Credito().build();
            String numeroCredito = credito.getNumeroCredito();
            // Given
            when(creditoRepository.buscarPorNumeroCredito(numeroCredito)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> consultaCreditoService.consultarPorNumeroCredito(numeroCredito))
                    .isInstanceOf(DomainNotFoundException.class)
                    .hasMessage("Crédito não encontrado");

            verify(consultaMessagePublisher, times(1)).publish(any(ConsultaCreditoEvent.class));
        }
    }

    @Nested
    @DisplayName("Validations for numeroNfse")
    class NumeroNfseValidationTests {

        @Test
        @DisplayName("Should throw IllegalArgumentException when numeroNfse is null")
        void shouldThrow_WhenNumeroNfseIsNull() {
            assertThatThrownBy(() -> consultaCreditoService.consultarPorNumeroNfse(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Número da NFSe cannot be null");
            verifyNoInteractions(creditoRepository, consultaMessagePublisher);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when numeroNfse is non-numeric")
        void shouldThrow_WhenNumeroNfseIsNonNumeric() {
            assertThatThrownBy(() -> consultaCreditoService.consultarPorNumeroNfse("ABC123"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Número da NFSe deve conter apenas dígitos.");
            verifyNoInteractions(creditoRepository, consultaMessagePublisher);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when numeroNfse exceeds 30 chars")
        void shouldThrow_WhenNumeroNfseIsTooLong() {
            String tooLong = "1".repeat(31);
            assertThatThrownBy(() -> consultaCreditoService.consultarPorNumeroNfse(tooLong))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Número da NFSe excede o tamanho máximo de 30 caracteres.");
            verifyNoInteractions(creditoRepository, consultaMessagePublisher);
        }
    }

    @Nested
    @DisplayName("Validations for numeroCredito")
    class NumeroCreditoValidationTests {

        @Test
        @DisplayName("Should throw IllegalArgumentException when numeroCredito is null")
        void shouldThrow_WhenNumeroCreditoIsNull() {
            assertThatThrownBy(() -> consultaCreditoService.consultarPorNumeroCredito(null))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Número do crédito cannot be null");
            verifyNoInteractions(creditoRepository, consultaMessagePublisher);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when numeroCredito is non-numeric")
        void shouldThrow_WhenNumeroCreditoIsNonNumeric() {
            assertThatThrownBy(() -> consultaCreditoService.consultarPorNumeroCredito("CRED-2025"))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Número do crédito deve conter apenas dígitos.");
            verifyNoInteractions(creditoRepository, consultaMessagePublisher);
        }

        @Test
        @DisplayName("Should throw IllegalArgumentException when numeroCredito exceeds 30 chars")
        void shouldThrow_WhenNumeroCreditoIsTooLong() {
            String tooLong = "9".repeat(31);
            assertThatThrownBy(() -> consultaCreditoService.consultarPorNumeroCredito(tooLong))
                    .isInstanceOf(IllegalArgumentException.class)
                    .hasMessage("Número do crédito excede o tamanho máximo de 30 caracteres.");
            verifyNoInteractions(creditoRepository, consultaMessagePublisher);
        }
    }

    @Nested
    @DisplayName("Event Tests")
    class EventTests {

        @Test
        @DisplayName("Should publish event even on query error")
        void shouldPublishEventEvenOnQueryError() {
            String numeroCredito = "123456";
            // Given
            when(creditoRepository.buscarPorNumeroCredito(numeroCredito)).thenReturn(Optional.empty());

            // When / Then
            assertThatThrownBy(() -> consultaCreditoService.consultarPorNumeroCredito(numeroCredito))
                    .isInstanceOf(DomainNotFoundException.class);

            verify(consultaMessagePublisher, times(1)).publish(any(ConsultaCreditoEvent.class));
        }
    }
}
