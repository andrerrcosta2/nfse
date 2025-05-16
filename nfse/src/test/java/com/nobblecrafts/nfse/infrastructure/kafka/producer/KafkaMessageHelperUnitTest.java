package com.nobblecrafts.nfse.infrastructure.kafka.producer;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.nobblecrafts.nfse.config.util.Dummy;
import com.nobblecrafts.nfse.domain.core.event.ConsultaCreditoEvent;
import com.nobblecrafts.nfse.domain.core.exception.DomainException;
import com.nobblecrafts.nfse.domain.core.objectvalue.TipoConsulta;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.boot.test.mock.mockito.MockBean;

import java.time.LocalDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class KafkaMessageHelperUnitTest {

    @org.junit.jupiter.api.Nested
    @DisplayName("Mocked Object Mapper")
    class MockedObjectMapperTests {
        private KafkaMessageHelper kafkaMessageHelper;
        @Mock
        private ObjectMapper objectMapper;

        @BeforeEach
        void setUp() {
            kafkaMessageHelper = new KafkaMessageHelper(objectMapper);
        }

        @Test
        @DisplayName("Should Throw Domain Exception when json is invalid")
        void shouldThrowDomainException_WhenJsonIsInvalid() throws JsonProcessingException {
            // Given
            String invalidJson = "invalid-json";

            // When
            when(objectMapper.readValue(invalidJson, ConsultaCreditoEvent.class))
                    .thenThrow(JsonProcessingException.class);

            // Then
            assertThatThrownBy(() -> kafkaMessageHelper.getOrderEventPayload(invalidJson, ConsultaCreditoEvent.class))
                    .isInstanceOf(DomainException.class)
                    .hasMessageContaining("Could not read " + ConsultaCreditoEvent.class.getName() + " object!")
                    .hasCauseInstanceOf(JsonProcessingException.class);
        }

        @Test
        @DisplayName("Should deserialize json with correct event")
        void shouldDeserializeJsonToConsultaCreditoEventSuccessfully() throws JsonProcessingException {
            // Given
            String json = "{"
                    + "\"tipoConsulta\":\"NFSE\","
                    + "\"identificador\":\"7891011\","
                    + "\"timestamp\":\"2025-05-13T10:00:00\""
                    + "}";

            LocalDateTime timestamp = LocalDateTime.of(2025, 5, 13, 10, 0, 0);
            ConsultaCreditoEvent expectedEvent = new ConsultaCreditoEvent(TipoConsulta.NFSE, "7891011");

            // When
            when(objectMapper.readValue(json, ConsultaCreditoEvent.class))
                    .thenReturn(expectedEvent);

            // Then
            ConsultaCreditoEvent result = kafkaMessageHelper.getOrderEventPayload(json, ConsultaCreditoEvent.class);

            assertThat(result).isEqualTo(expectedEvent);
            verify(objectMapper, times(1)).readValue(json, ConsultaCreditoEvent.class);
        }
    }

    @org.junit.jupiter.api.Nested
    @DisplayName("No Mocks")
    class NoMocks {
        private KafkaMessageHelper kafkaMessageHelper;


        @BeforeEach
        void setUp() {
            kafkaMessageHelper = new KafkaMessageHelper(new ObjectMapper());
        }



        @Test
        @DisplayName("Should throw domain exception when output type is null")
        void shouldThrowDomainException_WhenOutputTypeIsNull() {
            // Given
            String json = "{}";

            // Then
            assertThatThrownBy(() -> kafkaMessageHelper.getOrderEventPayload(json, null))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Output type must not be null");
        }

        @Test
        @DisplayName("Should throw domain exception when payload is null")
        void shouldThrowDomainException_WhenPayloadIsNull() {
            // Given
            String json = "{}";

            // Then
            assertThatThrownBy(() -> kafkaMessageHelper.getOrderEventPayload(null, Dummy.class))
                    .isInstanceOf(DomainException.class)
                    .hasMessage("Payload must not be null");
        }

        @Test
        @DisplayName("Should deserialize valid json into object")
        void shouldDeserializeValidJsonIntoObject() {
            // Given
            String json = "{\"name\":\"test\"}";

            // When
            Dummy result = kafkaMessageHelper.getOrderEventPayload(json, Dummy.class);

            // Then
            assertThat(result).isNotNull();
            assertThat(result.getName()).isEqualTo("test");
        }
    }
}
