package com.vtb.value;

/**
 * @author Ildar
 * 
 */
public class Name {
	private String last = null;// Ф
	private String first = null;// И
	private String middle = null;// О

	public Name() {
		last = "";
		first = "";
		middle = "";
	}

	public String getFirst() {
		return first;
	}

	public void setFirst(String newValue) {
		first = newValue;
	}

	public String getMiddle() {
		return middle;
	}

	public void setMiddle(String newValue) {
		middle = newValue;
	}

	public String getLast() {
		return last;
	}

	public void setLast(String newValue) {
		last = newValue;
	}

	/**
	 * @param fullName
	 */
	public void parse(String fullName) {
		int firstSpace = fullName.indexOf(' ');
		int lastSpace = fullName.lastIndexOf(' ');
		if (firstSpace == -1) {
			first = "";
			middle = "";
			last = fullName;
		} else {
			last = fullName.substring(0, firstSpace);
			if (firstSpace < lastSpace){
				first = fullName.substring(firstSpace + 1, lastSpace);
				middle = fullName.substring(lastSpace + 1, fullName.length());
			}else{
				first = fullName.substring(firstSpace + 1, fullName.length());;
			}
		}
	}

	/*
	 * (non-Javadoc)
	 * 
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append(last);
		builder.append(' ');
		builder.append(first);
		builder.append(' ');		
		if (middle.length() > 0) {
			builder.append(middle.charAt(0));
			builder.append(".");
		}		
		return builder.toString();
	}

	/**
	 * @param last
	 * @param first
	 * @param middle
	 */
	public Name(String last, String first, String middle) {
		super();
		this.last = last;
		this.first = first;
		this.middle = middle;
	}
}
