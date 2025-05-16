package com.nobblecrafts.nfse.dataaccess.entity;

import jakarta.persistence.*;
import lombok.*;
import org.hibernate.Hibernate;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Objects;

@Entity
@Table(name = "credito")
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CreditoEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "numero_credito", nullable = false, unique = true)
    private String numeroCredito;

    @Column(name = "numero_nfse", nullable = false)
    private String numeroNfse;

    @Column(name = "data_constituicao", nullable = false)
    private LocalDate dataConstituicao;

    @Column(name = "valor_issqn", nullable = false)
    private BigDecimal valorIssqn;

    @Column(name = "tipo_credito", nullable = false)
    private String tipoCredito;

    @Column(name = "simples_nacional", nullable = false)
    private boolean simplesNacional;

    @Column(name = "aliquota", nullable = false)
    private BigDecimal aliquota;

    @Column(name = "valor_faturado", nullable = false)
    private BigDecimal valorFaturado;

    @Column(name = "valor_deducao", nullable = false)
    private BigDecimal valorDeducao;

    @Column(name = "base_calculo", nullable = false)
    private BigDecimal baseCalculo;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || Hibernate.getClass(this) != Hibernate.getClass(o)) return false;
        CreditoEntity that = (CreditoEntity) o;
        return id != null && Objects.equals(id, that.id);
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((id == null) ? System.identityHashCode(this) : id.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "CreditoEntity{" +
                "id=" + id +
                ", numeroCredito='" + numeroCredito + '\'' +
                ", numeroNfse='" + numeroNfse + '\'' +
                ", dataConstituicao=" + dataConstituicao +
                ", valorIssqn=" + valorIssqn +
                ", tipoCredito='" + tipoCredito + '\'' +
                ", simplesNacional=" + simplesNacional +
                ", aliquota=" + aliquota +
                ", valorFaturado=" + valorFaturado +
                ", valorDeducao=" + valorDeducao +
                ", baseCalculo=" + baseCalculo +
                '}';
    }
}
