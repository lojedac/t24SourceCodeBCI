package com.techmill.integration.cw.cuentas;

import java.util.ArrayList;

public class Result {
	private ArrayList<Cuenta> Cuentas;
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

	public ArrayList<Cuenta> getCuentas() {
		return Cuentas;
	}

	public void setCuentas(ArrayList<Cuenta> cuentas) {
		Cuentas = cuentas;
	}

	@Override
	public String toString() {
		return "Result [Cuentas=" + Cuentas + ", codError=" + codError + ", mensajeError=" + mensajeError + "]";
	}
}
