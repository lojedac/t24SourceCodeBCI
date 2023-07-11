package com.techmill.integration.cw.cuentas;

public class Cuenta {
	private String codEntidad;
	private String codMoneda;
	private String nombreCuenta;
	private String numCuenta;
	private String codDivisa;
	
	public String getCodEntidad() {
		return codEntidad;
	}
	public void setCodEntidad(String codEntidad) {
		this.codEntidad = codEntidad;
	}
	public String getCodMoneda() {
		return codMoneda;
	}
	public void setCodMoneda(String codMoneda) {
		this.codMoneda = codMoneda;
	}
	public String getNombreCuenta() {
		return nombreCuenta;
	}
	public void setNombreCuenta(String nombreCuenta) {
		this.nombreCuenta = nombreCuenta;
	}
	public String getNumCuenta() {
		return numCuenta;
	}
	public void setNumCuenta(String numCuenta) {
		this.numCuenta = numCuenta;
	}
	public String getCodDivisa() {
		return codDivisa;
	}
	public void setCodDivisa(String codDivisa) {
		this.codDivisa = codDivisa;
	}
	@Override
	public String toString() {
		return "Cuenta [codEntidad=" + codEntidad + ", codMoneda=" + codMoneda + ", nombreCuenta=" + nombreCuenta
				+ ", numCuenta=" + numCuenta + ", codDivisa=" + codDivisa + "]";
	}


}
