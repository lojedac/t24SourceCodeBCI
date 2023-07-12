package com.techmill.integration.cw.tipoDeCambio;

import java.util.Date;

public class Result {
	private String codMoneda;
	private Date fecha;
	private String valorTipoCambio;
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

	public String getCodMoneda() {
		return codMoneda;
	}

	public void setCodMoneda(String codMoneda) {
		this.codMoneda = codMoneda;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getValorTipoCambio() {
		return valorTipoCambio;
	}

	public void setValorTipoCambio(String valorTipoCambio) {
		this.valorTipoCambio = valorTipoCambio;
	}

	@Override
	public String toString() {
		return "Result [codMoneda=" + codMoneda + ", fecha=" + fecha + ", valorTipoCambio=" + valorTipoCambio
				+ ", codError=" + codError + ", mensajeError=" + mensajeError + "]";
	}

}
