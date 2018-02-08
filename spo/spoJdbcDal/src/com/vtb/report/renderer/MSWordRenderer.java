package com.vtb.report.renderer;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * MSWord Renderer. Renders the content as an HTML file  
 * @author Kuznetsov Michael
 *
 */
public class MSWordRenderer implements ReportRenderer {

	private byte[] repBytes;
	private String report;
	private String reportName;
	private String reportExtension;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public void render(HttpServletRequest request, HttpServletResponse response) throws IOException {
		writeToResponseStream("doc", "applicaton/msword", getReportBytes(), response);
	}

   /**
     * {@inheritDoc}
     */
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
	 * Sets report binary bytes  
	 * @param repBytes binary bytes  
	 */
	public void setReportBytes(byte[] repBytes) {
		this.repBytes = repBytes;
	}

	/**
	 * Writes file to servlet output stream.
	 * @param fileExtension file extension ("doc", "txt" etc.)
	 * @param mimeType MIME type
	 * @param content file content
	 * @param response HttpServletResponse to write data
	 * @throws IOException exception
	 */
	public void writeToResponseStream(String fileExtension,String mimeType, byte[] content, HttpServletResponse response) throws IOException {
         
	    response.setCharacterEncoding("utf-8");
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

	/**
	 * Transforms file name
	 * @param fileName file name to transform
	 * @return transformed file name
	 */
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
	
	/**
	 * {@inheritDoc}
	 */
	@Override
    public String getReportName() {
        return reportName;
    }

    /**
     * {@inheritDoc}
     */
	@Override
    public void setReportName(String reportName) {
        this.reportName = reportName;
    }

	/**
	 * Gets real file extension, if exists. Is used in TaskBasedJoinDocumentsBuilder only.
	 * @return the reportExtension
	 */
	public String getReportExtension() {
		return reportExtension;
	}

	/**
	 * Sets real file extension, if exists. Is used only in TaskBasedJoinDocumentsBuilder
	 * @param reportExtension the reportExtension to set
	 */
	public void setReportExtension(String reportExtension) {
		this.reportExtension = reportExtension;
	}
}
