package com.techmill.integration.cw.operacionesOtorgadas;

import java.util.Date;

public class OperacionOtorgada {

	private String codBancoDestino;
	private String codBancoOrigen;
	private String codConcepto;
	private String codMoneda;
	private String cuentaDestino;
	private String cuentaOrigen;
	private DatosCliente datosCliente;
	private String estadoLiquidacion;
	private Date fechaLiquidacion;
	private String horaLiquidacion;
	private String instruccionesPago;
	private String montoOperacion;
	private String numRefLBTR;
	private String numRefOrigen;
	private String prioridad;
	private String tipoCambio;

	public String getCodBancoDestino() {
		return codBancoDestino;
	}

	public void setCodBancoDestino(String codBancoDestino) {
		this.codBancoDestino = codBancoDestino;
	}

	public String getCodBancoOrigen() {
		return codBancoOrigen;
	}

	public void setCodBancoOrigen(String codBancoOrigen) {
		this.codBancoOrigen = codBancoOrigen;
	}

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

	public String getCuentaDestino() {
		return cuentaDestino;
	}

	public void setCuentaDestino(String cuentaDestino) {
		this.cuentaDestino = cuentaDestino;
	}

	public String getCuentaOrigen() {
		return cuentaOrigen;
	}

	public void setCuentaOrigen(String cuentaOrigen) {
		this.cuentaOrigen = cuentaOrigen;
	}

	public DatosCliente getDatosCliente() {
		return datosCliente;
	}

	public void setDatosCliente(DatosCliente datosCliente) {
		this.datosCliente = datosCliente;
	}

	public String getEstadoLiquidacion() {
		return estadoLiquidacion;
	}

	public void setEstadoLiquidacion(String estadoLiquidacion) {
		this.estadoLiquidacion = estadoLiquidacion;
	}

	public Date getFechaLiquidacion() {
		return fechaLiquidacion;
	}

	public void setFechaLiquidacion(Date fechaLiquidacion) {
		this.fechaLiquidacion = fechaLiquidacion;
	}

	public String getHoraLiquidacion() {
		return horaLiquidacion;
	}

	public void setHoraLiquidacion(String horaLiquidacion) {
		this.horaLiquidacion = horaLiquidacion;
	}

	public String getInstruccionesPago() {
		return instruccionesPago;
	}

	public void setInstruccionesPago(String instruccionesPago) {
		this.instruccionesPago = instruccionesPago;
	}

	public String getMontoOperacion() {
		return montoOperacion;
	}

	public void setMontoOperacion(String montoOperacion) {
		this.montoOperacion = montoOperacion;
	}

	public String getNumRefLBTR() {
		return numRefLBTR;
	}

	public void setNumRefLBTR(String numRefLBTR) {
		this.numRefLBTR = numRefLBTR;
	}

	public String getNumRefOrigen() {
		return numRefOrigen;
	}

	public void setNumRefOrigen(String numRefOrigen) {
		this.numRefOrigen = numRefOrigen;
	}

	public String getPrioridad() {
		return prioridad;
	}

	public void setPrioridad(String prioridad) {
		this.prioridad = prioridad;
	}

	public String getTipoCambio() {
		return tipoCambio;
	}

	public void setTipoCambio(String tipoCambio) {
		this.tipoCambio = tipoCambio;
	}

	@Override
	public String toString() {
		return "OperacionOtorgada [codBancoDestino=" + codBancoDestino + ", codBancoOrigen=" + codBancoOrigen
				+ ", codConcepto=" + codConcepto + ", codMoneda=" + codMoneda + ", cuentaDestino=" + cuentaDestino
				+ ", cuentaOrigen=" + cuentaOrigen + ", datosCliente=" + datosCliente + ", estadoLiquidacion="
				+ estadoLiquidacion + ", fechaLiquidacion=" + fechaLiquidacion + ", horaLiquidacion=" + horaLiquidacion
				+ ", instruccionesPago=" + instruccionesPago + ", montoOperacion=" + montoOperacion + ", numRefLBTR="
				+ numRefLBTR + ", numRefOrigen=" + numRefOrigen + ", prioridad=" + prioridad + ", tipoCambio="
				+ tipoCambio + "]";
	}

}
