package com.nobblecrafts.nfse.dataaccess.adapter;

import com.nobblecrafts.nfse.config.supplier.EntitySupplier;
import com.nobblecrafts.nfse.dataaccess.entity.CreditoEntity;
import com.nobblecrafts.nfse.dataaccess.mapper.CreditoEntityMapper;
import com.nobblecrafts.nfse.dataaccess.repository.JpaCreditoRepository;
import com.nobblecrafts.nfse.domain.core.model.Credito;
import com.nobblecrafts.nfse.domain.core.objectvalue.TipoCredito;
import net.datafaker.Faker;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CreditoRepositoryImplUnitTest {

    @Mock
    private JpaCreditoRepository jpaCreditoRepository;
    @Mock
    private CreditoEntityMapper creditoEntityMapper;
    @InjectMocks
    private CreditoRepositoryImpl creditoRepository;
    private final Faker faker = new Faker();


    @Test
    void expect_to_find_() {
        // Given
        String numeroNfse = faker.business().creditCardNumber();
        CreditoEntity entity = EntitySupplier.Credito().numeroNfse(numeroNfse).build();
        Credito credito = Credito.builder()
                .numeroCredito(entity.getNumeroCredito())
                .numeroNfse(entity.getNumeroNfse())
                .dataConstituicao(entity.getDataConstituicao())
                .valorIssqn(entity.getValorIssqn())
                .tipoCredito(TipoCredito.valueOf(entity.getTipoCredito()))
                .simplesNacional(entity.isSimplesNacional())
                .aliquota(entity.getAliquota())
                .valorFaturado(entity.getValorFaturado())
                .valorDeducao(entity.getValorDeducao())
                .baseCalculo(entity.getBaseCalculo())
                .build();
        when(jpaCreditoRepository.findByNumeroNfse(numeroNfse)).thenReturn(List.of(entity));
        when(creditoEntityMapper.toDomain(entity)).thenReturn(credito);

        // When
        List<Credito> result = creditoRepository.buscarPorNumeroNfse(numeroNfse);

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0).getNumeroCredito()).isEqualTo(credito.getNumeroCredito());
        verify(jpaCreditoRepository, times(1)).findByNumeroNfse(numeroNfse);
        verify(creditoEntityMapper, times(1)).toDomain(entity);
    }

    @Test
    void deveBuscarPorNumeroCredito_RetornandoOptionalPreenchido() {
        // Given
        String numeroCredito = faker.business().creditCardNumber();
        CreditoEntity entity = EntitySupplier.Credito().numeroCredito(numeroCredito).build();
        Credito credito = Credito.builder()
                .numeroCredito(entity.getNumeroCredito())
                .numeroNfse(entity.getNumeroNfse())
                .dataConstituicao(entity.getDataConstituicao())
                .valorIssqn(entity.getValorIssqn())
                .tipoCredito(TipoCredito.valueOf(entity.getTipoCredito()))
                .simplesNacional(entity.isSimplesNacional())
                .aliquota(entity.getAliquota())
                .valorFaturado(entity.getValorFaturado())
                .valorDeducao(entity.getValorDeducao())
                .baseCalculo(entity.getBaseCalculo())
                .build();
        when(jpaCreditoRepository.findByNumeroCredito(numeroCredito)).thenReturn(Optional.of(entity));
        when(creditoEntityMapper.toDomain(entity)).thenReturn(credito);

        // When
        Optional<Credito> result = creditoRepository.buscarPorNumeroCredito(numeroCredito);

        // Then
        assertThat(result).isPresent();
        assertThat(result.get().getNumeroNfse()).isEqualTo(entity.getNumeroNfse());
        verify(jpaCreditoRepository, times(1)).findByNumeroCredito(numeroCredito);
        verify(creditoEntityMapper, times(1)).toDomain(entity);
    }

    @Test
    void deveBuscarPorNumeroCredito_RetornandoOptionalVazio() {
        // Given
        String numeroCredito = "000000";
        when(jpaCreditoRepository.findByNumeroCredito(numeroCredito)).thenReturn(Optional.empty());

        // When
        Optional<Credito> result = creditoRepository.buscarPorNumeroCredito(numeroCredito);

        // Then
        assertThat(result).isEmpty();
        verify(jpaCreditoRepository, times(1)).findByNumeroCredito(numeroCredito);
        verifyNoInteractions(creditoEntityMapper);
    }
}
