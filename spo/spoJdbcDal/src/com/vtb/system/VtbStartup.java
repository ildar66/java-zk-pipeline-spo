package com.vtb.system;

/**
 * VTB Startup Class.
 */
public class VtbStartup implements StartupCapable {
/**
 * VtbStartup constructor comment.
 */
public VtbStartup() {
	super();
}

public void end() {

	
}
/**
 * perform startup sequence.
 */
public void start() {

	// Initiliaze StrataSystem class 
	AppService.startup = this;
	
	// Initialize Properties Object
	AppService.property = new VtbProperties();

	
}
}
