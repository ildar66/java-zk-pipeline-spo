package org.uit.director.db.dbobjects;

import java.io.Serializable;

public class VariablesType implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public static final int BOOLEAN=0;
	public static final int INTEGER=1;
	public static final int FLOAT=2;
	public static final int STRING=3;
	public static final int SELECT=4;
	public static final int DATE=5;
	public static final int ACTION=6;
	public static final int FILE=7;
	public static final int URL=8;
	public static final int USER=9;
	public static final int DATA_SOURCE=10;
	
	public int value;

	public VariablesType(int value) {
		super();
		this.value = value;
	}
	
}
