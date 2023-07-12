package com.techmill.integration.cw.facturacion;

import java.util.Date;

public class Detalle {
	private String codConcepto;
	private String codTarifa;
	private Date fecha;
	private String montoTarifa;
	private String numOperaciones;
	private String tipoCobro;
	private String totalConceptoTarifa;

	public String getCodConcepto() {
		return codConcepto;
	}

	public void setCodConcepto(String codConcepto) {
		this.codConcepto = codConcepto;
	}

	public String getCodTarifa() {
		return codTarifa;
	}

	public void setCodTarifa(String codTarifa) {
		this.codTarifa = codTarifa;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

	public String getMontoTarifa() {
		return montoTarifa;
	}

	public void setMontoTarifa(String montoTarifa) {
		this.montoTarifa = montoTarifa;
	}

	public String getNumOperaciones() {
		return numOperaciones;
	}

	public void setNumOperaciones(String numOperaciones) {
		this.numOperaciones = numOperaciones;
	}

	public String getTipoCobro() {
		return tipoCobro;
	}

	public void setTipoCobro(String tipoCobro) {
		this.tipoCobro = tipoCobro;
	}

	public String getTotalConceptoTarifa() {
		return totalConceptoTarifa;
	}

	public void setTotalConceptoTarifa(String totalConceptoTarifa) {
		this.totalConceptoTarifa = totalConceptoTarifa;
	}

	@Override
	public String toString() {
		return "Detalle [codConcepto=" + codConcepto + ", codTarifa=" + codTarifa + ", fecha=" + fecha
				+ ", montoTarifa=" + montoTarifa + ", numOperaciones=" + numOperaciones + ", tipoCobro=" + tipoCobro
				+ ", totalConceptoTarifa=" + totalConceptoTarifa + "]";
	}

}
