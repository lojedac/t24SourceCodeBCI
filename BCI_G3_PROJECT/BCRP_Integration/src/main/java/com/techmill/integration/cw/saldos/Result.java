package com.techmill.integration.cw.saldos;

import java.util.Date;

public class Result {

	private String codEntidad;
	private Date fechaSaldo;
	private String numCuenta;
	private String saldoActual;
	private String saldoInicial;
	private String totalAbonos;
	private String totalCargos;
	private String codError;
	private String mensajeError;

	public String getCodError() {
		return codError;
	}

	public void setCodError(String codError) {
		this.codError = codError;
	}

	public String getMensajeError() {
		return mensajeError;
	}

	public void setMensajeError(String mensajeError) {
		this.mensajeError = mensajeError;
	}

	public String getCodEntidad() {
		return codEntidad;
	}

	public void setCodEntidad(String codEntidad) {
		this.codEntidad = codEntidad;
	}

	public Date getFechaSaldo() {
		return fechaSaldo;
	}

	public void setFechaSaldo(Date fechaSaldo) {
		this.fechaSaldo = fechaSaldo;
	}

	public String getNumCuenta() {
		return numCuenta;
	}

	public void setNumCuenta(String numCuenta) {
		this.numCuenta = numCuenta;
	}

	public String getSaldoActual() {
		return saldoActual;
	}

	public void setSaldoActual(String saldoActual) {
		this.saldoActual = saldoActual;
	}

	public String getSaldoInicial() {
		return saldoInicial;
	}

	public void setSaldoInicial(String saldoInicial) {
		this.saldoInicial = saldoInicial;
	}

	public String getTotalAbonos() {
		return totalAbonos;
	}

	public void setTotalAbonos(String totalAbonos) {
		this.totalAbonos = totalAbonos;
	}

	public String getTotalCargos() {
		return totalCargos;
	}

	public void setTotalCargos(String totalCargos) {
		this.totalCargos = totalCargos;
	}

	@Override
	public String toString() {
		return "Result [codEntidad=" + codEntidad + ", fechaSaldo=" + fechaSaldo + ", numCuenta=" + numCuenta
				+ ", saldoActual=" + saldoActual + ", saldoInicial=" + saldoInicial + ", totalAbonos=" + totalAbonos
				+ ", totalCargos=" + totalCargos + ", codError=" + codError + ", mensajeError=" + mensajeError + "]";
	}

}
