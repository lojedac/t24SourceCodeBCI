package com.techmill.integration.cw.operacionesRecibidas;

public class Result {
	private Operaciones operaciones;
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

	public Operaciones getOperaciones() {
		return operaciones;
	}

	public void setOperaciones(Operaciones operaciones) {
		this.operaciones = operaciones;
	}

	@Override
	public String toString() {
		return "Result [operaciones=" + operaciones + ", codError=" + codError + ", mensajeError=" + mensajeError + "]";
	}

}
