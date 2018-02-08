package org.uit.director.servlet;

import java.io.BufferedInputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;

import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.vtb.util.ApplProperties;

/**
 * Sends report file from file system to output stream.
 * @author achalov
 */
public class DownloadReportServlet extends HttpServlet {
	private static final long serialVersionUID = 1L;
	
	private static HashMap<String, String> mimeTypes = new HashMap<String, String>();
	
	private static String path = ApplProperties.getReportsPath();

	
	static {
		mimeTypes.put("word", "application/msword");
		mimeTypes.put("excel", "application/vnd.ms-excel");
		mimeTypes.put("default", "application/octet-stream");

		path += (path.endsWith("\\") || path.endsWith("/")) ? "" : System.getProperty("file.separator");
	}

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		String fileName = request.getParameter("reportFile");
		String fileType = request.getParameter("fileType");

		response.setContentType(getContentType(fileType));
		response.addHeader("Content-Disposition", "attachment; filename=" + fileName);
		
	    ServletOutputStream sos = response.getOutputStream();
	    BufferedInputStream bis = new BufferedInputStream(new FileInputStream(
	    	  path + fileName
	    ));

	    /* set read buffer to 4k */
        byte[] buffer = new byte[4096];
        int bytesRead = 0;
	    while((bytesRead = bis.read(buffer)) != -1) {
	    	sos.write(buffer, 0, bytesRead);
	    }
	    bis.close();
	    sos.close();
	}

	/**
	 * Detects report MIME type.
	 * @param fileType file type string
	 * @return MIME type string
	 */
	private String getContentType(String fileType) {
		String mimeType = mimeTypes.get(fileType.toLowerCase());
		if (mimeType == null) {
			mimeType = mimeTypes.get("default");
		}
		return mimeType;
	}
}
