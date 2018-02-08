package com.vtb.util.report.utils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.springframework.beans.factory.annotation.Autowired;

import ru.masterdm.compendium.domain.crm.FloatPartOfActiveRate;
import ru.masterdm.compendium.model.CompendiumCrmActionProcessor;
import ru.masterdm.spo.utils.SBeanLocator;
import ru.md.domain.MdTask;
import ru.md.domain.TargetGroupLimit;
import ru.md.domain.TargetGroupLimitType;
import ru.md.persistence.MdTaskMapper;
import ru.md.spo.dbobjects.FactPercentJPA;
import ru.md.spo.dbobjects.IndrateMdtaskJPA;
import ru.md.spo.dbobjects.OperDecisionDescriptionJPA;
import ru.md.spo.dbobjects.OperDecisionJPA;
import ru.md.spo.dbobjects.TaskJPA;
import ru.md.spo.ejb.CrmFacadeLocal;
import ru.md.spo.ejb.PupFacadeLocal;
import ru.md.spo.ejb.TaskFacadeLocal;

import com.aspose.words.ConvertUtil;
import com.aspose.words.Document;
import com.aspose.words.DocumentBuilder;
import com.aspose.words.MailMerge;
import com.aspose.words.PageSetup;
import com.aspose.words.PaperSize;
import com.aspose.words.SaveFormat;
import com.aspose.words.ViewType;
import com.vtb.domain.FactPercent;
import com.vtb.domain.Fine;
import com.vtb.domain.Forbidden;
import com.vtb.domain.Task;
import com.vtb.domain.Warranty;
import com.vtb.util.Formatter;

public class TaskBasedReportGenerator {
    protected static final Logger LOGGER = Logger.getLogger(TaskBasedReportGenerator.class.getName());
    private static final String outputTaskFilePath = "C:/Temp/limit_decision.doc";
    private static final String inputTaskFilePath = "C:/Temp/limit_decision_template.doc";
    
    private static final String CONCL_BLAGO = "Заключение о благонадежности";
    private static final String CONCL_JURID = "Заключение юридическое";
    private static final String CONCL_RISK = "Заключение по рискам";
    private static final String CONCL_RESERV = "Заключение по резервированию";
    private static final String CONCL_SUPPLY = "Заключение по обеспечению";
    
    public static final int PROCESS_HEADER = 0;  // обработка секции HEADER
    public static final int PROCESS_DOC = 1;     // обработка самого документа
    public static final int PROCESS_FOOTER = 2;  // обработка секции FOOTER

//    public static void main(String[] args) throws Exception {
//        startItSE(new Task(), new TreeMap<String, String>(), false, true, new TreeMap<String, String>());
//    }

    /**
     * start report generation in the SE environment
     */
    public static void startItSE(Task task, Map<String, String> russianNameFields, boolean viewSource, boolean testData, Map<String, String> extraParams) {
//        //  set Aspose license
//        try {
//            new com.aspose.words.License().setLicense(TaskBasedReportGenerator.class.getClassLoader().getResourceAsStream("Aspose.Words.lic"));
//        } catch (Exception e) {
//            LOGGER.log(Level.SEVERE, "Can't set aspose.words.License() " + e.getMessage(), e);
//            return;
//        }
        
        //task.getHeader().setProcessType("лимит");
        //task.getHeader().setProcessType("ну типа сублимит");
        task.getHeader().setProcessType("сделка");
        
        // generate document
        Document doc = null;
        byte[] docStorage = null;
        try {
            if (viewSource) {
                doc = new Document();   
                doc.getFirstSection().getBody().removeAllChildren();
                PageSetup pageSetup = doc.getSections().get(0).getPageSetup();
                pageSetup.setPaperSize(PaperSize.CUSTOM);
                //pageSetup.setPageWidth(ConvertUtil.millimeterToPoint(1000));
    
                pageSetup.setLeftMargin(ConvertUtil.millimeterToPoint(25));
                pageSetup.setRightMargin(ConvertUtil.millimeterToPoint(15));
                pageSetup.setTopMargin(ConvertUtil.millimeterToPoint(15));
                pageSetup.setBottomMargin(ConvertUtil.millimeterToPoint(15));
            } else {
                doc = new Document(inputTaskFilePath);
            }
            LOGGER.log(Level.INFO, "TaskBasedReport generation started");
            task.getHeader().setProcessType("лимит");
            createTaskBasedReport(doc, task, russianNameFields, viewSource, testData, extraParams);

            // save document in the mediatory place
            ByteArrayOutputStream out = new ByteArrayOutputStream();                
            doc.save(out, SaveFormat.DOC);  //(viewSource) ? SaveFormat.TEXT : 
            out.close();
            docStorage = out.toByteArray();
            LOGGER.log(Level.INFO, "TaskBasedReport generation finished successfully");
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "TaskBasedReport generation failed. " + e.getMessage(), e);
            return;
        }
        
