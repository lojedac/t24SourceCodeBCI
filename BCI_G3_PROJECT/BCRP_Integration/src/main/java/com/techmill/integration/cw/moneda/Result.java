package com.techmill.integration.cw.moneda;

import java.util.ArrayList;

public class Result {

	private ArrayList<Moneda> Monedas;
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

	public ArrayList<Moneda> getMonedas() {
		return Monedas;
	}

	public void setMonedas(ArrayList<Moneda> monedas) {
		Monedas = monedas;
	}

	@Override
	public String toString() {
		return "Result [Monedas=" + Monedas + ", codError=" + codError + ", mensajeError=" + mensajeError + "]";
	}

}
