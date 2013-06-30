package com.agentaid.a.client;

import java.io.Serializable;

public class CachedGeoResult implements Serializable {
	private static final long serialVersionUID = 15704938852091328L;
	private String query;
	private double latitude;
	private double longitude;
	private String formattedAddress;
	private String city;
	private String zipcode;
	private String state;

	public String toString() {
		StringBuilder sb = new StringBuilder();
		sb.append(query).append(" ");
		sb.append(latitude).append(" ");
		sb.append(longitude).append(" ");
		sb.append(formattedAddress).append(" ");
		sb.append(city).append(" ");
		sb.append(state).append(" ");
		sb.append(zipcode);
		return sb.toString();
	}

	public double getLatitude() {
		return latitude;
	}

	public void setLatitude(double latitude) {
		this.latitude = latitude;
	}

	public double getLongitude() {
		return longitude;
	}

	public void setLongitude(double longitude) {
		this.longitude = longitude;
	}

	public String getFormattedAddress() {
		return formattedAddress;
	}

	public void setFormattedAddress(String formattedAddress) {
		this.formattedAddress = formattedAddress;
	}

	public String getQuery() {
		return query;
	}

	public void setQuery(String query) {
		this.query = query;
	}

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZipcode() {
		return zipcode;
	}

	public void setZipcode(String zipcode) {
		this.zipcode = zipcode;
	}

	public String getState() {
		return state;
	}

	public void setState(String state) {
		this.state = state;
	}

}
