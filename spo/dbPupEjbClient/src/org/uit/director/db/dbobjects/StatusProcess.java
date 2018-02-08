/*
 * Created on 25.09.2008
 * 
 */
package org.uit.director.db.dbobjects;

import java.io.Serializable;

public class StatusProcess implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int ACTIVE = 0;

	public static final int COMPLETE = 1;

	public static final int SUSPEND = 2;

	public static final int DELETE = 3;

	public static final int FAULT = 4;
	
	public int value;

	public StatusProcess(int value) {
		super();
		this.value = value;
	}
	
}