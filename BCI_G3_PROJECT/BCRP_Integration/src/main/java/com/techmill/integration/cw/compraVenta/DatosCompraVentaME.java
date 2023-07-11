package com.techmill.integration.cw.compraVenta;

import java.util.Date;

public class DatosCompraVentaME {
	private String codConcepto;
	private Date fechaLiquidacion;
	private String cuentaDestino;
	private String cuentaOrigen;
	private String montoMN;
	private String montoME;
	private String tipoCambio;
	private String numRefOrigen;
	private String numRefLBTRCV;
	private String instruccionesPago;

	public String getCodConcepto() {
		return codConcepto;
	}

	public void setCodConcepto(String codConcepto) {
		this.codConcepto = codConcepto;
	}

	public Date getFechaLiquidacion() {
		return fechaLiquidacion;
	}

	public void setFechaLiquidacion(Date fechaLiquidacion) {
		this.fechaLiquidacion = fechaLiquidacion;
	}

	public String getCuentaDestino() {
		return cuentaDestino;
	}

	public void setCuentaDestino(String cuentaDestino) {
		this.cuentaDestino = cuentaDestino;
	}

	public String getCuentaOrigen() {
		return cuentaOrigen;
	}

	public void setCuentaOrigen(String cuentaOrigen) {
		this.cuentaOrigen = cuentaOrigen;
	}

	public String getMontoMN() {
		return montoMN;
	}

	public void setMontoMN(String montoMN) {
		this.montoMN = montoMN;
	}

	public String getMontoME() {
		return montoME;
	}

	public void setMontoME(String montoME) {
		this.montoME = montoME;
	}

	public String getTipoCambio() {
		return tipoCambio;
	}

	public void setTipoCambio(String tipoCambio) {
		this.tipoCambio = tipoCambio;
	}

	public String getNumRefOrigen() {
		return numRefOrigen;
	}

	public void setNumRefOrigen(String numRefOrigen) {
		this.numRefOrigen = numRefOrigen;
	}

	public String getNumRefLBTRCV() {
		return numRefLBTRCV;
	}

	public void setNumRefLBTRCV(String numRefLBTRCV) {
		this.numRefLBTRCV = numRefLBTRCV;
	}

	public String getInstruccionesPago() {
		return instruccionesPago;
	}

	public void setInstruccionesPago(String instruccionesPago) {
		this.instruccionesPago = instruccionesPago;
	}

	@Override
	public String toString() {
		return "DatosCompraVentaME [codConcepto=" + codConcepto + ", fechaLiquidacion=" + fechaLiquidacion
				+ ", cuentaDestino=" + cuentaDestino + ", cuentaOrigen=" + cuentaOrigen + ", montoMN=" + montoMN
				+ ", montoME=" + montoME + ", tipoCambio=" + tipoCambio + ", numRefOrigen=" + numRefOrigen
				+ ", numRefLBTRCV=" + numRefLBTRCV + ", instruccionesPago=" + instruccionesPago + "]";
	}

}
