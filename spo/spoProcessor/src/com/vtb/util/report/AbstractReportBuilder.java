package com.vtb.util.report;

import java.io.StringReader;
import java.rmi.RemoteException;
import java.util.ArrayList;
import java.util.Map;
import java.util.StringTokenizer;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.CDATASection;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;

import ru.masterdm.templatetransformer.TemplateTransformFactory;
import ru.masterdm.templatetransformer.core.ITemplateTransform;
import ru.masterdm.templatetransformer.list.ETemplateTransform;

import com.vtb.domain.ReportTemplate;
import com.vtb.exception.MappingException;
import com.vtb.exception.NoSuchObjectException;
import com.vtb.mapping.ReportTemplateMapper;
import com.vtb.report.renderer.HTMLRenderer;
import com.vtb.report.renderer.ReportRenderer;


/**
 * Abstract class for report building [based on XSLT transformation] 
 * @author Michael Kuznetsov
 *
 */
public abstract class AbstractReportBuilder {

	protected Logger LOGGER;	
	protected DocumentBuilder documentBuilder;
    protected ReportTemplateMapper reportMapper;   		  // mapper for ReportTemplate access.
    protected String reportName;   		  // mapper for ReportTemplate access.
    protected final String rootElement = "data";
    protected Document document;

	/**
     * Constructor.    
	 * @throws Exception 
     */
	public AbstractReportBuilder(String reportName, ReportTemplateMapper reportMapper) throws Exception {
	    this.reportMapper = reportMapper;
	    this.reportName = reportName;
		init();		
		LOGGER = Logger.getLogger(AbstractReportBuilder.class.getName());
	}	
	
	/**
	 * Builds a report
	 * @param parameters list of parameters (query String fo HTTP GET method: ?param1=value1 etc.)
	 * @param response TODO
	 * @return generated report as a String
	 */
	@SuppressWarnings("unchecked")
	public ReportRenderer buildReport(Map parameters) throws Exception {
		String template;
		ReportTemplate rptTmpl; 
		try {
			// get data for the report
			// get template
		    rptTmpl = getTemplate(getTemplateId());
		    template = rptTmpl.getText();
			// get report parameters
			getReportParameters(parameters);		
			// get report data with the found parameters
			getData();
			// builds an XML from the data and template
			document = documentBuilder.newDocument();
			Element dataElement = dataToXML(); 
			document.appendChild(dataElement);

			// adds CSS style;			
			addElementWithCDATA(document, dataElement, "style", getCSSTemplate());			

			// look at the results (DEBUG) 
			org.dom4j.io.DOMReader reader = new org.dom4j.io.DOMReader();
	        org.dom4j.Document result = reader.read(document);
	        String xml = result.asXML();
	        LOGGER.log(Level.FINE, xml);
		} catch (Exception e) {
			throw new Exception ("Error in getting data for XSLT transformation (report building, AbstractReportBuilder.buildReport)\n" +
					"    Nested exception is: " + e.getMessage(), e);			
		}
		try {
			// XSLT transformation
			ITemplateTransform<Source, Source, StreamResult> templateTransform = (ITemplateTransform<Source, Source, StreamResult>) TemplateTransformFactory.newInstance(ETemplateTransform.TRANSFORM_XSLT);
			StreamResult resultStream = new StreamResult();
			DOMSource dataDOMSource = new DOMSource(document);		
			resultStream = templateTransform.transform(new StreamSource(new StringReader(template)), dataDOMSource);
			String result = resultStream.getWriter().toString();

			// Create data for Renderer
			HTMLRenderer renderer = new HTMLRenderer();
			renderer.setReport(result);
			renderer.setReportName(rptTmpl.getName());
			return renderer;
		} catch (Exception e) {
			throw new Exception ("Error in XSLT transformation (report building, AbstractReportBuilder.buildReport)\n" +
					"    Nested exception is: " + e.getMessage(), e);
		}
	}
	
	/**
	 * Populates data for the report.
	 * @return database data
	 * @throws MappingException 
	 */
	abstract protected void getData() throws MappingException;

	/**
	 * Gets the parameters for this report from the Map (HTTP request attrributes map)
	 */
	@SuppressWarnings("unchecked")
	abstract protected void getReportParameters(Map parameters);
	
	/** 
	 * Get the template report id.
	 * @return
	 */
	abstract protected Long getTemplateId();
	
	/**
	 * Gets a report template (from the database or from other source)
	 * @param templateId report template id
	 * @throws MappingException 
	 * @throws NoSuchObjectException 
	 */
	protected ReportTemplate getTemplate(Long templateId) throws NoSuchObjectException, MappingException {		
	//	ReportTemplate reportTemplate = new ReportTemplate(templateId); 
		return reportMapper.findByFilename(reportName); 
	}
	
