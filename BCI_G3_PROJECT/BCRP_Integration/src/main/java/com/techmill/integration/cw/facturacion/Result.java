package com.techmill.integration.cw.facturacion;

import java.util.ArrayList;

public class Result {
	private String codEntidad;
	private String periodo;
	private String totalPeriodo;
	private String totalFijo;
	private String totalVariable;
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

	private ArrayList<Detalle> detalles;

	public String getCodEntidad() {
		return codEntidad;
	}

	public void setCodEntidad(String codEntidad) {
		this.codEntidad = codEntidad;
	}

	public String getPeriodo() {
		return periodo;
	}

	public void setPeriodo(String periodo) {
		this.periodo = periodo;
	}

	public String getTotalPeriodo() {
		return totalPeriodo;
	}

	public void setTotalPeriodo(String totalPeriodo) {
		this.totalPeriodo = totalPeriodo;
	}

	public String getTotalFijo() {
		return totalFijo;
	}

	public void setTotalFijo(String totalFijo) {
		this.totalFijo = totalFijo;
	}

	public String getTotalVariable() {
		return totalVariable;
	}

	public void setTotalVariable(String totalVariable) {
		this.totalVariable = totalVariable;
	}

	public ArrayList<Detalle> getDetalles() {
		return detalles;
	}

	public void setDetalles(ArrayList<Detalle> detalles) {
		this.detalles = detalles;
	}

	@Override
	public String toString() {
		return "Result [codEntidad=" + codEntidad + ", periodo=" + periodo + ", totalPeriodo=" + totalPeriodo
				+ ", totalFijo=" + totalFijo + ", totalVariable=" + totalVariable + ", codError=" + codError
				+ ", mensajeError=" + mensajeError + ", detalles=" + detalles + "]";
	}

}