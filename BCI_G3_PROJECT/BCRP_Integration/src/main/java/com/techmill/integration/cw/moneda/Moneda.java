package com.techmill.integration.cw.moneda;

public class Moneda {

    private String codMoneda;
    private String desMoneda;
    private String simbolo;

    public String getCodMoneda() {
        return codMoneda;
    }

    public void setCodMoneda(String codMoneda) {
        this.codMoneda = codMoneda;
    }

    public String getDesMoneda() {
        return desMoneda;
    }

    public void setDesMoneda(String desMoneda) {
        this.desMoneda = desMoneda;
    }

    public String getSimbolo() {
        return simbolo;
    }

    public void setSimbolo(String simbolo) {
        this.simbolo = simbolo;
    }

    @Override
    public String toString() {
        return "Moneda [codMoneda=" + codMoneda + ", desMoneda=" + desMoneda + ", simbolo=" + simbolo + "]";
    }

}
