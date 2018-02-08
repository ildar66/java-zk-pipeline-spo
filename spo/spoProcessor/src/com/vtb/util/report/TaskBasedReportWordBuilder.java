package com.vtb.util.report;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;

import com.vtb.model.ReportBuilderActionProcessor;
import ru.md.spo.dbobjects.FactPercentJPA;
import ru.md.spo.dbobjects.OperDecisionDescriptionJPA;
import ru.md.spo.dbobjects.OperDecisionJPA;
import ru.md.spo.dbobjects.PremiumJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.TaskFacadeLocal;

import com.aspose.words.Document;
import com.aspose.words.SaveFormat;
import com.vtb.domain.LimitTree;
import com.vtb.domain.OperationDecision;
import com.vtb.domain.Premium;
import com.vtb.domain.ReportTemplate;
import com.vtb.domain.Task;
import com.vtb.domain.Warranty;
import com.vtb.exception.FactoryException;
import com.vtb.exception.MappingException;
import com.vtb.exception.NoSuchObjectException;
import com.vtb.mapping.ReportTemplateMapper;
import com.vtb.report.renderer.MSWordRenderer;
import com.vtb.report.renderer.PlainTextRenderer;
import com.vtb.report.renderer.ReportRenderer;
import com.vtb.util.TaskInfoLimitTreeBuilder;
import com.vtb.util.report.utils.IfProcessor;
import com.vtb.util.report.utils.Task2ReportDataHelper;
import com.vtb.util.report.utils.TaskBasedReportGenerator;

/**
 * A class for building a generic MS Word reports based on Task data, with Limit/Sublimit structure. 
 * @author Michael Kuznetsov
 */

public class TaskBasedReportWordBuilder extends BasePrintReportBuilder {
    private boolean withSublimits = false;
    private ReportTemplate rptTmpl; 
    protected Map<String, String> extraParams = null; 
    
    /**
	 * Generic constructor. 
	 * @param reportName report Name
	 * @param reportMapper mapper
	 * @param dynamicEncoding encoding
	 * @throws Exception throws Exception 
	 */
	public TaskBasedReportWordBuilder(String reportName, ReportTemplateMapper reportMapper, boolean dynamicEncoding) throws Exception {
        super(reportName, reportMapper, dynamicEncoding);
    }

	/**
     * Get the BLOB data (report in the MSWord format) stored in the database.
     * @param templateId
     * @return
     * @throws NoSuchObjectException
     * @throws MappingException
     */
    private byte[] getDocPattern() throws NoSuchObjectException, MappingException {
        byte[] pattern = null; 
        try {
            rptTmpl = reportMapper.findByFilename(reportName);
        } catch (Exception e) {
            throw new MappingException("TaskBasedReportWordBuilder: row with name  \"" + reportName + "\" isn't found in the REPORT_TEMPLATE table");
        }
        if (rptTmpl == null)
        	throw new MappingException("TaskBasedReportWordBuilder: row with name  \"" + reportName + "\" isn't found in the REPORT_TEMPLATE table");
        pattern = rptTmpl.getDocPattern();
        // set whether process full hierarchy or not. 
        withSublimits = rptTmpl.isFullHierarchy();
        if (pattern == null)
           throw new MappingException("TaskBasedReportWordBuilder: row with name  \"" + reportName + "\" is null in the REPORT_TEMPLATE table");
        return pattern; 
    }
	
    /**
     * Get the BLOB data (report in the MSWord format) stored in the database.
     * @param templateId
     * @return
     * @throws NoSuchObjectException
     * @throws MappingException
     */
    private Map<String, String> getRussianNamesFields() {      
        try {
            return reportMapper.getRussianNamesFields();
        } catch (Exception e) {
            LOGGER.severe("TaskBasedReportWordBuilder: can't read russian names fields");
            return new HashMap<String, String>();
        }
    }
    
