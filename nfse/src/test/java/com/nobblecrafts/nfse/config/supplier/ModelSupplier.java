package com.nobblecrafts.nfse.config.supplier;

import com.nobblecrafts.nfse.config.util.DateUtil;
import com.nobblecrafts.nfse.domain.core.event.ConsultaCreditoEvent;
import com.nobblecrafts.nfse.domain.core.model.Credito;
import com.nobblecrafts.nfse.domain.core.objectvalue.TipoConsulta;
import com.nobblecrafts.nfse.domain.core.objectvalue.TipoCredito;
import com.nobblecrafts.nfse.infrastructure.kafka.model.avro.AvroTipoConsulta;
import com.nobblecrafts.nfse.infrastructure.kafka.model.avro.ConsultaCreditoAvroModel;
import net.datafaker.Faker;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Random;
import java.util.UUID;

public class ModelSupplier {

    private static final Faker faker = new Faker();
    private static final Random RANDOM = new Random();

    private ModelSupplier() {}

    public static Credito.CreditoBuilder Credito() {
        LocalDate inicio = LocalDate.now().minusYears(1);
        LocalDate fim = LocalDate.now().plusYears(1);
        LocalDate dataConstituicao = DateUtil.randomLocalDateBetween(inicio, fim);

        BigDecimal valorFaturado = BigDecimal.valueOf(faker.number().randomDouble(2, 10000, 50000));
        BigDecimal valorDeducao = BigDecimal.valueOf(faker.number().randomDouble(2, 1000, valorFaturado.longValue() - 1000));
        BigDecimal baseCalculo = valorFaturado.subtract(valorDeducao);
        BigDecimal aliquota = BigDecimal.valueOf(faker.number().randomDouble(2, 2, 10)); // Ex: entre 2% e 10%
        BigDecimal valorIssqn = baseCalculo
                .multiply(aliquota)
                .divide(BigDecimal.valueOf(100), 2, RoundingMode.HALF_UP);

        return Credito.builder()
                .numeroCredito(faker.number().digits(10))
                .numeroNfse(faker.number().digits(12))
                .dataConstituicao(dataConstituicao)
                .tipoCredito(TipoCredito.values()[RANDOM.nextInt(TipoCredito.values().length)])
                .simplesNacional(faker.bool().bool())
                .aliquota(aliquota)
                .valorFaturado(valorFaturado)
                .valorDeducao(valorDeducao)
                .baseCalculo(baseCalculo)
                .valorIssqn(valorIssqn);
    }

    public static ConsultaCreditoAvroModel.Builder ConsultaCreditoAvro() {
        return ConsultaCreditoAvroModel.newBuilder()
                .setTipoConsulta(AvroTipoConsulta.values()[RANDOM.nextInt(AvroTipoConsulta.values().length)])
                .setIdentificador(UUID.randomUUID().toString())
                .setTimestamp(LocalDateTime.now().toInstant(ZoneOffset.UTC));
    }

    public static ConsultaCreditoEvent ConsultaCreditoEvent() {
        return new ConsultaCreditoEvent(TipoConsulta.values()[RANDOM.nextInt(TipoConsulta.values().length)],
                UUID.randomUUID().toString());
    }

}
