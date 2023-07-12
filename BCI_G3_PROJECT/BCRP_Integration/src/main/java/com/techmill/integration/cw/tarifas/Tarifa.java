package com.techmill.integration.cw.tarifas;

public class Tarifa {
	private String codTarifa;
	private String costoFijo;
	private String horaFinal;
	private String horaInicio;
	private String tarifaAplicada;
	private String indAbono;
	private String indCargo;

	public String getCodTarifa() {
		return codTarifa;
	}

	public void setCodTarifa(String codTarifa) {
		this.codTarifa = codTarifa;
	}

	public String getCostoFijo() {
		return costoFijo;
	}

	public void setCostoFijo(String costoFijo) {
		this.costoFijo = costoFijo;
	}

	public String getHoraFinal() {
		return horaFinal;
	}

	public void setHoraFinal(String horaFinal) {
		this.horaFinal = horaFinal;
	}

	public String getHoraInicio() {
		return horaInicio;
	}

	public void setHoraInicio(String horaInicio) {
		this.horaInicio = horaInicio;
	}

	public String getTarifaAplicada() {
		return tarifaAplicada;
	}

	public void setTarifaAplicada(String tarifaAplicada) {
		this.tarifaAplicada = tarifaAplicada;
	}

	public String getIndAbono() {
		return indAbono;
	}

	public void setIndAbono(String indAbono) {
		this.indAbono = indAbono;
	}

	public String getIndCargo() {
		return indCargo;
	}

	public void setIndCargo(String indCargo) {
		this.indCargo = indCargo;
	}

	@Override
	public String toString() {
		return "Tarifa [codTarifa=" + codTarifa + ", costoFijo=" + costoFijo + ", horaFinal=" + horaFinal
				+ ", horaInicio=" + horaInicio + ", tarifaAplicada=" + tarifaAplicada + ", indAbono=" + indAbono
				+ ", indCargo=" + indCargo + "]";
	}

}
