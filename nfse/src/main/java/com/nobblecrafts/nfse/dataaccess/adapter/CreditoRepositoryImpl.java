package com.nobblecrafts.nfse.dataaccess.adapter;

import com.nobblecrafts.nfse.domain.core.model.Credito;
import com.nobblecrafts.nfse.domain.service.out.repository.CreditoRepository;
import com.nobblecrafts.nfse.dataaccess.mapper.CreditoEntityMapper;
import com.nobblecrafts.nfse.dataaccess.repository.JpaCreditoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

@Repository
public class CreditoRepositoryImpl implements CreditoRepository {

    private final JpaCreditoRepository jpaRepository;
    private final CreditoEntityMapper mapper;

    public CreditoRepositoryImpl(JpaCreditoRepository jpaRepository, CreditoEntityMapper mapper) {
        this.jpaRepository = jpaRepository;
        this.mapper = mapper;
    }

    @Override
    public List<Credito> buscarPorNumeroNfse(String numeroNfse) {
        return jpaRepository.findByNumeroNfse(numeroNfse)
                .stream()
                .map(mapper::toDomain)
                .collect(Collectors.toList());
    }

    @Override
    public Optional<Credito> buscarPorNumeroCredito(String numeroCredito) {
        return jpaRepository.findByNumeroCredito(numeroCredito)
                .map(mapper::toDomain);
    }
}
