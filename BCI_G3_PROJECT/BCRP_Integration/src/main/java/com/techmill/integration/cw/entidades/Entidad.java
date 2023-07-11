package com.techmill.integration.cw.entidades;

public class Entidad {
    private String codEntidad;
    private String nomCortoEntidad;
    private String nomLargoEntidad;

    public String getCodEntidad() {
        return codEntidad;
    }

    public void setCodEntidad(String codEntidad) {
        this.codEntidad = codEntidad;
    }

    public String getNomCortoEntidad() {
        return nomCortoEntidad;
    }

    public void setNomCortoEntidad(String nomCortoEntidad) {
        this.nomCortoEntidad = nomCortoEntidad;
    }

    public String getNomLargoEntidad() {
        return nomLargoEntidad;
    }

    public void setNomLargoEntidad(String nomLargoEntidad) {
        this.nomLargoEntidad = nomLargoEntidad;
    }

    @Override
    public String toString() {
        return "Entidad [codEntidad=" + codEntidad + ", nomCortoEntidad=" + nomCortoEntidad + ", nomLargoEntidad="
                + nomLargoEntidad + "]";
    }
}