    /**
     * Find Limit\Sublimit hierarchy 
     * @throws MappingException 
     */
    private ArrayList<LimitTree> findLimitSublimitTree() throws MappingException {
        ArrayList<LimitTree> tree = new ArrayList<LimitTree>();
        // найдем информацию о сделке \ лимите \ сублимите 
        task = getData(false); 
        if (task.isOpportunity()) { 
            // сделка
            if (task.getInLimit() != null) {
                // иерархия лимита-сублимитов, на которые ссылается сделка.
                TaskInfoLimitTreeBuilder builder = new TaskInfoLimitTreeBuilder(task.getInLimit(), false);
                builder.makeOutput();
                tree = builder.getLimitTreeList();
            } else {
                // добавим саму сделку (не ссылается на лимиты \ сублимиты)
                tree.add(new LimitTree(task.getId_task()));
            }
        } else {
            // лимит или сублимит
            // TODO : нужно, чтобы принимали id, а не только номер в CRM.
            TaskInfoLimitTreeBuilder builder = new TaskInfoLimitTreeBuilder(task.getId_task(), false);
            //
            builder.makeOutput();
            tree = builder.getLimitTreeList();
        }
        return tree;
    }
    
    /**
     * Converts LimitTree tree to the list of Task objects. Retrieves full Task information 
     * @param tree LimitTree tree
     * @return list of Task objects
     * @throws MappingException 
     */
    private ArrayList<Task> convertToTasks(ArrayList<LimitTree> tree) throws MappingException {
        ArrayList<Task> taskTree = new ArrayList<Task>();
        for (LimitTree element : tree) { 
            Task task = findTaskById(element.getId_task(), true);
            if (task != null) taskTree.add(task);
        }
        return taskTree;
    }
    
    /**
     * Appends one aspose document (docToAdd) to the end of the existing document (as docStorage)  
     * @param docStorage document storage, to which the document will be added. 
     * @param docToAdd document to add
     * @return document witn appended document   
     * @throws Exception
     */
    protected byte[] appendToDoc(byte[] docStorage, Document docToAdd) throws Exception {
    	try {
    		// read from storage
		    ByteArrayInputStream stream = new ByteArrayInputStream(docStorage);
		    Document doc = new Document(stream);
		    stream.close();
		    
		    // append documents
		    Task2ReportDataHelper.appendDoc(doc, docToAdd);
		    
		    // save to the intermediate docStorage (Otherwise have trouble with DocumentBuilder)
		    ByteArrayOutputStream out = new ByteArrayOutputStream();                
		    doc.save(out, SaveFormat.DOC);  //(viewSource) ? SaveFormat.TEXT : 
		    out.close();
		    return out.toByteArray();
	    } catch (Exception e) {
	        LOGGER.log(Level.SEVERE, e.getMessage(), e);
	        throw new Exception ("Error in appending documents (report building, TaskBasedReportWordBuilder.buildReport)\n" +
	                "    Nested exception is: " + e.getMessage(), e);
	    }
    }
    /**
     * Special case for SignatureReport
     * @throws MappingException throws MappingException
     */
    @SuppressWarnings({ "rawtypes" })
    private ReportRenderer buildSignatureReport(Map parameters) throws Exception {
        if(extraParams==null)
            extraParams = new HashMap<String, String>();
        try {
            // get report parameters
            getReportParameters(parameters);
        } catch (Exception e) {
            throw new Exception ("Error in getting data for REPORT transformation (report building, TaskBasedReportWordBuilder.buildReport)\n" +
                    "    Nested exception is: " + e.getMessage(), e);
        }

        try {
            // Aspose transformation of the first element in the tree (it's limit or opportunity)
            byte[] pattern = getDocPattern();
            LOGGER.log(Level.INFO, "TaskBasedReport generation of the report " + reportName + " started");

            // create report for the only element (task in mdtask)
            task = getData(true);
            Document doc = asposeTransformation(task, pattern, TaskBasedReportGenerator.PROCESS_DOC, extraParams);

            ByteArrayOutputStream out = new ByteArrayOutputStream();
            doc.save(out, SaveFormat.DOCX);
            out.close();
            byte[] docStorage = out.toByteArray();

            MSWordRenderer renderer = new MSWordRenderer();
            renderer.setReportBytes(docStorage);
            renderer.setReportName(rptTmpl.getName());
            return renderer;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new Exception ("Error in Aspose.Words.Transformation (report building, TaskBasedReportWordBuilder.buildReport)\n" +
                    "    Nested exception is: " + e.getMessage(), e);
        }
    }

