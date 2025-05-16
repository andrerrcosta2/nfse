package com.nobblecrafts.nfse.e2e.rest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.nobblecrafts.nfse.Application;
import com.nobblecrafts.nfse.config.AbstractContextualTest;
import com.nobblecrafts.nfse.dataaccess.entity.CreditoEntity;
import com.nobblecrafts.nfse.dataaccess.repository.JpaCreditoRepository;
import com.nobblecrafts.nfse.domain.core.event.ConsultaCreditoEvent;
import com.nobblecrafts.nfse.domain.service.out.message.publisher.ConsultaMessagePublisher;
import org.hamcrest.Matchers;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.http.MediaType;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;
import static com.nobblecrafts.nfse.config.supplier.EntitySupplier.*;

@SpringBootTest(classes = Application.class)
@AutoConfigureMockMvc
@ActiveProfiles("test")
public class CreditoRestControllerTest extends AbstractContextualTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private JpaCreditoRepository jpaCreditoRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @MockBean
    private ConsultaMessagePublisher kafkaPublisher;

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Queries for numeroNfse")
    class NumeroNfseQueryTests {
        private List<CreditoEntity> creditos = new ArrayList<>();
        private final String url =  "/api/creditos";

        @BeforeEach
        void context() {
            // Assert it is clean
            deleteAllByIds(CreditoEntity.class,
                    creditos.stream()
                            .map(CreditoEntity::getId)
                            .collect(Collectors.toList())
            );
            // Create context
            creditos = save(
                    Credito().numeroNfse("11111111").build(),
                    Credito().numeroNfse("11111111").build(),
                    Credito().numeroNfse("11111111").build(),
                    Credito().numeroNfse("11111111").build(),
                    Credito().numeroNfse("22222222").build(),
                    Credito().numeroNfse("22222222").build(),
                    Credito().numeroNfse("22222222").build(),
                    Credito().numeroNfse("22222222").build(),
                    Credito().numeroNfse("22222222").build(),
                    Credito().numeroNfse("22222222").build(),
                    Credito().numeroNfse("33333333").build()
            );
        }

        @Test
        @DisplayName("Should return credito list by numero Nfse")
        void shouldReturnCreditoListByNumeroNfse1() throws Exception {
            mockMvc.perform(get(String.format("%s/%s", url, "11111111"))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(4))
                    .andExpect(jsonPath("$[0].numeroCredito").value(creditos.get(0).getNumeroCredito()))
                    .andExpect(jsonPath("$[1].numeroCredito").value(creditos.get(1).getNumeroCredito()))
                    .andExpect(jsonPath("$[2].numeroCredito").value(creditos.get(2).getNumeroCredito()))
                    .andExpect(jsonPath("$[3].numeroCredito").value(creditos.get(3).getNumeroCredito()))
                    .andExpect(jsonPath("$[*].numeroNfse").value(Matchers.everyItem(Matchers.is("11111111"))));
        }

        @Test
        @DisplayName("Should return credito list by numero Nfse")
        void shouldReturnCreditoListByNumeroNfse2() throws Exception {
            mockMvc.perform(get("/api/creditos/22222222")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andDo(print())
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(6))
                    .andExpect(jsonPath("$[0].numeroCredito").value(creditos.get(4).getNumeroCredito()))
                    .andExpect(jsonPath("$[1].numeroCredito").value(creditos.get(5).getNumeroCredito()))
                    .andExpect(jsonPath("$[2].numeroCredito").value(creditos.get(6).getNumeroCredito()))
                    .andExpect(jsonPath("$[3].numeroCredito").value(creditos.get(7).getNumeroCredito()))
                    .andExpect(jsonPath("$[4].numeroCredito").value(creditos.get(8).getNumeroCredito()))
                    .andExpect(jsonPath("$[5].numeroCredito").value(creditos.get(9).getNumeroCredito()))
                    .andExpect(jsonPath("$[*].numeroNfse").value(Matchers.everyItem(Matchers.is("22222222"))));
        }

        @Test
        @DisplayName("Should return empty list when numeroNfse does not exist")
        void shouldReturnEmptyListForNonexistentNumeroNfse() throws Exception {
            mockMvc.perform(get(url + "/99999999")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk())
                    .andExpect(jsonPath("$.length()").value(0));
        }

        @Test
        @DisplayName("Should throw error when numeroNfse is empty")
        void shouldThrowErrorWhenNumeroNfseIsEmpty() throws Exception {
            mockMvc.perform(get(url + "/", ""))
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("Should throw error when numeroNfse is blank")
        void shouldThrowErrorWhenNumeroNfseIsBlank() throws Exception {
            mockMvc.perform(get(url + "/   ")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should publish event to Kafka when query is successful")
        void shouldPublishEventToKafkaWhenQueryIsSuccessful() throws Exception {
            var entity = Credito().numeroNfse("55555555").build();
            jpaCreditoRepository.save(entity);

            mockMvc.perform(get("/api/creditos/55555555")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(kafkaPublisher, times(1)).publish(any(ConsultaCreditoEvent.class));
        }
    }

    @Nested
    @TestInstance(TestInstance.Lifecycle.PER_CLASS)
    @DisplayName("Queries for numero credito")
    class NumeroCreditoQueryTests {
        private List<CreditoEntity> creditos = new ArrayList<>();
        private final String url =  "/api/creditos/credito";

        @BeforeEach
        void context() {
            // Assert it is clean
            deleteAllByIds(CreditoEntity.class,
                    creditos.stream()
                            .map(CreditoEntity::getId)
                            .collect(Collectors.toList())
            );
            // Create context
            creditos = save(
                    Credito().build(), Credito().build(), Credito().build(),
                    Credito().build(), Credito().build(), Credito().build(),
                    Credito().build(), Credito().build(), Credito().build(),
                    Credito().build(), Credito().build(), Credito().build()
            );
        }

        @Test
        @DisplayName("Should return a single credito for each number")
        void shouldReturnCreditoByNumeroCredito() throws Exception {
            for (int i = 0; i < creditos.size(); i++) {
                var expected = creditos.get(i);

                mockMvc.perform(get(String.format("%s/%s", url, expected.getNumeroCredito()))
                                .contentType(MediaType.APPLICATION_JSON))
                        .andExpect(status().isOk())
                        .andExpect(jsonPath("$.numeroCredito").value(expected.getNumeroCredito()))
                        .andExpect(jsonPath("$.numeroNfse").value(expected.getNumeroNfse()))
                        .andExpect(jsonPath("$.dataConstituicao").value(expected.getDataConstituicao().toString()))
                        .andExpect(jsonPath("$.valorIssqn").value(expected.getValorIssqn().doubleValue()))
                        .andExpect(jsonPath("$.tipoCredito").value(expected.getTipoCredito()))
                        .andExpect(jsonPath("$.simplesNacional").value(expected.isSimplesNacional()))
                        .andExpect(jsonPath("$.aliquota").value(expected.getAliquota().doubleValue()))
                        .andExpect(jsonPath("$.valorFaturado").value(expected.getValorFaturado().doubleValue()))
                        .andExpect(jsonPath("$.valorDeducao").value(expected.getValorDeducao().doubleValue()))
                        .andExpect(jsonPath("$.baseCalculo").value(expected.getBaseCalculo().doubleValue()));
            }
        }

        @Test
        @DisplayName("Should return domain not found when numero credito does not exist")
        void shouldReturnDomainNotFoundForInexistentNumeroCredito() throws Exception {
            mockMvc.perform(get(url + "/99999999")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isNotFound());
        }

        @Test
        @DisplayName("Should throw error when numeroNfse is empty")
        void shouldThrowErrorWhenNumeroCreditoIsEmpty() throws Exception {
            mockMvc.perform(get(url + "/", ""))
                    .andExpect(status().is4xxClientError());
        }

        @Test
        @DisplayName("Should throw error when numeroNfse is blank")
        void shouldThrowErrorWhenNumeroCreditoIsBlank() throws Exception {
            mockMvc.perform(get(url + "/   ")
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isBadRequest());
        }

        @Test
        @DisplayName("Should publish event to Kafka when query is successful")
        void shouldPublishEventToKafkaWhenQueryIsSuccessful() throws Exception {
            var entity = Credito().build();
            jpaCreditoRepository.save(entity);

            mockMvc.perform(get(String.format("%s/%s", url, entity.getNumeroCredito()))
                            .contentType(MediaType.APPLICATION_JSON))
                    .andExpect(status().isOk());

            verify(kafkaPublisher, times(1)).publish(any(ConsultaCreditoEvent.class));
        }
    }
}
