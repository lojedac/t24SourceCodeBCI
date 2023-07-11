package com.techmill.integration.auth;

public class Result {
	private String sid;
	private boolean respuesta;
	private String codError;
	private String mensajeError;

	public boolean isRespuesta() {
		return respuesta;
	}

	public void setRespuesta(boolean respuesta) {
		this.respuesta = respuesta;
	}

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

	public String getSid() {
		return sid;
	}

	public void setSid(String sid) {
		this.sid = sid;
	}

	@Override
	public String toString() {
		return "Result [sid=" + sid + ", respuesta=" + respuesta + ", codError=" + codError + ", mensajeError="
				+ mensajeError + "]";
	}

}
