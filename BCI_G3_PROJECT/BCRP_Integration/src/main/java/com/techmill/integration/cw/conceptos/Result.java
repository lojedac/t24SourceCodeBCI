package com.techmill.integration.cw.conceptos;

import java.util.ArrayList;

public class Result {
	private ArrayList<Concepto> Conceptos;
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

	public ArrayList<Concepto> getConceptos() {
		return Conceptos;
	}

	public void setConceptos(ArrayList<Concepto> conceptos) {
		this.Conceptos = conceptos;
	}

	@Override
	public String toString() {
		return "Result [Conceptos=" + Conceptos + ", codError=" + codError + ", mensajeError=" + mensajeError + "]";
	}

}
