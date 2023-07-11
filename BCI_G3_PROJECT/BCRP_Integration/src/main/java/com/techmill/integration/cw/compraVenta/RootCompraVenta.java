package com.techmill.integration.cw.compraVenta;

public class RootCompraVenta {
	private String sid;
	private DatosCompraVentaME datosCompraVentaME;
	private String firma;

	public RootCompraVenta(String sid, DatosCompraVentaME datosCompraVentaME, String firma) {
		super();
		this.sid = sid;
		this.datosCompraVentaME = datosCompraVentaME;
		this.firma = firma;
	}

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	public DatosCompraVentaME getDatosCompraVentaME() {
		return datosCompraVentaME;
	}

	public void setDatosCompraVentaME(DatosCompraVentaME datosCompraVentaME) {
		this.datosCompraVentaME = datosCompraVentaME;
	}

	public String getFirma() {
		return firma;
	}

	public void setFirma(String firma) {
		this.firma = firma;
	}

	@Override
	public String toString() {
		return "CompraVenta [sid=" + sid + ", datosCompraVentaME=" + datosCompraVentaME + ", firma=" + firma + "]";
	}

}
