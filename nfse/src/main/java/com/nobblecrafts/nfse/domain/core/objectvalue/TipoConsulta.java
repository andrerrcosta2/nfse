package com.nobblecrafts.nfse.domain.core.objectvalue;

public enum TipoConsulta {
    NFSE("NFSE"), CREDITO("CREDITO");

    TipoConsulta(String tipoConsulta) {}

    public String getConsulta() {
        return name();
    }
}