	/**
     * Special case for encoding processing
     * {@inheritDoc}
     * @throws MappingException throws MappingException  
     */
	@SuppressWarnings({ "rawtypes" })
    @Override
    public ReportRenderer buildReport(Map parameters) throws Exception {
        if(reportName!=null && reportName.equals("signature_report")){
            return buildSignatureReport(parameters);
        }
        ArrayList<Task> limitSublimitOpportunityTree = null;
        if(extraParams==null)
    		extraParams = new HashMap<String, String>();
	    try {
            // get report parameters
            getReportParameters(parameters);        
            
            // get report data with the found parameters
            if ((mdTaskId == null) || (mdTaskId.longValue() == 0) || (mdTaskId.longValue() == -1)) {
            	task = null; withSublimits = false;
            } else {
	            limitSublimitOpportunityTree = convertToTasks(findLimitSublimitTree());
	            if (limitSublimitOpportunityTree.size() == 0) {  
	                task = getData(true);
	                limitSublimitOpportunityTree.add(task);
	//                throw new Exception ("Error in getting data for REPORT transformation (report building, TaskBasedReportWordBuilder.buildReport)\n" +
	//                                                 "Cannot build LimitSublimitOpportunity hierarchy");
		        }
            }
        } catch (Exception e) {
            throw new Exception ("Error in getting data for REPORT transformation (report building, TaskBasedReportWordBuilder.buildReport)\n" +
                    "    Nested exception is: " + e.getMessage(), e);
        }
        
        try {
            // Aspose transformation of the first element in the tree (it's limit or opportunity)
        	byte[] pattern = getDocPattern();
        	byte[] docStorage = null;
            LOGGER.log(Level.INFO, "TaskBasedReport generation of the report " + reportName + " started");
            
            // create header of the document
            Document doc = asposeTransformation(null, pattern, TaskBasedReportGenerator.PROCESS_HEADER, extraParams);
            if (doc != null) {
	            // save doc to the intermediate docStorage (Otherwise have trouble with DocumentBuilder)
	            ByteArrayOutputStream out = new ByteArrayOutputStream();                
	            doc.save(out, SaveFormat.DOC);  //(viewSource) ? SaveFormat.TEXT : 
	            out.close();
	            docStorage = out.toByteArray();
            }

            if(task.isLimit()){
            	extraParams.put("limit_part", "1");
            }
            if (withSublimits)
                // create report for the 1st element in the list (limit)
                doc = asposeTransformation(limitSublimitOpportunityTree.get(0), pattern, TaskBasedReportGenerator.PROCESS_DOC, extraParams);
            else {
                // create report for the only element (task in mdtask)
                task = getData(true);
                if(task.isOpportunity()){
                	extraParams.put("opp_part", "1");
                }
                doc = asposeTransformation(task, pattern, TaskBasedReportGenerator.PROCESS_DOC, extraParams);
            }

            if (docStorage != null) {
            	docStorage = appendToDoc(docStorage, doc);
            } else {
            	// header is null. Don't need to append to empty document
            	// save doc to the intermediate docStorage (Otherwise have trouble with DocumentBuilder)
	            ByteArrayOutputStream out = new ByteArrayOutputStream();                
	            doc.save(out, SaveFormat.DOC);  //(viewSource) ? SaveFormat.TEXT : 
	            out.close();
	            docStorage = out.toByteArray();
            }
            if(task.getSupply().getWarranty()!=null)
            	for(Warranty warranty : task.getSupply().getWarranty()){
            		Document warrantyDocument = asposeTransformationW(warranty, pattern, TaskBasedReportGenerator.PROCESS_DOC, extraParams);
            		if (warrantyDocument != null) docStorage = appendToDoc(docStorage, warrantyDocument);
            	}
            
            if(task.isOpportunity())
            	extraParams.put("opp_part", "2");
            else
            	extraParams.put("limit_part", "2");
            doc = asposeTransformation(task, pattern, TaskBasedReportGenerator.PROCESS_DOC, extraParams);
            if (doc != null) docStorage = appendToDoc(docStorage, doc);

            
            // generating document for sublimits (or opportunities inside the sublimit) and adding it to the main document. 
            if (withSublimits && (limitSublimitOpportunityTree.size() > 1)) {
            	for (int i = 1; i < limitSublimitOpportunityTree.size(); i++) {
            		Task sublimit = limitSublimitOpportunityTree.get(i);
            		extraParams.put("limit_part", "1");
            		Document sublimitDocument = asposeTransformation(sublimit, pattern, TaskBasedReportGenerator.PROCESS_DOC, extraParams);
            		if (sublimitDocument != null) docStorage = appendToDoc(docStorage, sublimitDocument);
            		if(sublimit.getSupply().getWarranty()!=null)
            			for(Warranty warranty : sublimit.getSupply().getWarranty()){
            				Document warrantyDocument = asposeTransformationW(warranty, pattern, TaskBasedReportGenerator.PROCESS_DOC, extraParams);
            				if (warrantyDocument != null) docStorage = appendToDoc(docStorage, warrantyDocument);
            			}
            		extraParams.put("limit_part", "2");
            		sublimitDocument = asposeTransformation(sublimit, pattern, TaskBasedReportGenerator.PROCESS_DOC, extraParams);
            		if (sublimitDocument != null) docStorage = appendToDoc(docStorage, sublimitDocument);
            	}
            }
            
            // adds document with empty task to generate footer (text without any data from the limit\sublimit\opportunity)
            Document noFildsDocument = asposeTransformation(null, pattern, TaskBasedReportGenerator.PROCESS_FOOTER, extraParams);
            if (noFildsDocument != null) docStorage = appendToDoc(docStorage, noFildsDocument);
            
            LOGGER.log(Level.INFO, "TaskBasedReport generation of the report " + reportName + " finished successfully");

            // save result to stream
            if (!viewSource) {
                // read from storage
                ByteArrayInputStream stream = new ByteArrayInputStream(docStorage);
                doc = new Document(stream);
                stream.close();
                
                IfProcessor.joinParagraphsWithNonBreakingEx(doc);
                
                IfProcessor.joinMarkedSections(doc);

                //обработка конструкций IF-THEN-ELSE-ENDIF
                IfProcessor.parseIfEx(doc);

                // еще раз уберем пустые строки (могли остаться после append документов) 
                Task2ReportDataHelper.removeEmptyParagraphs(doc);
                
                MSWordRenderer renderer = new MSWordRenderer();
	            ByteArrayOutputStream out = new ByteArrayOutputStream();                
                doc.save(out, SaveFormat.DOC);
                out.close();
                renderer.setReportBytes(out.toByteArray());
                renderer.setReportName(rptTmpl.getName());
                return renderer;
            } else {
                // show source instead of applying a transformation.
                PlainTextRenderer renderer = new PlainTextRenderer();
	            ByteArrayOutputStream out = new ByteArrayOutputStream();                
                doc.save(out, SaveFormat.TEXT);
                out.close();
                renderer.setReportBytes(out.toByteArray());
                renderer.setReportName(rptTmpl.getName());
                return renderer;                
            }
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            throw new Exception ("Error in Aspose.Words.Transformation (report building, TaskBasedReportWordBuilder.buildReport)\n" +
                    "    Nested exception is: " + e.getMessage(), e);
        }
    }

