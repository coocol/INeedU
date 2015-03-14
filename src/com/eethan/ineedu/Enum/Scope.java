package com.eethan.ineedu.Enum;

public enum Scope {
	NEAR("near"),COLLEGE("college"),COUNTRY("country");
	private String type;
	Scope(String type)
	{
		this.setType(type);
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
}
