package com.nobblecrafts.nfse.dataaccess.mapper;

import com.nobblecrafts.nfse.domain.core.model.Credito;
import com.nobblecrafts.nfse.dataaccess.entity.CreditoEntity;
import com.nobblecrafts.nfse.domain.core.objectvalue.TipoCredito;
import org.springframework.stereotype.Component;

@Component
public class CreditoEntityMapper {

    public Credito toDomain(CreditoEntity entity) {
        Credito credito = new Credito();
        credito.setNumeroCredito(entity.getNumeroCredito());
        credito.setNumeroNfse(entity.getNumeroNfse());
        credito.setDataConstituicao(entity.getDataConstituicao());
        credito.setValorIssqn(entity.getValorIssqn());
        credito.setTipoCredito(TipoCredito.valueOf(entity.getTipoCredito()));
        credito.setSimplesNacional(entity.isSimplesNacional());
        credito.setAliquota(entity.getAliquota());
        credito.setValorFaturado(entity.getValorFaturado());
        credito.setValorDeducao(entity.getValorDeducao());
        credito.setBaseCalculo(entity.getBaseCalculo());
        return credito;
    }
}
