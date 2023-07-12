package com.techmill.integration.cw.operacionesCvme;

public class RootOperacionesCvme {
	public boolean ok;
	public String description;
	public Result result;
	private String codError;
	private String mensajeError;

	public boolean isOk() {
		return ok;
	}

	public void setOk(boolean ok) {
		this.ok = ok;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Result getResult() {
		return result;
	}

	public void setResult(Result result) {
		this.result = result;
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

	@Override
	public String toString() {
		return "OperacionesCvme [ok=" + ok + ", description=" + description + ", result=" + result + ", codError="
				+ codError + ", mensajeError=" + mensajeError + "]";
	}

}
