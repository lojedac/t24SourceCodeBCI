package com.techmill.integration.cw.agentesBolsa;

public class Agente {
	private String codigoSAB;
	private String razonSocial;

	public String getCodigoSAB() {
		return codigoSAB;
	}

	public void setCodigoSAB(String codigoSAB) {
		this.codigoSAB = codigoSAB;
	}

	public String getRazonSocial() {
		return razonSocial;
	}

	public void setRazonSocial(String razonSocial) {
		this.razonSocial = razonSocial;
	}

	@Override
	public String toString() {
		return "Agente [codigoSAB=" + codigoSAB + ", razonSocial=" + razonSocial + "]";
	}

}
