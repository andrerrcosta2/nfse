package com.nobblecrafts.nfse.domain.core.objectvalue;

public enum TipoCredito {
    ISSQN("ISSQN"), OUTRO("OUTRO");

    TipoCredito(String tipoCredito) {}

    public String getCredito() {
        return name();
    }
}
