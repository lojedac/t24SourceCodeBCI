package com.techmill.integration.cw.conceptos;

public class Concepto {
	private String codConcepto;
	private String codMoneda;
	private String desConcepto;
	private String estado;
	private String montoMinimo;
	private String origen;
	private String servicio;

	public String getCodConcepto() {
		return codConcepto;
	}

	public void setCodConcepto(String codConcepto) {
		this.codConcepto = codConcepto;
	}

	public String getCodMoneda() {
		return codMoneda;
	}

	public void setCodMoneda(String codMoneda) {
		this.codMoneda = codMoneda;
	}

	public String getDesConcepto() {
		return desConcepto;
	}

	public void setDesConcepto(String desConcepto) {
		this.desConcepto = desConcepto;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public String getMontoMinimo() {
		return montoMinimo;
	}

	public void setMontoMinimo(String montoMinimo) {
		this.montoMinimo = montoMinimo;
	}

	public String getOrigen() {
		return origen;
	}

	public void setOrigen(String origen) {
		this.origen = origen;
	}

	public String getServicio() {
		return servicio;
	}

	public void setServicio(String servicio) {
		this.servicio = servicio;
	}

	@Override
	public String toString() {
		return "Concepto [codConcepto=" + codConcepto + ", codMoneda=" + codMoneda + ", desConcepto=" + desConcepto
				+ ", estado=" + estado + ", montoMinimo=" + montoMinimo + ", origen=" + origen + ", servicio="
				+ servicio + "]";
	}

}
