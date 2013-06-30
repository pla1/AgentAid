package com.agentaid.a.client;

import java.io.Serializable;

public class Agent implements Serializable {
	private static final long serialVersionUID = 382271195327779410L;
	private int id;
	private String emailAddress;
	private String userName;
	private String name;
	private String city;
	private String state;
	private String zipCode;
	private double longitude;
	private double latitude;
	private int level;
	private String profileId;
	private String imageUrl;
	private boolean vetted = false;
	private int role;
	public static final int ROLE_MODERATOR = 1;
	public static final int ROLE_USER = 2;

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(id).append(" ");
		sb.append(emailAddress).append(" ");
		sb.append(userName).append(" ");
		sb.append(name).append(" ");
		sb.append(city).append(" ");
		sb.append(state).append(" ");
		sb.append(zipCode).append(" ");
		sb.append(longitude).append(" ");
		sb.append(latitude).append(" ");
		sb.append(level).append(" ");
		sb.append(profileId).append(" ");
		sb.append(imageUrl).append(" ");
		sb.append(vetted).append(" ");
		sb.append(getRoleDisplay()).append(" ");
		return sb.toString();
	}

	public String getRoleDisplay() {
		switch (role) {
		case ROLE_USER:
			return "User";
		case ROLE_MODERATOR:
			return "Moderator";
		default:
			return "Unknown";
		}
	}

	public void setModerator(boolean b) {
		if (b) {
			role = ROLE_MODERATOR;
		} else {
			role = ROLE_USER;
		}
	}

	public boolean isModerator() {
		return role == ROLE_MODERATOR;
	}

	public String getCity() {
		return city;
	}

	public String getEmailAddress() {
		return emailAddress;
	}

	public int getId() {
		return id;
	}

	public double getLatitude() {
		return latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public String getName() {
		return name;
	}

	public String getState() {
		return state;
	}

	public String getUserName() {
		return userName;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public void setId(int id) {
		this.id = id;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public void setName(String name) {
		this.name = name;
	}

	public void setState(String state) {
		this.state = state;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public int getLevel() {
		return level;
	}

	public void setLevel(int level) {
		this.level = level;
	}

	public String getProfileId() {
		return profileId;
	}

	public void setProfileId(String profileId) {
		this.profileId = profileId;
	}

	public String getImageUrl() {
		return imageUrl;
	}

	public void setImageUrl(String imageUrl) {
		this.imageUrl = imageUrl;
	}

	public boolean isVetted() {
		return vetted;
	}

	public void setVetted(boolean vetted) {
		this.vetted = vetted;
	}

	public int getRole() {
		return role;
	}

	public void setRole(int role) {
		this.role = role;
	}

}
