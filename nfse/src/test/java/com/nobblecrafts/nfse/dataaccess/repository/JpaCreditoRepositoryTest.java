package com.nobblecrafts.nfse.dataaccess.repository;

import com.nobblecrafts.nfse.config.supplier.EntitySupplier;
import com.nobblecrafts.nfse.dataaccess.entity.CreditoEntity;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;
import org.springframework.test.context.ActiveProfiles;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;

@DataJpaTest
@ActiveProfiles("test")
class JpaCreditoRepositoryTest {
    @Autowired
    private JpaCreditoRepository jpaCreditoRepository;

    @Test
    @DisplayName("should save and load by nfse number")
    void shouldSaveAndLoadByNumeroNfse() {
        // Given
        CreditoEntity entity = EntitySupplier.Credito().id(1L).build();

        jpaCreditoRepository.save(entity);

        // When
        List<CreditoEntity> result = jpaCreditoRepository.findByNumeroNfse(entity.getNumeroNfse());

        // Then
        assertThat(result).hasSize(1);
        assertThat(result.get(0)).isEqualTo(entity);
    }

    @Test
    @DisplayName("shouldn't find invalid nfse number")
    void shouldReturnEmptyOptionalForInvalidNumeroNfse() {
        // When
        List<CreditoEntity> result = jpaCreditoRepository.findByNumeroNfse("000000");

        // Then
        assertThat(result).isEmpty();
    }

    @Test
    @DisplayName("should find by credit number")
    void shouldFindByNumeroCredito() {
        // Given
        CreditoEntity entity = EntitySupplier.Credito().id(1L).build();

        jpaCreditoRepository.save(entity);

        // When
        Optional<CreditoEntity> result = jpaCreditoRepository.findByNumeroCredito(entity.getNumeroCredito());

        // Then
        assertThat(result).isPresent();
        assertThat(result.get()).isEqualTo(entity);
    }

    @Test
    @DisplayName("shouldn't find invalid credit number")
    void shouldReturnEmptyOptionalForInvalidNumeroCredito() {
        // When
        Optional<CreditoEntity> result = jpaCreditoRepository.findByNumeroCredito("000000");

        // Then
        assertThat(result).isEmpty();
    }
}
