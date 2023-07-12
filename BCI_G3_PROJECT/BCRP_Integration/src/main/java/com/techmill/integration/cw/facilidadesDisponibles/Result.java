package com.techmill.integration.cw.facilidadesDisponibles;

import java.util.ArrayList;

public class Result {

	private ArrayList<FacilidadDisponible> Facilidades;
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

	public ArrayList<FacilidadDisponible> getFacilidades() {
		return Facilidades;
	}

	public void setFacilidades(ArrayList<FacilidadDisponible> facilidades) {
		Facilidades = facilidades;
	}

	@Override
	public String toString() {
		return "Result [Facilidades=" + Facilidades + ", codError=" + codError + ", mensajeError=" + mensajeError + "]";
	}

}
