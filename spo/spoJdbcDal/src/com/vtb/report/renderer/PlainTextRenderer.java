package com.vtb.report.renderer;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * PlainText Word Renderer. Renders the content as a TXT file  
 * @author Kuznetsov Michael
 *
 */
public class PlainTextRenderer extends MSWordRenderer {

    @Override
	public void render(HttpServletRequest request, HttpServletResponse response) throws IOException {
		writeToResponseStream("txt", "plain/text", getReportBytes(), response);
	}
}
