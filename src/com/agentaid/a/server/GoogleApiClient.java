package com.agentaid.a.server;

public class GoogleApiClient {
	private String id;
	private String secret;

	public GoogleApiClient() {
	}

	public String toString() {
		return id.concat(" ").concat(secret);
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getSecret() {
		return secret;
	}

	public void setSecret(String secret) {
		this.secret = secret;
	}
}
