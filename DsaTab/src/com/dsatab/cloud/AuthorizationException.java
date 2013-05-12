package com.dsatab.cloud;

import com.dsatab.common.DsaTabException;

public class AuthorizationException extends DsaTabException {

	private static final long serialVersionUID = 6125176899299182097L;

	private String token;

	/**
	 * 
	 */
	public AuthorizationException(String token) {
		this.token = token;
	}

	public String getToken() {
		return token;
	}

}
