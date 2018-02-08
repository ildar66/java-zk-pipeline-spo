package org.uit.director.db.dbobjects;

import java.io.Serializable;

public class ProcessControlType implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int NONE = 0;

	public static final int DISABLE_FOR_ACTIVE = 1;

	public static final int DISABLE_FOR_ALL = 2;

	public static final int INFORM_FOR_ACTIVE = 3;

	public static final int INFORM_FOR_ALL = 4;
	
	public int value;

	public ProcessControlType(int value) {
		super();
		this.value = value;
	}
	
}
