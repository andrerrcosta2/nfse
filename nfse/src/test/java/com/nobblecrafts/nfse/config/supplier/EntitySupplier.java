package com.nobblecrafts.nfse.config.supplier;

import com.nobblecrafts.nfse.config.util.DateUtil;
import com.nobblecrafts.nfse.dataaccess.entity.CreditoEntity;
import com.nobblecrafts.nfse.domain.core.objectvalue.TipoCredito;
import net.datafaker.Faker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.util.Random;

public class EntitySupplier {

    private static final Faker faker = new Faker();
    private static final Random RANDOM = new Random();

    private EntitySupplier() {}

    public static CreditoEntity.CreditoEntityBuilder Credito() {
        LocalDate inicio = LocalDate.now().minusYears(1);
        LocalDate fim = LocalDate.now().plusYears(1);
        LocalDate dataConstituicao = DateUtil.randomLocalDateBetween(inicio, fim);

        BigDecimal valorFaturado = BigDecimal.valueOf(faker.number().randomDouble(2, 10000, 50000));
        BigDecimal valorDeducao = BigDecimal.valueOf(faker.number().randomDouble(2, 1000, valorFaturado.longValue() - 1000));
        BigDecimal baseCalculo = valorFaturado.subtract(valorDeducao);
        BigDecimal aliquota = BigDecimal.valueOf(faker.number().randomDouble(2, 2, 10));
        BigDecimal valorIssqn = baseCalculo
                .multiply(aliquota)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);
        String tipoCredito = TipoCredito.values()[RANDOM.nextInt(TipoCredito.values().length)].name();

        return CreditoEntity.builder()
//                .id(faker.number().randomNumber())
                .numeroCredito(faker.number().digits(10))
                .numeroNfse(faker.number().digits(12))
                .dataConstituicao(dataConstituicao)
                .valorIssqn(valorIssqn)
                .tipoCredito(tipoCredito)
                .simplesNacional(faker.bool().bool())
                .aliquota(aliquota)
                .valorFaturado(valorFaturado)
                .valorDeducao(valorDeducao)
                .baseCalculo(baseCalculo);
    }
}
