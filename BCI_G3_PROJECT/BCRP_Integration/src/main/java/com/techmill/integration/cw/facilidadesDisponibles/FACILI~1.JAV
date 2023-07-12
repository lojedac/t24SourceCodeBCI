package com.techmill.integration.cw.facilidadesDisponibles;

public class FacilidadDisponible {

	private String codFacilidad;
	private String codMoneda;
	private String desFacilidad;

	@Override
	public String toString() {
		return "FacilidadDisponible [codFacilidad=" + codFacilidad + ", codMoneda=" + codMoneda + ", desFacilidad="
				+ desFacilidad + "]";
	}

	public String getCodFacilidad() {
		return codFacilidad;
	}

	public void setCodFacilidad(String codFacilidad) {
		this.codFacilidad = codFacilidad;
	}

	public String getCodMoneda() {
		return codMoneda;
	}

	public void setCodMoneda(String codMoneda) {
		this.codMoneda = codMoneda;
	}

	public String getDesFacilidad() {
		return desFacilidad;
	}

	public void setDesFacilidad(String desFacilidad) {
		this.desFacilidad = desFacilidad;
	}

}
