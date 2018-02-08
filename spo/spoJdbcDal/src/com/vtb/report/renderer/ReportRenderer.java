package com.vtb.report.renderer;

import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Interface for different report renderers (plain HTML, MSWord as attachment, PDF etc.)
 * @author Kuznetsov Michael
 *
 */
public interface ReportRenderer {

	/**
	 * Renders the content into the HttpServletResponse response.
	 * @param request HttpServletRequest to get data from.
	 * @param response HttpServletResponse stream and parameters to write the content to.
	 * @throws IOException 
	 */
	void render (HttpServletRequest request, HttpServletResponse response) throws IOException;
	
	/**
     * Gets the report as String 
     * @return report as String
     */
    String getReport();
    
    /**
     * Sets the report as String 
     * @param report as String  
     */
    void setReport(String report);
    
    /**
     * Gets the report name as String 
     * @return report as String
     */
    String getReportName();
    
    /**
     * Sets the report name as String 
     * @param report as String  
     */
    void setReportName(String report);

}
