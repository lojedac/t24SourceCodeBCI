package com.techmill.integration.cw.movimientos;

import java.util.ArrayList;

public class Result {

	private ArrayList<Movimiento> Movimientos;
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

	public ArrayList<Movimiento> getMovimientos() {
		return Movimientos;
	}

	public void setMovimientos(ArrayList<Movimiento> movimientos) {
		Movimientos = movimientos;
	}

	@Override
	public String toString() {
		return "Result [Movimientos=" + Movimientos + ", codError=" + codError + ", mensajeError=" + mensajeError + "]";
	}

}
