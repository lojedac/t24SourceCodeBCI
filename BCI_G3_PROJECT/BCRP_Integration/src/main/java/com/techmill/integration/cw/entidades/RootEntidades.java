package com.techmill.integration.cw.entidades;

public class RootEntidades {
	private boolean ok;
	private String description;
	private Result result;

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

	@Override
	public String toString() {
		return "Entidades [ok=" + ok + ", description=" + description + ", result=" + result + "]";
	}

}