	/**
	 * Aspose Transformation of the document (filling the document pattern with data of the task)
	 * @param task  task to fill the document
	 * @param pattern  document pattern
	 * @param mode whether to process header, footer or the document itself
	 * @param extraParams extra params key-value, passed to use in template transformation
	 */
	private Document asposeTransformation(Task task, byte[] pattern, int mode, Map<String, String> extraParams) throws IOException {
		mixInData(task);
		ByteArrayInputStream stream = new ByteArrayInputStream(pattern);
		// just for testing (set variables in runtime in debugger)
		boolean testSEEnvironment = false; 
		boolean testViewSource = false;
		
		Document doc = null;
		if (testSEEnvironment) {
			// test (in runtime)
			TaskBasedReportGenerator.startItSE(task, getRussianNamesFields(), viewSource, testViewSource, extraParams);
		} else {
			doc = TaskBasedReportGenerator.startItWebSphere(task, stream, getRussianNamesFields(), 
					viewSource, false, mode, extraParams);
		}
		stream.close();
		return doc;
	}
	/**
	 * Aspose Transformation of the document (filling the document pattern with data of the task)
	 * @param task  task to fill the document
	 * @param pattern  document pattern
	 * @param mode whether to process header, footer or the document itself
	 * @param extraParams extra params key-value, passed to use in template transformation
	 */
    private Document asposeTransformationW(Warranty warranty, byte[] pattern, int mode, Map<String, String> extraParams) throws IOException {
    	ByteArrayInputStream stream = new ByteArrayInputStream(pattern);
        Document doc = null;
        doc = TaskBasedReportGenerator.startItWebSphere(warranty, stream, getRussianNamesFields(), viewSource, false, mode, extraParams);
        stream.close();
        return doc;
    }
    
