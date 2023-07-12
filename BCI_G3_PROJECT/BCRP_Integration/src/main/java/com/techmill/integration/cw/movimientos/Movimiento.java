package com.techmill.integration.cw.movimientos;

import java.util.Date;

public class Movimiento {

    private String codCargoAbono;
    private String codConcepto;
    private String codEntidad;
    private String codEntidadCP;
    private String codMoneda;
    private Date fechaLiquidacion;
    private String instruccionesPago;
    private String montoOperacion;
    private String numCuenta;
    private String numRefLBTR;
    private String tipoCambio;

    public String getCodCargoAbono() {
        return codCargoAbono;
    }

    public void setCodCargoAbono(String codCargoAbono) {
        this.codCargoAbono = codCargoAbono;
    }

    public String getCodConcepto() {
        return codConcepto;
    }

    public void setCodConcepto(String codConcepto) {
        this.codConcepto = codConcepto;
    }

    public String getCodEntidad() {
        return codEntidad;
    }

    public void setCodEntidad(String codEntidad) {
        this.codEntidad = codEntidad;
    }

    public String getCodEntidadCP() {
        return codEntidadCP;
    }

    public void setCodEntidadCP(String codEntidadCP) {
        this.codEntidadCP = codEntidadCP;
    }

    public String getCodMoneda() {
        return codMoneda;
    }

    public void setCodMoneda(String codMoneda) {
        this.codMoneda = codMoneda;
    }

    public Date getFechaLiquidacion() {
        return fechaLiquidacion;
    }

    public void setFechaLiquidacion(Date fechaLiquidacion) {
        this.fechaLiquidacion = fechaLiquidacion;
    }

    public String getInstruccionesPago() {
        return instruccionesPago;
    }

    public void setInstruccionesPago(String instruccionesPago) {
        this.instruccionesPago = instruccionesPago;
    }

    public String getMontoOperacion() {
        return montoOperacion;
    }

    public void setMontoOperacion(String montoOperacion) {
        this.montoOperacion = montoOperacion;
    }

    public String getNumCuenta() {
        return numCuenta;
    }

    public void setNumCuenta(String numCuenta) {
        this.numCuenta = numCuenta;
    }

    public String getNumRefLBTR() {
        return numRefLBTR;
    }

    public void setNumRefLBTR(String numRefLBTR) {
        this.numRefLBTR = numRefLBTR;
    }

    public String getTipoCambio() {
        return tipoCambio;
    }

    public void setTipoCambio(String tipoCambio) {
        this.tipoCambio = tipoCambio;
    }

    @Override
    public String toString() {
        return "Movimiento [codCargoAbono=" + codCargoAbono + ", codConcepto=" + codConcepto + ", codEntidad="
                + codEntidad + ", codEntidadCP=" + codEntidadCP + ", codMoneda=" + codMoneda + ", fechaLiquidacion="
                + fechaLiquidacion + ", instruccionesPago=" + instruccionesPago + ", montoOperacion=" + montoOperacion
                + ", numCuenta=" + numCuenta + ", numRefLBTR=" + numRefLBTR + ", tipoCambio=" + tipoCambio + "]";
    }
}