	/**
	 * Gets a CSS template (from the database or from other source)
	 * @throws MappingException 
	 * @throws NoSuchObjectException 
	 */
	protected String getCSSTemplate() throws NoSuchObjectException, MappingException {		
		ReportTemplate reportTemplate = new ReportTemplate(new Long (0L)); 
		ReportTemplate found = reportMapper.findByPrimaryKey(reportTemplate); 
		return found.getText(); 
	}
	
	/**
	 * Generates a data to be inserted in the template
	 * @throws TransformerException 
	 * @throws IllegalAccessException 
	 * @throws IllegalArgumentException 
	 * @throws ParserConfigurationException 
	 * @throws MappingException 
	 * @throws RemoteException 
	 */	
	protected abstract Element dataToXML() throws Exception;

	
	/**
	 * Gets a parameter from the requeest parameters Map.
	 * Attention! Always get only one value from the map. Not all the values of the same parameter
	 * @param parameters
	 */
	@SuppressWarnings("unchecked")
	protected String getParameter(Map parameters, String name) {
    	String[] result = (String[])parameters.get(name);
    	if (result != null) 
    		if (!result[0].equals("")) return result[0];
    		else return null;
    	else return null;
	}

	/**
     * Gets a parameter list from the request parameters Map.
     * @param parameters
     */
    @SuppressWarnings("unchecked")
    protected String[] getParameterList(Map parameters, String name) {
        String[] result = (String[])parameters.get(name);
        if (result != null) return result; 
        else return null;
    }

    /**
     * Performs document builder initialization.
     * @throws Exception 
     */
    private void init() throws Exception {
        try {
        	 DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
             dbf.setNamespaceAware(true);
             documentBuilder = dbf.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            throw new Exception("Error initializing ReportBuilder");
        }
    }

	/******************************************************************************************
	 *        Methods of a XML DOM building   
	 ******************************************************************************************/

    /**
     * Adds new element as root one.
     * @param document {@link Document}
     * @param elementName element name
     * @return created element
     */
    Element addElement(Document document, String elementName) {
        return addElement(document, null, elementName, null);
    }

    /**
     * Adds new element as child to existing one.
     * @param document {@link Document}
     * @param parent {@link Element}
     * @param elementName element name
     * @return created element
     */
    Element addNestedElement(Document document, Element parent, String elementName) {
        Element element = document.createElement(elementName);
        parent.appendChild(element);
        return element;
    }

    /**
     * Adds new element as child to existing one, that includes text node.
     * @param document {@link Document}
     * @param parent {@link Element}
     * @param elementName element name
     * @param elementText element text value
     * @return created element
     */
    Element addElement(Document document, Element parent, String elementName, String elementText) {
        Element element = document.createElement(elementName);
        if (elementText != null) {
            Text text = document.createTextNode(elementText);
            element.appendChild(text);
        }
        if (parent != null) {
            parent.appendChild(element);
        } else {
            document.appendChild(element);
        }
        return element;
    }

    /**
     * Adds new element with CDATA section as child to existing one.
     * @param document {@link Document}
     * @param parent {@link Element}
     * @param elementName element name
     * @param cdataText CDATA text
     * @return created element
     */
    Element addElementWithCDATA(Document document, Element parent, String elementName, String cdataText) {
        Element element = document.createElement(elementName);
        CDATASection cdata = document.createCDATASection(cdataText);
        element.appendChild(cdata);
        parent.appendChild(element);
        return element;
    }
    
    /**
     * Returns true if application is running on Windows, false otherwise.
     * @return true if application is running on Windows, false otherwise
     */
    public static boolean isWindows() {
        return System.getProperty("os.name").toLowerCase().indexOf("windows") != -1;
    }

    /**
     * Returns particular encoding depending on running operation system.
     * @return encoding
     */
    public static String getEncoding() {
        return "UTF-8";
    }

    /**
     * Get the numerated list of parameters (prefix0,prefix1,..., prefix123, ... ).
     * @param parameters Map of parameters (request)
     * @param key parameter name
     * @return null, if not found, List<String> otherwise
     * This method is used because of the bug: too many elements of the Map having the same key results in loss of some data.
     */
    @SuppressWarnings("unchecked")
    protected String[] getNumeratedParams(Map parameters, String key) {
        String[] curValue = (String[])parameters.get(key);
        String codeAsStr = "";
        if ((curValue != null) && (curValue[0] != null)) codeAsStr = curValue[0];
        // codeAsStr consist departments ids as one string, delimited by whitespace: "-1 0 1 2 3 12 38 "
        ArrayList<String> result = new ArrayList<String>();
        StringTokenizer tokenizer = new StringTokenizer(codeAsStr, " ");
        while (tokenizer.hasMoreTokens()) 
            result.add(tokenizer.nextToken());

        if (result.size() > 0)  {
            String[] returnValue = new String[result.size()];
            return (String[])(result.toArray(returnValue));
        }
        else return null;
    }
}
