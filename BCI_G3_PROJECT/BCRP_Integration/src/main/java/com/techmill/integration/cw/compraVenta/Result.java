package com.techmill.integration.cw.compraVenta;

public class Result {
	private String estado;
	private String numRefLBTR;
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

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getNumRefLBTR() {
		return numRefLBTR;
	}

	public void setNumRefLBTR(String numRefLBTR) {
		this.numRefLBTR = numRefLBTR;
	}

	@Override
	public String toString() {
		return "Result [estado=" + estado + ", numRefLBTR=" + numRefLBTR + ", codError=" + codError + ", mensajeError="
				+ mensajeError + "]";
	}

}
