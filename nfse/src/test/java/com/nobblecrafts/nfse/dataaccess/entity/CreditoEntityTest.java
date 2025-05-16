package com.nobblecrafts.nfse.dataaccess.entity;

import com.nobblecrafts.nfse.config.supplier.EntitySupplier;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import static org.assertj.core.api.Assertions.assertThat;

class CreditoEntityTest {

    @Test
    @DisplayName("should be equals if ids are equals")
    void shouldBeEqualsIfIdsAreEqual() {
        // Given
        CreditoEntity entity1 = EntitySupplier.Credito().id(1L).build();

        CreditoEntity entity2 = EntitySupplier.Credito().id(entity1.getId()).build();

        // Then
        assertThat(entity1).isEqualTo(entity2);
        assertThat(entity1.hashCode()).isEqualTo(entity2.hashCode());
    }

    @Test
    @DisplayName("should not be equals if ids aren't equals")
    void shouldNotBeEqualsIfIdsAreDifferent() {
        // Given
        CreditoEntity entity1 = EntitySupplier.Credito().id(1L).build();
        CreditoEntity entity2 = EntitySupplier.Credito().id(2L).build();

        // Then
        assertThat(entity1).isNotEqualTo(entity2);
        assertThat(entity1.hashCode()).isNotEqualTo(entity2.hashCode());
    }

    @Test
    @DisplayName("should be different if is not the same class")
    void shouldBeDifferentFromObject() {
        // Given
        CreditoEntity entity = EntitySupplier.Credito().build();
        Object outro = new Object();

        // Then
        assertThat(entity).isNotEqualTo(outro);
    }

    @Test
    @DisplayName("should be different from null id")
    void shouldBeDifferentFromNullId() {
        // Given
        CreditoEntity entity1 = EntitySupplier.Credito().build();
        CreditoEntity entity2 = EntitySupplier.Credito().id(null).build();

        // Then
        assertThat(entity1).isNotEqualTo(entity2);
        assertThat(entity1.hashCode()).isNotEqualTo(entity2.hashCode());
    }

    @Test
    void hashCodeAndEqualsConsistency() {
        // Given
        CreditoEntity entity1 = EntitySupplier.Credito().build();
        entity1.setId(1L);

        CreditoEntity entity2 = EntitySupplier.Credito().build();
        entity2.setId(1L);

        // Then
        assertThat(entity1.hashCode()).isEqualTo(entity2.hashCode());
    }

}
