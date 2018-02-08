package com.vtb.utils;

import java.math.BigDecimal;
import java.sql.Blob;
import java.util.Date;

/**
 * Useful scripts.
 * @author Michael Kuznetsov
 *
 */
public final class Utils {

	/**
	 * Returns String value of the object regarding object type
	 * @param variable
	 * @return
	 */
	static public String varToString (Object variable) {
		try {
			if (variable instanceof String) return (String) variable;
			if (variable instanceof BigDecimal) return ((BigDecimal) variable).toString();
			if (variable instanceof Long) return ((Long) variable).toString();		
			if (variable instanceof Integer) return ((Integer) variable).toString();
			if (variable instanceof Double) return ((Double) variable).toString();
			if (variable instanceof Boolean) return ((Boolean) variable).toString();
			if (variable instanceof Date) return ((Date) variable).toString();
			if (variable instanceof Blob) return ((Blob) variable).toString();		 
			// null values are not recognized. Return empty string. 
			return "";
		} catch (Exception e) {
			return "";
		}
		//return "not recognized type";	
	}

	/**
	 * Returns Long value of the object (which should be Long)
	 * @param variable
	 * @return
	 */
	static public Long objToLong (Object variable) {
		try {
			if (variable instanceof Long) return (Long) variable;
			if (variable instanceof BigDecimal) return new Long(  ((BigDecimal) variable).longValue());
			if (variable instanceof Integer) return new Long(  ((Integer) variable).longValue());
			if (variable instanceof Double) return new Long(  ((Double) variable).longValue());
			return null; 
		} catch (Exception e) {
			return null;
		}	
	}
	
	/**
	 * Returns Long value of the object (which should be Long)
	 * @param variable
	 * @return
	 */
	static public Long stringToLong (String variable) {
		try {
			if (variable == null || variable.equals("")) return null; 
			return Long.parseLong(variable);			 
		} catch (Exception e) {
			return null;
		}
	}
}
