package com.vtb.util;

import java.math.BigDecimal;


public class Converter {	
	public static java.sql.Timestamp toTimetamp(java.util.Date dateIn ) {
		java.sql.Timestamp dateOut = null;
			if (dateIn != null)
				dateOut = new java.sql.Timestamp(dateIn.getTime());// Timestamp(dateIn.getYear(),dateIn.getMonth(),dateIn.getDay(),dateIn.getHours(),dateIn.getMinutes(),dateIn.getSeconds(), 0);				
		return dateOut;
	}
	
	public static java.util.Date toDate(java.sql.Timestamp dateIn ) {		
		java.util.Date dateOut = null;
		if (dateIn != null)
			dateOut = new java.util.Date(dateIn.getYear(),dateIn.getMonth(),dateIn.getDay(),dateIn.getHours(),dateIn.getMinutes(),dateIn.getSeconds());				
		return dateOut;
	}
	
	public static Long toLong(BigDecimal dataIn) {
		Long dataOut = null;
		if (dataIn != null) {
			dataOut = Long.valueOf(dataIn.longValue());
		}		
		return dataOut;
	}
	
	public static Integer toInteger(BigDecimal dataIn) {
		Integer dataOut = null;
		if (dataIn != null) {
			dataOut = Integer.valueOf(dataIn.intValue());
		}		
		return dataOut;
	}
}
