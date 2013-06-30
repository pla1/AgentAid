package com.agentaid.a.client;

import java.io.Serializable;

public class Comment implements Serializable {
	private static final long serialVersionUID = -7962347927665425447L;
	private String text;
	private int type;
	private String emailAddress;
	private int id;
	private String timeDisplay;
	public static final int TYPE_LOG = 1;
	public static final int TYPE_USER = 2;

	public Comment() {
	}

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(id).append(" ");
		sb.append(emailAddress).append(" ");
		sb.append(getTypeDisplay()).append(" ");
		sb.append(timeDisplay).append(" ");
		sb.append(text).append(" ");
		return sb.toString();
	}

	public String getTypeDisplay() {
		switch (type) {
		case TYPE_LOG:
			return "Log";
		case TYPE_USER:
			return "User";
		default:
			return "Unknown";
		}
	}

	public void setLogType() {
		type = 1;
	}

	public void setUserType() {
		type = 2;
	}

	public Comment(int type, String emailAddress, String text) {
		this.type = type;
		this.emailAddress = emailAddress;
		this.text = text;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public String getTimeDisplay() {
		return timeDisplay;
	}

	public void setTimeDisplay(String timeDisplay) {
		this.timeDisplay = timeDisplay;
	}
}
