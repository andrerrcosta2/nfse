package com.nobblecrafts.nfse.domain.core.event;

import com.nobblecrafts.nfse.domain.core.objectvalue.TipoConsulta;
import lombok.Builder;
import lombok.Getter;

import java.time.LocalDateTime;

@Getter
public class ConsultaCreditoEvent {
    private final TipoConsulta tipoConsulta;
    private final String identificador;
    private final LocalDateTime timestamp;

    public ConsultaCreditoEvent(TipoConsulta tipoConsulta, String identificador) {
        this.tipoConsulta = tipoConsulta;
        this.identificador = identificador;
        this.timestamp = LocalDateTime.now();
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        ConsultaCreditoEvent that = (ConsultaCreditoEvent) o;

        if (tipoConsulta != that.tipoConsulta) return false;
        if (!identificador.equals(that.identificador)) return false;
        return timestamp.equals(that.timestamp);
    }

    @Override
    public int hashCode() {
        int result = tipoConsulta.hashCode();
        result = 31 * result + identificador.hashCode();
        result = 31 * result + timestamp.hashCode();
        return result;
    }

    @Override
    public String toString() {
        return "ConsultaCreditoEvent{" +
                "tipoConsulta=" + tipoConsulta.getConsulta() +
                ", identificador='" + identificador + '\'' +
                ", timestamp=" + timestamp +
                '}';
    }
}
