package com.vtb.util.report;

import java.io.StringReader;
import java.util.Map;
import java.util.logging.Level;

import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerException;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.w3c.dom.Element;

import ru.masterdm.templatetransformer.TemplateTransformFactory;
import ru.masterdm.templatetransformer.core.ITemplateTransform;
import ru.masterdm.templatetransformer.list.ETemplateTransform;

import com.vtb.domain.ReportTemplate;
import com.vtb.domain.Task;
import com.vtb.exception.MappingException;
import com.vtb.exception.NoSuchObjectException;
import com.vtb.mapping.ReportTemplateMapper;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.ReportBuilderActionProcessor.ReportTemplateParams;
import com.vtb.model.TaskActionProcessor;
import com.vtb.report.renderer.HTMLRenderer;
import com.vtb.report.renderer.ReportRenderer;

/**
 * A base class for building a PrintReports reports 
 * @author Michael Kuznetsov
 */
public class BasePrintReportBuilder extends AbstractReportBuilder {
 
	private final Long TEMPLATE_ID = -1L;  // use different template than for ordinary reports
	protected Long mdTaskId; // report parameter
	private final String rootElement = "task";
	protected Task task; 
	private boolean dynamicEncoding;
	protected boolean viewSource;

	public BasePrintReportBuilder(String reportName, ReportTemplateMapper reportMapper, boolean dynamicEncoding) throws Exception {
		super(reportName, reportMapper);
		this.dynamicEncoding = dynamicEncoding;
		//this.mapper = mapper;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	protected Long getTemplateId() {
		return TEMPLATE_ID;
	}
	
	/**
	 * Gets a CSS template (from the database or from other source)
	 * @throws MappingException 
	 * @throws NoSuchObjectException 
	 */
	@Override
	protected String getCSSTemplate() throws NoSuchObjectException, MappingException {		
		ReportTemplate reportTemplate = new ReportTemplate(TEMPLATE_ID); 
		ReportTemplate found = reportMapper.findByPrimaryKey(reportTemplate);
		return found.getText();
	}
	
	/**
	 * {@inheritDoc}
	 */	
	@SuppressWarnings("unchecked")
	@Override
	protected void getReportParameters(Map parameters) {
		String mdTaskIdStr = getParameter(parameters, ReportTemplateParams.MDTASK_ID.getValue());
		mdTaskId = (mdTaskIdStr != null) ? Long.parseLong(mdTaskIdStr) : new Long(-1L);
		String sourceStr = getParameter(parameters, ReportTemplateParams.SOURCE.getValue());
		viewSource = (sourceStr != null) ? true  : false;
	}	
	
	/**
	 * find Task information in the database by task id, if found
	 * null, if not found
	 * @param  taskId task identifier
	 */	
	protected Task findTaskById(Long taskId, boolean full) throws MappingException {
		try {
    	    TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
    		return processor.getReportData(new Task(taskId));
		} catch (Exception e) {
		    LOGGER.info("not found in the database Task with taskId:" + taskId);
		    return null;
		}
	}

    protected Task getData(boolean full) throws MappingException {
        if ((mdTaskId == null) || (mdTaskId.longValue() == 0) || (mdTaskId.longValue() == -1)) {
        	return null;
        } else {
        	return findTaskById(mdTaskId, full);
        }
    }

	/**
     * {@inheritDoc}
     * @throws MappingException 
     */ 
    @Override
    protected void getData() throws MappingException {
        task = findTaskById(mdTaskId, true);        
    }


	/**
     * {@inheritDoc}
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws MappingException 
     */
    @Override
    protected Element dataToXML() throws ParserConfigurationException, IllegalArgumentException, IllegalAccessException, TransformerException, MappingException {
        // collects all data and generates XML DOM.
        return task.toXML(document, rootElement);
    }

	/**
     * Special case for encoding processing
     * {@inheritDoc}
     * @throws MappingException 
     */
	@SuppressWarnings("unchecked")
    @Override
    public ReportRenderer buildReport(Map parameters) throws Exception {
        String template;
        ReportTemplate rptTmpl;
        try {
            // get data for the report
            // get template
            rptTmpl =  getTemplate(getTemplateId());
            template = mixIntoTemplate(rptTmpl.getText());
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

            // look at the results in DEBUG 
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
     * Searhes searchStr in original string and inserts strToInsert before it.  
     * @param original  -- original string
     * @param searchStr  -- search string
     * @param strToInsert  -- string to insert
     * @return new string, original string if any error happened (searchStr not found etc).
     */
	protected String insertBefore (String original, String searchStr, String strToInsert) {
        try {
            int idx = original.indexOf(searchStr);
            return original.substring(0, idx) + strToInsert + original.substring(idx, original.length());
        } catch (Exception e) {
            return original;  
        }    
    }

    /**
     * Mix in the ecnoding into the tempalte, if dynamicEncoding is set and no xsl:output method is present in the template. 
     * @param template original template.
     * @return modyfied tempalte, if dynamicEncoding is set. Original template otherwise.
     */
    protected String mixIntoTemplate(String template) {
        if (template == null) return null; 
        // Only when output method is not found.
        if (template.indexOf("<xsl:output") == -1) {
            // set encoding
            String encoding = dynamicEncoding ? AbstractReportBuilder.getEncoding() : "utf-8";
            String insertString = 
                "<xsl:output method=\"html\" encoding=\"" + encoding + "\" indent=\"yes\"/>";
            // Insert xsl:output string
            return insertBefore(template, "<xsl:template", insertString);
        } else return template;
    }
}


