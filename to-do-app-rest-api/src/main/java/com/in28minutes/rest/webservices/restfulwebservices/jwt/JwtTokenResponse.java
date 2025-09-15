package com.in28minutes.rest.webservices.restfulwebservices.jwt;

public class JwtTokenResponse {

	private String refreshToken;

	public JwtTokenResponse(String token, String refreshToken) {
		super();
		this.token = token;
		this.setRefreshToken(refreshToken);
	}

	private String token;

	public String getToken() {
		return token;
	}

	public void setToken(String token) {
		this.token = token;
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}

}
