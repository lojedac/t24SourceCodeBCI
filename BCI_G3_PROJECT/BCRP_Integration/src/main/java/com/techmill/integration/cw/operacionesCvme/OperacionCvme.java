package com.techmill.integration.cw.operacionesCvme;

import java.util.Date;

public class OperacionCvme {
	private String codBancoDestino;
	private String codBancoOrigen;
	private String codConcepto;
	private String cuentaDestino;
	private String cuentaOrigen;
	private Date fechaLiquidacion;
	private String instruccionesPago;
	private String montoME;
	private String montoMN;
	private String numRefCompraVentaBCR;
	private String numRefOrigen;
	private String tipoCambio;

	public String getCodBancoDestino() {
		return codBancoDestino;
	}

	public void setCodBancoDestino(String codBancoDestino) {
		this.codBancoDestino = codBancoDestino;
	}

	public String getCodBancoOrigen() {
		return codBancoOrigen;
	}

	public void setCodBancoOrigen(String codBancoOrigen) {
		this.codBancoOrigen = codBancoOrigen;
	}

	public String getCodConcepto() {
		return codConcepto;
	}

	public void setCodConcepto(String codConcepto) {
		this.codConcepto = codConcepto;
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

	public String getMontoME() {
		return montoME;
	}

	public void setMontoME(String montoME) {
		this.montoME = montoME;
	}

	public String getMontoMN() {
		return montoMN;
	}

	public void setMontoMN(String montoMN) {
		this.montoMN = montoMN;
	}

	public String getNumRefCompraVentaBCR() {
		return numRefCompraVentaBCR;
	}

	public void setNumRefCompraVentaBCR(String numRefCompraVentaBCR) {
		this.numRefCompraVentaBCR = numRefCompraVentaBCR;
	}

	public String getNumRefOrigen() {
		return numRefOrigen;
	}

	public void setNumRefOrigen(String numRefOrigen) {
		this.numRefOrigen = numRefOrigen;
	}

	public String getTipoCambio() {
		return tipoCambio;
	}

	public void setTipoCambio(String tipoCambio) {
		this.tipoCambio = tipoCambio;
	}

	@Override
	public String toString() {
		return "OperacionCvme [codBancoDestino=" + codBancoDestino + ", codBancoOrigen=" + codBancoOrigen
				+ ", codConcepto=" + codConcepto + ", cuentaDestino=" + cuentaDestino + ", cuentaOrigen=" + cuentaOrigen
				+ ", fechaLiquidacion=" + fechaLiquidacion + ", instruccionesPago=" + instruccionesPago + ", montoME="
				+ montoME + ", montoMN=" + montoMN + ", numRefCompraVentaBCR=" + numRefCompraVentaBCR
				+ ", numRefOrigen=" + numRefOrigen + ", tipoCambio=" + tipoCambio + "]";
	}

}
