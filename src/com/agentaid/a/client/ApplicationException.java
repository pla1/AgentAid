package com.agentaid.a.client;

public class ApplicationException extends Exception {
	private static final long serialVersionUID = 1L;

	public ApplicationException() {
	}

	public ApplicationException(String message) {
		super(message);
	}
}
