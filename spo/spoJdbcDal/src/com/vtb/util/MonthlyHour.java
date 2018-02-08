package com.vtb.util;

import java.io.Serializable;
import java.util.Calendar;

public class MonthlyHour implements Serializable, Comparable {
	
	/**
     * 
     */
    private static final long serialVersionUID = 1L;
    private Calendar month;
	private double  hours;
	private String hashString;

	public MonthlyHour(Calendar date, double hours) {
		this.month = date;
		this.hours = hours;
		computeHashString();
	}
	
	public int compareTo(Object monthlyHour) {
		MonthlyHour compareObj = (MonthlyHour)monthlyHour;
		if (hashString.equals(compareObj.getHashString())) return 0;
		else if (getMonth().after(compareObj.getMonth())) return 1;
		else return -1;
	}
	
	public boolean equals(Object o) {
		if (o instanceof MonthlyHour) {
			MonthlyHour mh = (MonthlyHour)o;
			return getMonth().equals(mh.getMonth());
		}
		return false;
	}
	
	private void computeHashString() {
		StringBuffer buf = new StringBuffer((new Integer(getMonth().get(Calendar.YEAR))).toString());
		buf.append((new Integer(getMonth().get(Calendar.MONTH))).toString());
		hashString = buf.toString();
	}
	
	public int hashCode() {
		return hashString.hashCode();
	}
	/**
	 * Gets the month
	 * @return Returns a Calendar
	 */
	public Calendar getMonth() {
		return month;
	}

	/**
	 * Gets the hours
	 * @return Returns a double
	 */
	public double getHours() {
		return hours;
	}
	/**
	 * Sets the hours
	 * @param hours The hours to set
	 */
	public void setHours(double hours) {
		this.hours = hours;
	}

	/**
	 * Gets the hashString
	 * @return Returns a String
	 */
	public String getHashString() {
		return hashString;
	}

	/**
	 * Sets the month
	 * @param month The month to set
	 */
	public void setMonth(Calendar month) {
		this.month = month;
	}

}

