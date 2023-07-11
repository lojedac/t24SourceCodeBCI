package com.techmill.integration.auth;

public class RootAuthentication {
	private String codigo;
	private String password;
	private String ksim;
	private String firma;

	public RootAuthentication(String codigo, String password) {
		this.codigo = codigo;
		this.password = password;
		this.ksim = "123";
		this.firma = "123";
	}

	public String getCodigo() {
		return codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String password) {
		this.password = password;
	}

	public String getKsim() {
		return ksim;
	}

	public void setKsim(String ksim) {
		this.ksim = ksim;
	}

	public String getFirma() {
		return firma;
	}

	public void setFirma(String firma) {
		this.firma = firma;
	}

	@Override
	public String toString() {
		return "Authentication [codigo=" + codigo + ", password=" + password + ", ksim=" + ksim + ", firma=" + firma
				+ "]";
	}

}
