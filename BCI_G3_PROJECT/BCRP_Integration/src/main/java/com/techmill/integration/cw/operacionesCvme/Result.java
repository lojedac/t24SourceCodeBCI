package com.techmill.integration.cw.operacionesCvme;

import java.util.ArrayList;

public class Result {

	public ArrayList<OperacionCvme> operaciones;
	private String codError;
	private String mensajeError;

	public ArrayList<OperacionCvme> getOperaciones() {
		return operaciones;
	}

	public void setOperaciones(ArrayList<OperacionCvme> operaciones) {
		this.operaciones = operaciones;
	}

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

	@Override
	public String toString() {
		return "Result [operaciones=" + operaciones + ", codError=" + codError + ", mensajeError=" + mensajeError + "]";
	}
}
