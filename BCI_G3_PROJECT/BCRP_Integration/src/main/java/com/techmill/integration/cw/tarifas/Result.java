package com.techmill.integration.cw.tarifas;

import java.util.ArrayList;

public class Result {
	private ArrayList<Tarifa> tarifas;
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

	public ArrayList<Tarifa> getTarifas() {
		return tarifas;
	}

	public void setTarifas(ArrayList<Tarifa> tarifas) {
		this.tarifas = tarifas;
	}

	@Override
	public String toString() {
		return "Result [tarifas=" + tarifas + ", codError=" + codError + ", mensajeError=" + mensajeError + "]";
	}
}
