package com.techmill.integration.cw.entidades;

import java.util.ArrayList;

public class Result {
	private ArrayList<Entidad> Entidades;
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

	public ArrayList<Entidad> getEntidades() {
		return Entidades;
	}

	public void setEntidades(ArrayList<Entidad> entidades) {
		this.Entidades = entidades;
	}

	@Override
	public String toString() {
		return "Result [Entidades=" + Entidades + ", codError=" + codError + ", mensajeError=" + mensajeError + "]";
	}

}
