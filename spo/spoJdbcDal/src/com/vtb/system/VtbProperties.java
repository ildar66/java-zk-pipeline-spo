package com.vtb.system;

import java.util.Properties;
import java.io.*;

/**
 * This demonstrates how to obtain the VtbProperties
 */
public class VtbProperties implements PropertyCapable {
/**
 * VtbProperties constructor comment.
 */
public VtbProperties() {
	super();
	initialize();
}
/**
 * applyFilePath method comment.
 */
public String applyFilePath(String aFilename) {
	return platformFilePath()+aFilename;
}
/**
 * applyFilePath method comment.
 */
public String applyFilePath(String anId, String aFilename) {
	return null;
}
/**
 * Return value for key, if default value
 * not in properties file, return default.
 */
public Object at(String aKey) {
	if (aKey.equalsIgnoreCase("servleturl"))
		
		// Check to see if property has value
		{
		if (System.getProperty("servleturl") != null) {
			return System.getProperty("servleturl");
		}
		// Servlet URL defaults to Local host
		else {
			return "http://127.0.0.1:8080/servlet/";
		}
	}
	if (aKey.equalsIgnoreCase("heading"))
		
		// Check to see if property has value
		{
		if (System.getProperty("heading") != null) {
			return System.getProperty("heading");
		}
		// Servlet URL defaults to Local host
		else {
			return "#6699FF";
		}
	}
	if (aKey.equalsIgnoreCase("detail"))
		
		// Check to see if property has value
		{
		if (System.getProperty("detail") != null) {
			return System.getProperty("detail");
		}
		// Servlet URL defaults to Local host
		else {
			return "#AABBCC";
		}
	}
	if (aKey.equalsIgnoreCase("footing"))
		
		// Check to see if property has value
		{
		if (System.getProperty("footing") != null) {
			return System.getProperty("footing");
		}
		// Servlet URL defaults to Local host
		else {
			return "#BBBBBB";
		}
	}
	return null;
}
/**
 * getContext method comment.
 */
public java.awt.Component getContext() {
	return null;
}
/**
 * Initialize System properties from wsbook.properties file
 * A platform neuteral file path is computed by the platformFilePath(); 
 */
public void initialize() {

	
	String path = platformFilePath()+"vtb.properties";
	
	// Create a Properties object with defaults from the System
	Properties props = new Properties(System.getProperties());
	try {
		// Read the .ini file
		props.load(new BufferedInputStream(new FileInputStream(path)));
		// Add to the System Properties
		System.setProperties(props);
		// For Debug, spill properties to console.
//		System.out.println(System.getProperties());
	}catch (Throwable e) {AppService.handle(e);}
}
		
		/**
 * Determines platform specific file path where Apex properties file exists..
 */
private String platformFilePath() {

	String drive = null;
	String separator = System.getProperty("file.separator");
	if (System.getProperty("os.name").toUpperCase().indexOf("WINDOWS") >= 0)
		{drive = "c:\\";}
	else
		{ // Unix does not have drive letters
			drive = "/";
		}
	// return drive+separator+"tad"+separator;
	return drive+separator+"vtb"+separator;
}	
	

/**
 * setContext method comment.
 */
public void setContext(java.awt.Component aComponent) {
}
}
