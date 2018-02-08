package org.uit.director.db.dbobjects;

import java.io.Serializable;

public class TypeComparator implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public static final int idUser = 0;

	public static final int name = 1;

	public static final int family = 2;

	public static final int patronymic = 3;

	public static final int departamentShortName = 4;

	public static final int departamentFullName = 5;

	public static final int login = 6;

	public static final int mailUser = 7;

	public static final int ipUser = 8;

	public int value;

	public TypeComparator(int value) {
		super();
		this.value = value;
	}
};
