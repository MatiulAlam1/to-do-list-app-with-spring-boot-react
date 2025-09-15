package com.in28minutes.rest.webservices.restfulwebservices.jwt;


public class RefreshTokenRequest {
	private String refreshToken;

	public RefreshTokenRequest() {
	}

	public String getRefreshToken() {
		return refreshToken;
	}

	// @JsonProperty("refreshToken")
	public void setRefreshToken(String refreshToken) {
		this.refreshToken = refreshToken;
	}
}