    /**
     * Используем этот изврат для чего? для того, чтобы начитать в Task данные из TaskJPA, которые дорогой Андрей не читает в Task
     * Он использует два метода начитывания сразу: через JDBC и через JPA.
     * upd: я использую три метода. Еще myBatis. Такое архитектурное решение принял Сергей Сергеевич при поддержке Жогина.
     * Они осознают, что это усложняет разработку.
     * @param task
     */
    private void mixInData(Task task) {
    	if (task == null) return;
    	LOGGER.log(Level.INFO, task.toString());
    	LOGGER.log(Level.INFO, task.getId_task().toString());
    	//подмешиваем требуемые данные 
    	try {
			TaskFacadeLocal taskFacade = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
			if (taskFacade == null) throw new NullPointerException("taskFacade is null");
			TaskJPA taskJPA = taskFacade.getTask(task.getId_task());

			// подмешиваем значения для полей Вознаграждения 
				 //task.getTaskProcent().setPremiumTypeDisplay(taskJPA.getPremiumList());
				 //task.getTaskProcent().setPremiumSizeDisplay(taskJPA.getPremiumSizeDisplay());
			ArrayList<Premium> list = new ArrayList<Premium>();
			for (PremiumJPA jpa : taskJPA.getPremiumList()) {
				list.add(
					new Premium(jpa.getPremiumType().getPremium_name(), 
								jpa.getPremiumType().getValue(),
								jpa.getVal(),
								jpa.getCurr(), 
								jpa.getText()));
			}
			task.setPremiumList(list);
			
			// подмешиваем значения для поля "Источник формирования покрытия для осуществления платежа по аккредитиву"
			if (taskJPA.getAcredetivSourcePayment() != null) {
				 task.getMain().setAcredetivSourcePayment(taskJPA.getAcredetivSourcePayment().getNameSource());
			}

			// подмешиваем тип Документарный для сублимита или сделки
			task.getMain().isDocumentary = taskJPA.isDocumentary();
			
			// подмешиваем индикативную ставку.
			task.setInd_rate(taskJPA.getInd_rate());
			
			
			String operationDecisionList = " ";  
			if ((taskJPA.getOperDecision() != null) && (!taskJPA.getOperDecision().isEmpty())) {
				for (OperDecisionJPA jpa : taskJPA.getOperDecision()) {
					String element = "";
					if ((jpa.getDescriptions() != null) && (!jpa.getDescriptions().isEmpty())) {
						element += "Решение о/об:\n";
						for(OperDecisionDescriptionJPA decision : jpa.getDescriptions()) 
							element += "- "+decision.getDescr()+"\n";
					}
					if(!jpa.getAccepted().isEmpty())
						element += "принимаются\n"+jpa.getAccepted();
					element += jpa.getSpecials();
					operationDecisionList += element + "\n\n";
				}
			}
			task.setOperationDecisionList(operationDecisionList);
			
			// подмешаем поле title.
			
			task.getHeader().setTitle(taskJPA.getTitleReport());
			
			// подмешаем поле Индивидульаные условия для первой записи фактических значений...
			if ((task.getFactPercentList() != null) && (!task.getFactPercentList().isEmpty())) {
				if ((taskJPA.getFactPercents() != null) && (!taskJPA.getFactPercents().isEmpty())) {
					for (FactPercentJPA per : taskJPA.getFactPercents()) {
						if(per.getTranceId() ==null) {
							task.getFactPercentList().get(0).setIndcondition(per.getIndcondition());
							break;
						}
					}
				}
			}
			
			
    	} catch (FactoryException e) {
			LOGGER.log(Level.SEVERE, e.getMessage(), e);
		}
    }
}


