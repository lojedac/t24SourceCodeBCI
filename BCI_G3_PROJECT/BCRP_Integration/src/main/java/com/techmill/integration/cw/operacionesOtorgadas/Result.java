package com.techmill.integration.cw.operacionesOtorgadas;

import java.util.ArrayList;

public class Result {

	private ArrayList<OperacionOtorgada> operaciones;
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

	public ArrayList<OperacionOtorgada> getOperaciones() {
		return operaciones;
	}

	public void setOperaciones(ArrayList<OperacionOtorgada> operaciones) {
		this.operaciones = operaciones;
	}

	@Override
	public String toString() {
		return "Result [operaciones=" + operaciones + ", codError=" + codError + ", mensajeError=" + mensajeError + "]";
	}

}
