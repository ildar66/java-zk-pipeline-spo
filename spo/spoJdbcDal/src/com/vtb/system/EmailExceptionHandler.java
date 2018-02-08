package com.vtb.system;

import java.io.*;
import java.net.*;
/**
 * The EmailExceptionHandler demonstrates another method of handling exceptions (e.g. sending emails)
 * Creation date: (2/5/00 11:41:07 PM)
 * @author: Administrator
 */
public class EmailExceptionHandler implements ExceptionHandler {
/**
 * EmailExceptionHandler constructor comment.
 */
public EmailExceptionHandler() {
	super();
}
/**
 * Email exception notification
 */
public void handle(ExceptionEvent event) {
	
	String address = "nowhere@crosslogic.com";
	String urlStr = "mailto:"+address;
	try {
		
		URL u = new URL(urlStr);   //Create a mailto the value of email: URL
		URLConnection c = u.openConnection();	//Create a URLConnection for it and call it c
		c.setDoInput(false);  //specify no input from this URL
		c.setDoOutput(true);  //specify we do have output
		c.connect();		  //connect to mail host
		
		//Get output stream to mail host  This allows us to have something to write our message on
		PrintWriter mailout = new PrintWriter(new OutputStreamWriter(c.getOutputStream()));
		//Write out mail headers.
		String headerLine = "From:  \"" + "webmaster@crosslogic.com" + "\" <formtest@crosslogic.com>";
		mailout.println(headerLine);
		System.err.println("FormEmailServlet>>#doEmail writting: " + headerLine);

		headerLine = "To:  \"" + address + "\"";
		mailout.println(headerLine);
		mailout.println();  	//blank line to end the list of headers

		java.util.Date today = event.getDate();
		mailout.println(today);
		mailout.println("** Exception Occured **");
		mailout.println(event.exception);
		mailout.close();
		
	} catch (java.io.IOException e) {
		System.err.println("Error attempting to handle exception " + e);
		return;	
	}}
}
