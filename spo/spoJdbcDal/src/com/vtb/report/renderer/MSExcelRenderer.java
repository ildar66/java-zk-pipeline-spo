package com.vtb.report.renderer;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * MSExcel Renderer. Renders the content as an Excel file  
 * @author Kuznetsov Michael
 *
 */
public class MSExcelRenderer implements ReportRenderer {

	private byte[] repBytes;
	private String report;
	private String reportName;
	
	@Override
	public void render(HttpServletRequest request, HttpServletResponse response) throws IOException {
		writeToResponseStream("xls", "application/vnd.ms-excel", getReportBytes(), response);
	}

   @Override
    public String getReport() {
        return report;
    }

   /**
    * Sets the report as String 
    * @param report as String  
    */
   @Override
   public void setReport(String report) {
       this.report = report;
   }
	
   /**
	 * Gets the report as MSWord file 
	 * @return report in MSWord format  
	 */
	public byte[] getReportBytes() {
		return repBytes;
	}

	/**
	 * Sets the report as HTML file 
	 * @param report in HTML format  
	 */
	public void setReportBytes(byte[] repBytes) {
		this.repBytes = repBytes;
	}

	/**
	 * Writes file to servlet output stream.
	 * @param fileExtension file extension ("doc", "txt" etc.)
	 * @param fileName file name
	 * @param mimeType MIME type
	 * @param content file content
	 * @param response HttpServletResponse to write data
	 * @throws IOException 
	 */
	public void writeToResponseStream(String fileExtension, String mimeType, byte[] content, HttpServletResponse response) throws IOException {
         
        response.setContentType(mimeType);
        String fileName = (reportName == null) ? "report" : (reportName);

        fileName  = transformFileName(fileName) + "." + fileExtension;
        fileName  = "report." + fileExtension;
        //fileName = new String(fileName.getBytes("utf-8"), "CP1252");
        
        String attachment = "inline"; //isAttached(mimeType) ? "attachment" : "inline";
        response.setHeader("Content-Disposition", attachment + "; filename=\"" + fileName + "\"");

        response.getOutputStream().write(content);
        response.getOutputStream().flush();
        response.getOutputStream().close();
	}

   public static String transformFileName(String fileName) {
        final int FILENAME_LENGTH = 250;
        // обрежем имя файла
        fileName  = fileName.substring(0, (fileName.length() > FILENAME_LENGTH ? FILENAME_LENGTH : fileName.length()));
        // уберем некорректные символы
        fileName = removeIllegalCharacters(fileName); //.replace(8211, "_");
        return fileName; 
    }

	private static String removeIllegalCharacters(String name) {
	    StringBuilder sb = new StringBuilder("");
	    if (name != null) {
	        for(int i=0; i< name.length(); i++)
	            if (Character.isLetterOrDigit(name.charAt(i)) || legalExtraCharacter(name.substring(i,i+1)))
	                sb.append(name.charAt(i));
	            else sb.append(" ");
	    }
	    return sb.toString().replaceAll("(  )+", " ");
	}
	
	private static boolean legalExtraCharacter(String character) {
	    if (".".equals(character) 
	        || ",".equals(character)
	        || "(".equals(character)
	        || ")".equals(character)
	        || " ".equals(character)
	        || "-".equals(character)
	        || "_".equals(character)
	       )
	       return true;
	    return false;
	}
	
	@Override
    public String getReportName() {
        return reportName;
    }

	@Override
    public void setReportName(String reportName) {
        this.reportName = reportName;
    }
}
