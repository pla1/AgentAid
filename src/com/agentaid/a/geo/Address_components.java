package com.agentaid.a.geo;


public class Address_components {
	private String long_name;
	private String short_name;

	private String[] types;

	public String getLong_name() {
		return this.long_name;
	}

	public void setLong_name(String long_name) {
		this.long_name = long_name;
	}

	public String getShort_name() {
		return this.short_name;
	}

	public void setShort_name(String short_name) {
		this.short_name = short_name;
	}

	public String[] getTypes() {
		return this.types;
	}

	public void setTypes(String[] types) {
		this.types = types;
	}
}
