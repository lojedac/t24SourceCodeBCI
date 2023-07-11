package com.techmill.integration.cw.agentesBolsa;

import java.util.ArrayList;

public class Result {
	private ArrayList<Agente> agentes;
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

	public ArrayList<Agente> getAgentes() {
		return agentes;
	}

	public void setAgentes(ArrayList<Agente> agentes) {
		this.agentes = agentes;
	}

	@Override
	public String toString() {
		return "Result [agentes=" + agentes + ", codError=" + codError + ", mensajeError=" + mensajeError + "]";
	}

}
