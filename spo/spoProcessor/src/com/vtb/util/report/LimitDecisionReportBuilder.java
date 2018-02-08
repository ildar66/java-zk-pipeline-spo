package com.vtb.util.report;

import java.io.StringReader;
import java.util.ArrayList;
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

import com.vtb.domain.LimitTree;
import com.vtb.domain.Task;
import com.vtb.exception.MappingException;
import com.vtb.mapping.ReportTemplateMapper;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.ReportBuilderActionProcessor.ReportTemplateReportName;
import com.vtb.model.TaskActionProcessor;
import com.vtb.report.renderer.HTMLRenderer;
import com.vtb.report.renderer.ReportRenderer;

/**
 * A class for building a LimitDecision reports with Limit/Sublimit structure. 
 * @author Michael Kuznetsov
 */
public class LimitDecisionReportBuilder extends BasePrintReportBuilder {
    private final String rootElement = "limitDecisionDoc";
    private ArrayList<Task> sublimits = new ArrayList<Task>();
    
	public LimitDecisionReportBuilder(ReportTemplateMapper reportMapper, boolean dynamicEncoding) throws Exception {
		super(ReportTemplateReportName.LIMIT_DECISION.getValue(), reportMapper, dynamicEncoding);
	}
	
	/**
     *  Gets sublimits of this limit (if this is limit!)
     */ 
    private void getSublimits() throws MappingException {
        if (task.getHeader().isLimit()) {
            TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
            for (LimitTree sublimit: task.getMain().getLimitTreeList()) {
                Task sublimitTask = processor.getTask(new Task(Long.parseLong(sublimit.getReferenceId())));
                sublimits.add(sublimitTask);    
            }
        }
    }
    
	
    /**
     * Transforms data (VtbObject) to XML
     * @throws IllegalAccessException 
     * @throws IllegalArgumentException 
     * @throws MappingException 
     */
    private Element dataToXML(Task task, String rootElement) throws ParserConfigurationException, IllegalArgumentException, IllegalAccessException, TransformerException, MappingException {
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
        com.vtb.domain.ReportTemplate rptTmpl;
        try {
            // get data for the report
            // get template
            rptTmpl = getTemplate(getTemplateId());
            template = mixIntoTemplate(rptTmpl.getText());
            // get report parameters
            getReportParameters(parameters);        
            // get report data with the found parameters
            getData();
            getSublimits();
            
            // builds an XML from the data and template
            document = documentBuilder.newDocument();
            Element rootElementNode = addElement(document, rootElement); 
            Element limitElement = addNestedElement(document, rootElementNode, "limit");
            Element limitTaskElement = dataToXML(task, "task");
            limitElement.appendChild(limitTaskElement);
            
            if (sublimits.size() > 0) {
                Element sublimitsElement = addNestedElement(document, rootElementNode, "sublimits");                
                for (Task sublimit : sublimits) {
                    Element sublimitElement = addNestedElement(document, sublimitsElement, "sublimits-element");
                    Element sublimitTask = dataToXML(sublimit, "task");
                    sublimitElement.appendChild(sublimitTask);
                }
            }
            
            // adds CSS style;          
            addElementWithCDATA(document, rootElementNode, "style", getCSSTemplate());          

            // look at the results in the debug 
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
            throw new Exception ("Error in XSLT transformation (report building, LimitDecisionReportBuilder.buildReport)\n" +
                    "    Nested exception is: " + e.getMessage(), e);
        }
    }
}


