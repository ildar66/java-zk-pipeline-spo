package ru.md.jsp.tag.helpers;

public class ValidatedObject {
	//html object name
	String name;
	//Data type in html object
	String type;
	//Pattern using in validation time
	String pattern;
	
	boolean required = false;
	
	public ValidatedObject() {
	}
	
	public ValidatedObject(String name) {
		this.name = name;
	}
	
	public ValidatedObject(String name, String type, String pattern) {
		this.name = name;
		this.type = type;
		this.pattern = pattern;
	}
	
	public ValidatedObject(String name, String type, String pattern, boolean required) {
		this.name = name;
		this.type = type;
		this.pattern = pattern;
		this.required = required;
	}
	
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getPattern() {
		return pattern;
	}
	public void setPattern(String pattern) {
		this.pattern = pattern;
	}
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}

	public boolean isRequired() {
		return required;
	}

	public void setRequired(boolean required) {
		this.required = required;
	}
}
