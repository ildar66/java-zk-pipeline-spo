package com.vtb.report.renderer;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * HTML Renderer. Renders the content as an HTML file  
 * @author Kuznetsov Michael
 *
 */
public class HTMLRenderer implements ReportRenderer {

	private String report;
	private String encoding = "UTF-8";
	private String reportName;
	
	@Override
	public void render(HttpServletRequest request, HttpServletResponse response) throws IOException {
        if (report != null) {
            // finds format of the report : doc or html
         	String[] formatParam = (String[])request.getParameterMap().get("__format");
         	String formatName = "html";
         	if (formatParam != null) formatName = formatParam[0];
             if (formatName.equals("doc")) {
             	// report in the ms word format
             	String fileName = (reportName == null) ? "report" : reportName;
             	fileName = MSWordRenderer.transformFileName(fileName);
             	fileName = new String(fileName.getBytes("CP1251"), "CP1252");

             	response.setCharacterEncoding(encoding);
                response.setContentType("application/msword;charset=" + encoding);
                fileName = new String(fileName.getBytes(encoding), encoding);
                response.setHeader("Content-Disposition", "attachment; filename=\"" + fileName + ".doc" + "\"");
             } else {
             	// report in html format ( + default : when report format is not found).
             	response.setContentType("text/html;charset=" + encoding);            
             }
             PrintWriter out = response.getWriter();            
             out.println(report);
             out.flush();
         }
	}

	@Override
	public String getReport() {
		return report;
	}

	@Override
	public void setReport(String report) {
		this.report = report;
	}

	public void setEncoding(String encoding) {
	    this.encoding = encoding;
	}

    @Override
    public String getReportName() {
        return this.reportName;
    }

    @Override
    public void setReportName(String reportName) {
        this.reportName = reportName;
    }
}