        // save document
        try {
            String fileName = outputTaskFilePath;
            File f = new File(fileName);
            if (f.exists()) {
                f.delete();
            }
            FileOutputStream fos = new FileOutputStream(fileName);
            if (viewSource) doc.save(fos, SaveFormat.TEXT);
            else doc.save(fos, SaveFormat.DOC);
            fos.close();
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "TaskBasedReport couldn't be saved. The reason: " + e.getMessage(), e);
            return;
        }
    }

    /**
     * 
     *
     *
     * @param task
     * @param stream
     * @param russianNameFields
     * @param viewSource
     * @param testData
     * @param mode 
     * @return
     */
    public static Document startItWebSphere(Task task, InputStream stream, Map<String, String> russianNameFields, 
    		boolean viewSource, boolean testData, int mode, Map<String, String> extraParams) {
    	try {
    		Document doc = null;
    		if (viewSource) {
    			doc = new Document();        
    			doc.getFirstSection().getBody().removeAllChildren();
    			PageSetup pageSetup = doc.getSections().get(0).getPageSetup();
    			pageSetup.setPaperSize(PaperSize.CUSTOM);
    			//pageSetup.setPageWidth(ConvertUtil.millimeterToPoint(1000));
    			
    			pageSetup.setLeftMargin(ConvertUtil.millimeterToPoint(25));
    			pageSetup.setRightMargin(ConvertUtil.millimeterToPoint(15));
    			pageSetup.setTopMargin(ConvertUtil.millimeterToPoint(15));
    			pageSetup.setBottomMargin(ConvertUtil.millimeterToPoint(15));
    		} else {
    			doc = new Document(stream);
    			// emulation of testing: read not from database, read from file
    			//doc = new Document(inputTaskFilePath);
    		}
    		
    		switch (mode) {
    		case PROCESS_HEADER: 
    			if (Task2ReportDataHelper.findMergeField(
    					doc, 
    					Task2ReportDataHelper.BLOCK_HEADER_PREFIX + "_" + Task2ReportDataHelper.STARTING_BLOCK_SUFFIX) == null)
    				// header is not found. 
    				return null;
    			break;
    		case PROCESS_DOC:
    			// nothing to check. 
    			break;
    		case PROCESS_FOOTER:
    			if (Task2ReportDataHelper.findMergeField(
    					doc, 
    					Task2ReportDataHelper.BLOCK_FOOTER_PREFIX + "_" + Task2ReportDataHelper.STARTING_BLOCK_SUFFIX) == null)
    				// footer is not found.  
    				return null; 
    			break;
    		}
    		
    		createTaskBasedReport(doc, task, russianNameFields, viewSource, testData, extraParams);
    		return doc;
    	} catch (Exception e) {
    		LOGGER.log(Level.SEVERE, "TaskBasedReport generation failed. " + e.getMessage(), e);
    		return null;
    	}
    }
    public static Document startItWebSphere(Warranty warranty, InputStream stream, Map<String, String> russianNameFields, 
    		                                boolean viewSource, boolean testData, int mode, Map<String, String> extraParams) {
        try {
            Document doc = new Document(stream);
            createWarrantyBasedReport(doc, warranty, russianNameFields, viewSource, testData, extraParams);
            return doc;
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "TaskBasedReport generation failed. " + e.getMessage(), e);
            return null;
        }
   }
    
    public static void createWarrantyBasedReport(Document doc, Warranty warranty, Map<String, String> russianNameFields, 
    		boolean viewSource, boolean test, Map<String, String> extraParams) throws Exception {
    	MailMerge mm = doc.getMailMerge();
    	DocumentBuilder builder = new DocumentBuilder(doc);
    	
    	// удалим лишние блоки документа и разметку для блоков.
    	Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_HEADER_PREFIX);
    	Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_FOOTER_PREFIX);
   		Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_LIMIT_PREFIX);
   		Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_OPPORTUNITY_PREFIX);
   		Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_SUBLIMIT_PREFIX);
   		Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_SUBLIMIT_CREDIT1_PREFIX);
   		Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_SUBLIMIT_CREDIT2_PREFIX);
   		Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_SUBLIMIT_DOCUMENT1_PREFIX);
   		Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_SUBLIMIT_DOCUMENT2_PREFIX);
   		Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_BOTH_LIMIT_SUBLIMIT_PREFIX);
   		Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_OPPORTUNITY_INLIMIT_PREFIX);
   		Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_OPPORTUNITY_NOT_INLIMIT_PREFIX);
   		Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_1OPPORTUNITY_PREFIX);
   		Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_2OPPORTUNITY_PREFIX);
   		Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_2LIMIT_PREFIX);
   		Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_1LIMIT_PREFIX);

   		//слить merge поля
   		List<String> simpleFieldsArray = new ArrayList<String>();
        List<Object> simpleFieldsValuesArray = new ArrayList<Object>();
        String[] fields = new String[1];
        for(String key : warranty.getReportMap().keySet()){
        	simpleFieldsArray.add(key);
            simpleFieldsValuesArray.add(warranty.getReportMap().get(key));
        }
        mm.execute(simpleFieldsArray.toArray(fields), simpleFieldsValuesArray.toArray());
        if(warranty.getFineList()!=null){
        	for(Fine f : warranty.getFineList()){
        		f.generateColumn2();
        		f.generateColumn3();
        	}
        	MailMergeDataSource rs = new MailMergeDataSource("fines");
        	rs.createColumns("f_", Fine.class);
        	rs.fillRows("f_", warranty.getFineList());
        	mm.executeWithRegions(rs);
        }
   		
   		Task2ReportDataHelper.removeAllOccurencesOfMergeField(doc, Task2ReportDataHelper.BLOCK_1OPPORTUNITY_PREFIX);
   		Task2ReportDataHelper.removeAllOccurencesOfMergeField(doc, Task2ReportDataHelper.BLOCK_2OPPORTUNITY_PREFIX);
   		Task2ReportDataHelper.removeAllOccurencesOfMergeField(doc, Task2ReportDataHelper.BLOCK_WARRANTY_PREFIX);
   		
        //обработка конструкций IF-THEN-ELSE-ENDIF
        IfProcessor.parseIfEx(doc);
   		
    	// Убираем пустые строки
    	Task2ReportDataHelper.removeEmptyParagraphs(doc);
    	
    	// разобьем  строки, где есть символ \n
    	doc.getRange().replace("\n", "" + com.aspose.words.ControlChar.LINE_BREAK_CHAR, false, false);
    	
    	// уберем пустые записи в таблицах
    	Task2ReportDataHelper.removeEmptyTableRows(doc, false);
    	Task2ReportDataHelper.removeEmptyTableRows(doc, true);
    	
    	// Установим ориентацию полей
    	doc.getViewOptions().setViewType(ViewType.PAGE_LAYOUT);
    }
    
    /**
     * Creates Task based MSWord report.
     */
    public static void createTaskBasedReport(Document doc, Task task, Map<String, String> russianNameFields, 
                                             boolean viewSource, boolean test, Map<String, String> extraParams) throws Exception {
        // if task is not loaded into the SPO, make a fictitious report or footer of the existent report.
        // if (task == null) task = new Task();

    	//Add a handler for the event that inserts HTML data fields and tranfers them into DOC format.
        HandleMergeFieldInsertHtml htmlHandler = new HandleMergeFieldInsertHtml();
        doc.getMailMerge().addMergeFieldEventHandler(htmlHandler);
        htmlHandler.fields.add("header_manager");
        htmlHandler.fields.add("taskStatusReturn_statusReturnText");
        htmlHandler.fields.add("commentList_comment_body");  // TODO : change, show right name
        
        htmlHandler.fields.add("conclusion_blago_simple");
        htmlHandler.fields.add("conclusion_juridical_simple");
        htmlHandler.fields.add("conclusion_risk_simple");
        htmlHandler.fields.add("conclusion_reserv_simple");
        htmlHandler.fields.add("conclusion_supply_simple");
        // TODO возможно, добавить из frame_attribute.jsp

        
        MailMerge mm = doc.getMailMerge();
        DocumentBuilder builder = new DocumentBuilder(doc);
        
        Task2ReportDataHelper h = null;
        if (test) h = new Task2ReportDataHelperTest(builder, task, viewSource);
        else h = new Task2ReportDataHelper(builder, task, viewSource);
        
        // удалим лишние блоки документа и разметку для блоков.
        Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_WARRANTY_PREFIX);
        if (task == null) {
        	// удалим все блоки, кроме header и footer.
        	Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_LIMIT_PREFIX);
        	Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_OPPORTUNITY_PREFIX);
        	Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_SUBLIMIT_PREFIX);
        	Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_SUBLIMIT_CREDIT1_PREFIX);
        	Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_SUBLIMIT_CREDIT2_PREFIX);
        	Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_SUBLIMIT_DOCUMENT1_PREFIX);
            Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_SUBLIMIT_DOCUMENT2_PREFIX);
        	Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_BOTH_LIMIT_SUBLIMIT_PREFIX);
        	// excessive, but leave it here in case if prefix names will change. 
            Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_OPPORTUNITY_INLIMIT_PREFIX);
            Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_OPPORTUNITY_NOT_INLIMIT_PREFIX);
        } else {
	        if (task.isLimit()) {
	        	if(extraParams.containsKey("limit_part")){
	        		LOGGER.info("================ limit_part="+extraParams.get("limit_part"));
	        		if(extraParams.get("limit_part").equals("1"))
	        			Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_2LIMIT_PREFIX);
	        		if(extraParams.get("limit_part").equals("2"))
	        			Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_1LIMIT_PREFIX);
	        	}
	        	Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_HEADER_PREFIX);
	            Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_FOOTER_PREFIX);
	        	Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_SUBLIMIT_PREFIX);
	        	Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_SUBLIMIT_CREDIT1_PREFIX);
	        	Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_SUBLIMIT_CREDIT2_PREFIX);
	        	Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_SUBLIMIT_DOCUMENT1_PREFIX);
	            Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_SUBLIMIT_DOCUMENT2_PREFIX);
	            Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_OPPORTUNITY_PREFIX);	            
	            // excessive, but leave it here in case if prefix names will change. 
	            Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_OPPORTUNITY_INLIMIT_PREFIX);
	            Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_OPPORTUNITY_NOT_INLIMIT_PREFIX);
	        } 
	        else if (task.isSubLimit()) {
	        	Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_HEADER_PREFIX);
	            Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_FOOTER_PREFIX);
	        	Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_LIMIT_PREFIX);
	            Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_OPPORTUNITY_PREFIX);
	            // excessive, but leave it here in case if prefix names will change. 
	            Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_OPPORTUNITY_INLIMIT_PREFIX);
	            Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_OPPORTUNITY_NOT_INLIMIT_PREFIX);

	            if (task.getMain().isDocumentary) {
	            	// считаем, что документарный
	            	Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_SUBLIMIT_CREDIT1_PREFIX);
    	            Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_SUBLIMIT_CREDIT2_PREFIX);
    	            if(extraParams.containsKey("limit_part")){
    	            	if(extraParams.get("limit_part").equals("1"))
    	            		Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_SUBLIMIT_DOCUMENT2_PREFIX);
    	            	if(extraParams.get("limit_part").equals("2"))
    	            		Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_SUBLIMIT_DOCUMENT1_PREFIX);
    	            }
	            } else {
	            	// считаем, что кредитный. Другие слувчаи пока не рассматриваем.
	            	Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_SUBLIMIT_DOCUMENT1_PREFIX);
    	            Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_SUBLIMIT_DOCUMENT2_PREFIX);
    	            if(extraParams.containsKey("limit_part")){
    	            	if(extraParams.get("limit_part").equals("1"))
    	            		Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_SUBLIMIT_CREDIT2_PREFIX);
    	            	if(extraParams.get("limit_part").equals("2"))
    	            		Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_SUBLIMIT_CREDIT1_PREFIX);
    	            }
	            }
	        } 
	        else if (task.isOpportunity()) {
	        	if(extraParams.containsKey("opp_part")){
	        		LOGGER.info("================ opp_part="+extraParams.get("opp_part"));
	        		if(extraParams.get("opp_part").equals("1"))
	        			Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_2OPPORTUNITY_PREFIX);
	        		if(extraParams.get("opp_part").equals("2"))
	        			Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_1OPPORTUNITY_PREFIX);
	        	}
	        	Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_HEADER_PREFIX);
	            Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_FOOTER_PREFIX);
	        	Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_LIMIT_PREFIX);
	            Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_SUBLIMIT_PREFIX);
	            Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_SUBLIMIT_CREDIT1_PREFIX);
	        	Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_SUBLIMIT_CREDIT2_PREFIX);
	        	Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_SUBLIMIT_DOCUMENT1_PREFIX);
	            Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_SUBLIMIT_DOCUMENT2_PREFIX);
	            Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_BOTH_LIMIT_SUBLIMIT_PREFIX);
	            if ((task.getInLimit() == null) || ("".equals(task.getInLimit().trim()))) 
	                // opportunity is not in limit/sublimit
	                // remove specific to opportunity in limit/sublimit section 
	                Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_OPPORTUNITY_INLIMIT_PREFIX);
	            else
	                // opportunity is in limit/sublimit
	                // remove specific to opportunity not in limit/sublimit section
	                Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_OPPORTUNITY_NOT_INLIMIT_PREFIX);
	        }
        }

        // Выполняем MailMerge. Сначала -- простые поля. Потом -- таблицы 
    	// Сначала получим все простые поля
        TreeMap<String, String> map = generateSimpleFields(builder, task, russianNameFields, viewSource, test, extraParams);
        h.exec(mm, map);
        
        if (task != null) {
        	//таблицы
	        generateTableData(russianNameFields, mm, h, map);
	        //снова простые поля для добавления счётчиков
	        h.exec(mm, map);
	        
	        // удалим разметку для блоков.
	        if (task.isLimit()) {
	            Task2ReportDataHelper.removeAllOccurencesOfMergeField(doc, Task2ReportDataHelper.BLOCK_LIMIT_PREFIX);
	            Task2ReportDataHelper.removeAllOccurencesOfMergeField(doc, Task2ReportDataHelper.BLOCK_BOTH_LIMIT_SUBLIMIT_PREFIX);
	            Task2ReportDataHelper.removeAllOccurencesOfMergeField(doc, Task2ReportDataHelper.BLOCK_2LIMIT_PREFIX);
	            Task2ReportDataHelper.removeAllOccurencesOfMergeField(doc, Task2ReportDataHelper.BLOCK_1LIMIT_PREFIX);
	        } else if (task.isSubLimit()) {
	            Task2ReportDataHelper.removeAllOccurencesOfMergeField(doc, Task2ReportDataHelper.BLOCK_SUBLIMIT_PREFIX);
	            Task2ReportDataHelper.removeAllOccurencesOfMergeField(doc, Task2ReportDataHelper.BLOCK_BOTH_LIMIT_SUBLIMIT_PREFIX);
	            Task2ReportDataHelper.removeAllOccurencesOfMergeField(doc, Task2ReportDataHelper.BLOCK_SUBLIMIT_CREDIT1_PREFIX);
	            Task2ReportDataHelper.removeAllOccurencesOfMergeField(doc, Task2ReportDataHelper.BLOCK_SUBLIMIT_CREDIT2_PREFIX);
	            Task2ReportDataHelper.removeAllOccurencesOfMergeField(doc, Task2ReportDataHelper.BLOCK_SUBLIMIT_DOCUMENT1_PREFIX);
	            Task2ReportDataHelper.removeAllOccurencesOfMergeField(doc, Task2ReportDataHelper.BLOCK_SUBLIMIT_DOCUMENT2_PREFIX);
	        } else if (task.isOpportunity()) {
	            Task2ReportDataHelper.removeAllOccurencesOfMergeField(doc, Task2ReportDataHelper.BLOCK_OPPORTUNITY_PREFIX);
	            Task2ReportDataHelper.removeAllOccurencesOfMergeField(doc, Task2ReportDataHelper.BLOCK_WARRANTY_PREFIX);
	            Task2ReportDataHelper.removeAllOccurencesOfMergeField(doc, Task2ReportDataHelper.BLOCK_1OPPORTUNITY_PREFIX);
	       		Task2ReportDataHelper.removeAllOccurencesOfMergeField(doc, Task2ReportDataHelper.BLOCK_2OPPORTUNITY_PREFIX);
	        }
        
	        removeEmptyTablesBlocks(doc);
	        
	        // Обновляем значения полей (аналогично нажатию клавиши F9 на клавиатуре)
	        doc.updateFields();
	
	        // Вычисляем значения IF-полей
	        Task2ReportDataHelper.updateIfFields(doc);
        }
        if (task == null) {
            Task2ReportDataHelper.removeAllOccurencesOfMergeField(doc, Task2ReportDataHelper.BLOCK_HEADER_PREFIX);
            Task2ReportDataHelper.removeAllOccurencesOfMergeField(doc, Task2ReportDataHelper.BLOCK_FOOTER_PREFIX);
        }

        //обработка конструкций IF-THEN-ELSE-ENDIF
        IfProcessor.parseIfEx(doc);
        	
        // Убираем пустые строки
        Task2ReportDataHelper.removeEmptyParagraphs(doc);
        
        // разобьем  строки, где есть символ \n
        doc.getRange().replace("\n", "" + com.aspose.words.ControlChar.LINE_BREAK_CHAR, false, false);
        
        // уберем пустые записи в таблицах
        Task2ReportDataHelper.removeEmptyTableRows(doc, false);
        Task2ReportDataHelper.removeEmptyTableRows(doc, true);
        
        //doc.Range.Replace("abcd", "ab" & Aspose.Words.ControlChar.LineBreak & "cd", False, False)
        // Установим ориентацию полей
        doc.getViewOptions().setViewType(ViewType.PAGE_LAYOUT);
    }


    /**
     * Generate table data
     * @param russianNameFields
     * @param mm Mailmerge
     * @param h Task2ReportDataHelper
     */
	private static void generateTableData(Map<String, String> russianNameFields, MailMerge mm,
										  Task2ReportDataHelper h, TreeMap<String, String> map) {
		Map<String, String> reversedMap = reverseMap(russianNameFields);
		
		// Информация о лимите \ сублимитах
		h.execWithRegions(mm, h.getSublimitContractorsData(map), reversedMap);

		//Ответственные подразделения
		//Другие подразделения
		h.execWithRegions(mm, h.getOtherDepartmentsData(map), reversedMap);
		
		// Контрагенты
		h.execWithRegions(mm, h.getContractorsData(map), reversedMap);

		//Основные параметры 
		//Виды сделок
		h.execWithRegions(mm, h.getOpportunityTypesData(map), reversedMap);
		//Другие цели
		h.execWithRegions(mm, h.getOtherGoalsData(map), reversedMap);
		//Сроки сделок (те же виды сделок, вид сбоку)
		h.execWithRegions(mm, h.getOpportunityTypesDatesData(map), reversedMap);
		
		// Стоимостные условия 
		// Процентная ставка (для кого?)
		h.execWithRegions(mm, h.getProcentStandardPriceConditionListData(map), reversedMap);

		//Фактические значения для процентной ставки (для Сделки)по периодам  
		h.execWithRegions(mm, h.getFactPercentListData(map), reversedMap);

		//Фактические значения для процентной ставки (для Сделки). Только первая строка
		h.execWithRegions(mm, h.getFactOneLinePercentListData(map), reversedMap);

		//Фактические значения для процентной ставки по траншам
		h.execWithRegions(mm, h.getTrancheFactPercentListData(map), reversedMap);
		
		
		//Вознаграждения для процентной ставки
		h.execWithRegions(mm, h.getPremiumListData(map), reversedMap);
		
		//Комиссии
		h.execWithRegions(mm, h.getCommissionListData(map), reversedMap);
		//Штрафные санкции
		h.execWithRegions(mm, h.getFineListData(map), reversedMap);
		
		//Комиссии (для Сделки) 
		h.execWithRegions(mm, h.getCommissionDealListData(map), reversedMap);

		// График платежей (для Сделки)
		h.execWithRegions(mm, h.getPaymentScheduleListData(map), reversedMap);
		
		// Условия
		// Условия досрочного погашения
		h.execWithRegions(mm, h.getEarlyPaymentListData(map), reversedMap);
		//Отлагательные условия заключения сделки
		h.execWithRegions(mm, h.getOtherConditionType1Data(), reversedMap);
		//Отлагательные условия использования кредитных средств
		h.execWithRegions(mm, h.getOtherConditionType2Data(), reversedMap);
		//Дополнительные условия сделки
		h.execWithRegions(mm, h.getOtherConditionType3Data(), reversedMap);
		//Индивидуальные условия
		h.execWithRegions(mm, h.getOtherConditionType4Data(), reversedMap);
		h.execWithRegions(mm, h.getOtherConditionType4DataW(), reversedMap);
		h.execWithRegions(mm, h.getOtherConditionType4DataG(), reversedMap);
		h.execWithRegions(mm, h.getOtherConditionType4DataD(), reversedMap);
		//Отклонения от установленных стандартов
		h.execWithRegions(mm, h.getOtherConditionType5Data(), reversedMap);
		//Критерии и устанавливаемый размер кредитных оборотов
		h.execWithRegions(mm, h.getOtherConditionType6Data(), reversedMap);
		//Отлагательные условия открытия аккредетива / выдачи гарантии
		h.execWithRegions(mm, h.getOtherConditionType7Data(), reversedMap);
		h.execWithRegions(mm, h.getOtherConditionType12Data(), reversedMap);
		
		//Обеспечение
		// Залоги
		h.execWithRegions(mm, h.getDepositListData(map), reversedMap);
		h.execWithRegions(mm, h.getDepositAdditionalAttrsData(), reversedMap);
		// Поручители
		h.execWithRegions(mm, h.getWarrantyListData(map), reversedMap);
		// Гарантии
		h.execWithRegions(mm, h.getGuaranteeListData(map), reversedMap);
		// Векселя
		h.execWithRegions(mm, h.getPromissoryNoteListData(map), reversedMap);
		
		// Транши (для сделки)
		h.execWithRegions(mm, h.getTranchesListData(map), reversedMap);
		
		// Проектная команда. Структураторы
		h.execWithRegions(mm, h.getProjectTeamStructurerListData(map), reversedMap);

		// Проектная команда. Клиентские менеджеры
		h.execWithRegions(mm, h.getProjectTeamClientManagerListData(map), reversedMap);

		// Проектная команда. СПКЗ
		h.execWithRegions(mm, h.getProjectTeamSPKZListData(map), reversedMap);

		// Проектная команда. Руководители структураторов
		h.execWithRegions(mm, h.getProjectTeamStrucurerManagerListData(map), reversedMap);

		// Проектная команда. Кредитные аналитики
		h.execWithRegions(mm, h.getProjectTeamCreditAnalyticListData(map), reversedMap);

		// Проектная команда. Продуктовый менеджер
		h.execWithRegions(mm, h.getProjectTeamProductManagerListData(map), reversedMap);

		// Договоры (Контракты)
		h.execWithRegions(mm, h.getContractListData(map), reversedMap);
		
		// departmentAgreements
		h.execWithRegions(mm, h.getDepartmentAgreementsListData(map), reversedMap);
		h.execWithRegions(mm, h.getVocConditionsData(), reversedMap);
	}

    /**
     * Finds and removes all blocks marked with BLOCK_EMPTY_TABLE_PREFIX<table_name>(STARTING/ENDING)_BLOCK_SUFFIX 
     * in case if the tables  
     * @param doc
     */
    private static void removeEmptyTablesBlocks(Document doc) {
        // TODO : implement!!!
        // удалим пустые таблицы
        //Task2ReportDataHelper.removeBlocks(doc, Task2ReportDataHelper.BLOCK_LIMIT_PREFIX);
        // уберем разметку для блоков пустых таблиц
        Task2ReportDataHelper.removeAllOccurencesOfMergeField(doc, Task2ReportDataHelper.BLOCK_EMPTY_TABLE_PREFIX);
    }
    
	public static String getIndRateNameById(String id){
		if(id==null || id.isEmpty())
			return "";
		CompendiumCrmActionProcessor compenduimCrm = (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
		for(FloatPartOfActiveRate fpar : compenduimCrm.findFloatPartOfActiveRateList(new FloatPartOfActiveRate(),null)){
			if(fpar.getId().equals(id))
				return fpar.getText();
		}
		return id;
	}
   
    /**
     * Helper method to fill simple fields 
     */
    public static TreeMap<String, String> generateSimpleFields(DocumentBuilder builder, Task task, Map<String, String> russianNameFields, 
                                                               boolean viewSource, boolean testData, Map<String, String> extraParams) {
        // генерируем пары KEY -- VALUE
        TreeMap<String, String> map =  new TreeMap<String, String>(); 
        if (extraParams != null) map.putAll(extraParams);

    	Task2ReportDataHelper helper = null;
    	if(task != null) {
	    	if (testData) helper = new Task2ReportDataHelperTest(builder, task, viewSource);
	        else helper = new Task2ReportDataHelper(builder, task, viewSource);
	
	        
	        // Это еще почему? TODO : выяснить!
	        map.put("isLimit", "True");
	        
	        String displayNumber = "";
	        if((task != null) && (task.getDisplayNumber() != null)) displayNumber = task.getDisplayNumber();
	        map.put("displayNumber", displayNumber);
	        
	        // Общие поля для основных параметров (Header)
	        map.putAll(helper.getHeaderData());
	        // Общие поля для основных параметров (Main)
	        map.putAll(helper.getMainData());
	        // Информация о родителе.
	        map.putAll(helper.getParentData());
	        // Контрагенты. Сборка имен групп и имен организаций
	        map.put("contractors_group_name", helper.getAllContractorsGroups());
	        map.put("contractors_org_name", helper.getAllContractors());
	        // Основной заемщик
	        map.putAll(helper.getMainBorrowerData());
	        // Категория качества
	        map.putAll(helper.getGeneralConditionData());
	        // для таблицы Стоимостные условия. Процент
	        map.putAll(helper.getTaskProcentData());
	        // Общие поля для обеспечения (Supply)
	        map.putAll(helper.getSupplyData());
	
	        // TranceComment
	        map.putAll(helper.getTranceCommentData());
	        
	        // в рамках лимита? (для сделки)
	        map.putAll(helper.getInLimitForPrintFormData());
	
	        // PrincipalPay (График погашения основного долга)
	        map.putAll(helper.getPrincipalPayData());
	        
	        // InterestPay (График погашения процентов)
	        map.putAll(helper.getInterestPayData());
	        
	        // conclusions
	        map.put("conclusion_blago_simple", helper.getConclusionSimpleData(CONCL_BLAGO));
	        map.put("conclusion_juridical_simple", helper.getConclusionSimpleData(CONCL_JURID));
	        map.put("conclusion_risk_simple", helper.getConclusionSimpleData(CONCL_RISK));
	        map.put("conclusion_reserv_simple", helper.getConclusionSimpleData(CONCL_RESERV));
	        map.put("conclusion_supply_simple", helper.getConclusionSimpleData(CONCL_SUPPLY));

	        //JPA
	        try {
	        	TaskJPA taskJPA = taskFacade().getTask(task.getId_task());
				MdTask mdtask = SBeanLocator.singleton().mdTaskMapper().getById(task.getId_task());
				if(taskJPA.getOrgList() != null && taskJPA.getOrgList().size()>0)
					map.put("kz_name", taskJPA.getOrgList().get(0).getOrganizationName());
				String projectName = ru.masterdm.spo.utils.Formatter.str(mdtask.getProjectName());
				if(mdtask.getProcessname()!=null && mdtask.getProcessname().equalsIgnoreCase("Pipeline") && !projectName.isEmpty())
					map.put("kz_name", projectName);
				if( (!map.containsKey("kz_name") || map.get("kz_name")==null) && task.getContractors().size()>0)
					map.put("kz_name", task.getContractors().get(0).getOrg().getOrganizationName());
				if(!map.containsKey("kz_name") || map.get("kz_name")==null)
					map.put("kz_name", SBeanLocator.singleton().mdTaskMapper().getById(task.getId_task()).getEkname());

	        	map.put("definition", taskJPA.getDefinition());
	        	map.put("rate2", Formatter.format(taskJPA.getRate2()));

  	        	//назначения для суммы сделки
	        	map.put("targetGroupLimit", createTargetGroupeLimitText(task.getId_task(), taskJPA.getTargetTypeControlNote()));
	        	//запрещённые цели
	        	map.put("forbidden_goals", createForbiddenGoalsText(task));
	        	
	        	String rate2report = Formatter.format(taskJPA.getRate2());
	        	if (!rate2report.isEmpty())
	        		rate2report += " % годовых";
	        	
	        	String rate2note = taskJPA.getRate2Note();
		        map.put("rate2note", (rate2note == null)?"":rate2note);
	        	
	        	map.put("rate2report", rate2report);
	        	map.put("active_decision", taskJPA.getActive_decision());
	        	map.put("period_full", taskJPA.getPeriodFull());
	        	map.put("pmn_order", taskJPA.getPmnOrder());
	        	
	        	//authorized_person
	        	Long userid = task.getTaskStatusReturn().getIdUser();
				String authorizedPerson = "";
				if (taskJPA.getAuthorizedPerson()!=null){
					authorizedPerson = taskJPA.getAuthorizedPerson().getDisplayName();
				}
				if(authorizedPerson=="" && userid!=null && userid>0l){
				    authorizedPerson = pupFacade().getUser(userid).getFullName() + " (" + 
				        pupFacade().getUser(userid).getDepartment().getShortName() + ")";
				}
	        	map.put("authorized_person", authorizedPerson);

	        	//ставка 
	        	map.put("rate", createRateText(taskJPA));

	        	map.put("authorizedPerson", taskJPA.getAuthorizedPerson()!=null?taskJPA.getAuthorizedPerson().getDisplayName():"");

	        	String operationDecisionList = "";  
	        	if ((taskJPA.getOperDecision() != null) && (!taskJPA.getOperDecision().isEmpty())) {
	        		for (OperDecisionJPA jpa : taskJPA.getOperDecision()) {
	        			String element = "";
	        			if ((jpa.getDescriptions() != null) && (!jpa.getDescriptions().isEmpty())) {
	        				element += "Решение о/об:\n";
	        				for(OperDecisionDescriptionJPA decision : jpa.getDescriptions()) 
	        					element += "- "+decision.getDescr()+"\n";
	        			}
	        			if(!jpa.getAccepted().isEmpty())
	        				element += "принимаются:\n"+jpa.getAccepted();
	        			element += "\n"+jpa.getSpecials();
	        			operationDecisionList += element + "\n \n \n";
	        		}
	        	}
	        	map.put("operationDecisionList", operationDecisionList);
			} catch (Exception e) {
				LOGGER.log(Level.WARNING,e.getMessage(), e);
			}
	        //Прогнозные значения процентной ставки
	        try {
	        	CrmFacadeLocal crm = com.vtb.util.EjbLocator.getInstance().getReference(CrmFacadeLocal.class);
	        	if(task.getHeader().getCrmQueueId()!=null){
	        		ru.md.crm.dbobjects.NetworkWagerJPA[] wagers = crm.getNetworkWagerByProductQueueId(task.getHeader().getCrmQueueId());
	        		for(ru.md.crm.dbobjects.NetworkWagerJPA w : wagers) {
	        			map.put("procent_crm_period_name", w.getPERIOD_NAME());
	        			map.put("procent_crm_start_date", Formatter.format(w.getSTART_DATE()));
	        			map.put("procent_crm_end_date", Formatter.format(w.getEND_DATE()));
	        			map.put("procent_crm_wrkliborsrok", w.getWRKLIBORSROK());//Тип индикативной ставки
	        			map.put("procent_crm_stavfloatfixedwrk", Formatter.format(w.getSTAVFLOATFIXEDWRK()));//Переменная часть ставки
	        			map.put("procent_crm_stavrazwrk", Formatter.format(w.getSTAVRAZWRK()));//Фиксированная часть ставки
	        			map.put("procent_crm_stplavwrk", Formatter.format(w.getSTPLAVWRK()));//Результирующая ставка
	        			map.put("procent_crm_vidobesp", w.getVIDOBESP());//Вид обеспечения
	        		}
	        	}
			} catch (Exception e) {
				// TODO : вернуть!
				//LOGGER.log(Level.WARNING,"NetworkWager error",e);
			}
    	}
        addRussianNames(map, russianNameFields);
        if (task != null) {
	        // напечатаем результаты
	        helper.printMap(builder, map);
        }
        return map;
    }

    private static String createRateText(TaskJPA taskJPA) {
		//по этому функционалу была ошибка СПО 113А. МО.104.105. ПКР 4 и 6: значение в разделе Стоим.условия-Проц.ставка (поле «rate») выводится неверно
		//но как верно пока не удаётся выяснить. Ждем согласования требований. После согласования механизм будет изменён.
    	String result = "";
    	try {
        	
        	List<FactPercentJPA> periods = taskJPA.getFactPercents();
        	if (periods != null && periods.size() > 0) {
        		boolean skipTitle = (periods.size() < 2); //если период 1, то пропустить заголовок
        		//периоды с диапазоном дат
        		int p = 0;
        		int t = 0;
        		int i = 0;
        		for (FactPercentJPA period:periods) {
					if(!taskJPA.isTrance_graph() && period.getTranceId() != null)
						continue;
        			boolean isPeriod = false;
        			boolean isTranche = false;
        			if (period.getTranceId() != null) {
        				isTranche = true;
        				t++;
        			} else if (period.getId() != null) {
            			isPeriod = true;
            			p++;
            		} else 
        				continue;
            		String indrateText = "";
                	boolean fixRate = false;
                	boolean floatRate = false;
                	String ratePercent = "";
                	String rateDescription = "";

                	if (i != 0)
                		result += "\n\n";
                	if (skipTitle == false) {
                		if (isPeriod)
                			result += ("Период " + p);
                		if (isTranche)
                		result += ("Транш " + t);
                		if (period.getStart_date() != null && period.getEnd_date() != null)
                			result += (" с " + Formatter.format(period.getStart_date()) + " по " + Formatter.format(period.getEnd_date()));
                		result += "\n";

                		//фиксированная/плавающая из периода
                		fixRate = period.isInterestRateFixed();
                		floatRate = period.isInterestRateDerivative();
                	}
                	else {
                		//фиксированная/плавающая из сделки
                		fixRate = (taskJPA.isInterestRateFixed()==null)?false:taskJPA.isInterestRateFixed();
                		floatRate = (taskJPA.isInterestRateDerivative()==null)?false:taskJPA.isInterestRateDerivative();
                	}

        			//result += ("periodId=" + period.getId() + ", fix:" + fixRate + ", float:" + floatRate + "\n");
        			
        			if (period.getRate4() != null)
        				ratePercent += Formatter.toMoneyFormat(period.getRate4()) + " % годовых";
        			if (period.getRate4Desc() != null)
        				rateDescription += period.getRate4Desc();

        			if (floatRate == true)
                		indrateText = createIndRateText(taskJPA, skipTitle?null:period.getId());

        			// Карта Карно для fixRate и floatRate
            		//                fixRate
            		//           ---------------------------------
            		//           | ставка %      |               |
            		// floatRate | инд. ставка   | инд. ставка   |
            		//           | описание      |               |
            		//           ---------------------------------
            		//           | ставка %      | ставка %      |
            		//           | описание      | описание      |
            		//           ---------------------------------

        			if (!floatRate) {
            			result += ratePercent;
            			if (!rateDescription.isEmpty())
        	    			result += ("\n" + rateDescription);
            		}
            		else if (fixRate) {
            			result += ratePercent;
            			if (!indrateText.isEmpty())
        	    			result += ("\n" + indrateText);
            			if (!rateDescription.isEmpty())
        	    			result += ("\n" + rateDescription);
            		}
            		else { // float и не fix
            			result += indrateText;
            		}
        			i++;
        		}
        	}
    	} catch (Exception ex) {
    		result = "";
    	}
    	return result;
    }

    private static String createIndRateText(TaskJPA taskJPA, Long periodId) {
    	String result = "";
    	try {
    		List<IndrateMdtaskJPA> indrates = taskJPA.getIndrates();
    		if (indrates != null && indrates.size() > 0) {
        		int i = 0;
    			for (IndrateMdtaskJPA indrate:indrates) {
    				if (i != 0)
    					result += "\n";
    				if ((periodId == null && indrate.getIdFactpercent() == null) //для сделок 
    						|| 
    						(indrate.getIndrate() != null && periodId != null && periodId.equals(indrate.getIdFactpercent()))) {
    					result += (getIndRateNameById(indrate.getIndrate()));
	    				if (indrate.getRate() != null)
	    					result += (" + " + indrate.getRate() + " % годовых");
    					//result += (", id=" + indrate.getIdFactpercent() + ", periodId=" + periodId);
	    				i++;
    				}
    			}
    		}
    	} catch (Exception ex) {
    		result = "";
    	}
    	return result;    	
    }

/*
    private static String createRateText(TaskJPA taskJPA) {
    	String result = "";
    	try {
        	String periodPrefix = "";
        	boolean fixRate = false;
        	boolean floatRate = false;
        	String ratePercent = "";
        	String rateDescription = "";
        	
        	List<FactPercentJPA> periods = taskJPA.getFactPercents();
        	if (periods != null && periods.size() > 0) {
        		//период с диапазоном дат
        		if (periods.get(0).getStart_date() != null && periods.get(0).getEnd_date() != null)
        			periodPrefix = "Период 1 с " + Formatter.format(periods.get(0).getStart_date()) + " по " + Formatter.format(periods.get(0).getEnd_date());
        		
        		fixRate = periods.get(0).isInterestRateFixed();
        		floatRate = periods.get(0).isInterestRateDerivative();
        		if (periods.get(0).getRate4() != null)
        			ratePercent = Formatter.toMoneyFormat(periods.get(0).getRate4()) + " % годовых";
        		if (periods.get(0).getRate4Desc() != null)
	        		rateDescription = periods.get(0).getRate4Desc();
        	}
        	
    		String indrateText = "";
        	if (floatRate == true) {
	    		List<IndrateMdtaskJPA> indrates = taskJPA.getIndrates();
	    		if (indrates != null && indrates.size() > 0) {
	        		//indrateText += "\n";
	        		int i = 0;
	    			for (IndrateMdtaskJPA indrate:indrates) {
	    				if (i != 0)
			        		indrateText += "\n";
	    				if (indrate.getIndrate() != null) {
	    					indrateText += (getIndRateNameById(indrate.getIndrate()));
		    				if (indrate.getRate() != null) {
		    					indrateText += (" + " + indrate.getRate() + " % годовых");
		    				}
		    				i++;
	    				}
	    			}
	    		}
        	}
    		
    		// Карта Карно для fixRate и floatRate
    		//                fixRate
    		//           ---------------------------------
    		//           | ставка %      |               |
    		// floatRate | инд. ставка   | инд. ставка   |
    		//           | описание      |               |
    		//           ---------------------------------
    		//           | ставка %      | ставка %      |
    		//           | описание      | описание      |
    		//           ---------------------------------
    		
    		
    		if (!floatRate) {
    			result += ratePercent;
    			if (!rateDescription.isEmpty())
	    			result += ("\n" + rateDescription);
    		}
    		else if (fixRate) {
    			result += ratePercent;
    			if (!indrateText.isEmpty())
	    			result += ("\n" + indrateText);
    			if (!rateDescription.isEmpty())
	    			result += ("\n" + rateDescription);
    		}
    		else { // float и не fix
    			result += indrateText;
    		}
    		
    		if (!periodPrefix.isEmpty())
    			result = (periodPrefix + "\n" + result); 
    	} catch (Exception ex) {
    		result = "";
    	}
    	return result;
    }
*/
    private static String createForbiddenGoalsText(Task task) {
    	String result = "";
    	if (task == null || task.getMain() == null)
    		return result;
    	try {
    		ArrayList<Forbidden> forbiddens = task.getMain().getForbiddens();
    		if (forbiddens == null || forbiddens.size() == 0)
    			return result;
    		result += "\nЗапрещается предоставление денежных средств на любую из нижеуказанных целей (прямо или косвенно, через третьих лиц):\n";
    		int i = 0;
    		for (Forbidden forbidden:forbiddens) {
    			if (i != 0)
    				result += "\n";
    			if (forbidden.getGoal() != null) {
    				result += forbidden.getGoal();
    				i++;
    			}
    		}
    	} catch (Exception ex) {
    		result = "";
    	}
    	return result;
    }
    
	private static String createTargetGroupeLimitText(Long idMdtask, String commonComment) {
    	String result = "";
    	try {
        	List<TargetGroupLimit> targetGroupLimits = SBeanLocator.singleton().mdTaskMapper().getTargetGroupLimits(idMdtask);
        	if (targetGroupLimits == null || targetGroupLimits.size() == 0)
        		return result;
        	int groupeCounter = 0;
        	for (TargetGroupLimit targetGroupLimit:targetGroupLimits) {
        		if (groupeCounter != 0)
        			result += "\n"; //два перевода строки между группами
        		String groupeAmount = Formatter.format(targetGroupLimit.getAmount());
        		String groupeCurrency = "";
        		if (groupeAmount != null && !groupeAmount.isEmpty()) {
        			groupeCurrency = SBeanLocator.singleton().getDictService().moneyCurrencyDisplay(targetGroupLimit.getAmount(),targetGroupLimit.getAmountCurrency());
        			if (groupeCurrency == null || groupeCurrency.isEmpty())
            			groupeCurrency = "";
        		}
        		else
        			groupeAmount = "";
        		String groupeComment = targetGroupLimit.getNote();
        		
        		List<TargetGroupLimitType> limitTypes = targetGroupLimit.getTargetGroupLimitTypes();
        		if (limitTypes != null && limitTypes.size() != 0) {
        			result += "На ";
                	int groupeTypeLastIndex = limitTypes.size() - 1;
                	int groupeTypeCounter = 0;
        			for (TargetGroupLimitType limitType:limitTypes) {
        				if (limitType.getTargetTypeName() != null) {
        					result += (limitType.getTargetTypeName());
        					if (groupeTypeCounter != groupeTypeLastIndex)   //если элемент списка не последний,
        						result += "; ";								//то добавляется разделитель "; "
        				}
    					groupeTypeCounter++;
        			}
        			result += " - ";
        		}
        		if (!groupeAmount.isEmpty())
        			result += ("до " + groupeAmount + " " + groupeCurrency + " ");
        		if (groupeComment != null)
        			result += groupeComment;
        		groupeCounter++;
        	}
    		if (commonComment != null && !commonComment.isEmpty())
    			result += ("\n\n" + commonComment);

    	} catch (Exception ex) {
    		result = "";
    	}
    	return result;
    }
    
    /**
     * adds to map record like <russianName, englishName.value>
     * That is, we have two identical values in the map with keys russianName and englishName
     * @param map
     * @param russianNameFields
     */
    private static void addRussianNames(Map<String, String> map, Map<String, String> russianNameFields) {
        for (String key : russianNameFields.keySet()) {
            if (map.containsKey(russianNameFields.get(key)))
                map.put(key,  map.get(russianNameFields.get(key)));
        }
    }
    
    /** 
     * makes a reversed map: <value, key> for the given one
     */
    private static Map<String, String> reverseMap(Map<String, String> map) {
        Map<String, String> result = new HashMap<String, String>();
        for (Map.Entry<String, String> entry : map.entrySet()) 
            result.put(entry.getValue(), entry.getKey());
        return result;
    }
    public static TaskFacadeLocal taskFacade(){
    	try {
    		return com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
    	} catch (Exception e) {
    	}
    	return null;
    }
    public static PupFacadeLocal pupFacade(){
        try {
            return com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
       } catch (Exception e) {
       }
        return null;
    }
}
