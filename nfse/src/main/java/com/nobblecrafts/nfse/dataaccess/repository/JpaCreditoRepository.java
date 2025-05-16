package com.nobblecrafts.nfse.dataaccess.repository;

import com.nobblecrafts.nfse.dataaccess.entity.CreditoEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface JpaCreditoRepository extends JpaRepository<CreditoEntity, Long> {
    List<CreditoEntity> findByNumeroNfse(String numeroNfse);
    Optional<CreditoEntity> findByNumeroCredito(String numeroCredito);
}
