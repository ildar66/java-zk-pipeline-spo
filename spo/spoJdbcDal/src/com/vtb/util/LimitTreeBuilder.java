package com.vtb.util;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import javax.servlet.jsp.JspWriter;

import ru.masterdm.compendium.domain.spo.LimitType;
import ru.masterdm.compendium.model.CompendiumSpoActionProcessor;

import com.vtb.domain.CRMLimit;
import com.vtb.domain.Task;
import com.vtb.exception.MappingException;
import com.vtb.exception.ModelException;
import com.vtb.model.ActionProcessorFactory;
import com.vtb.model.TaskActionProcessor;

/**
 * Basic method to build an LimitTree to which an Opportunity is binded
 * Uses hooks methods whichs are overriden by inheritences.
 * Uses design pattern *** (GOF) 
 * @author Michael Kuznetsov
 */
public class LimitTreeBuilder {
	private static final Logger LOGGER = Logger.getLogger(LimitTreeBuilder.class.getName());
    private TaskActionProcessor processor = null;
    private String limitid = null;
	private Long taskId = null;
	private StringBuffer htmlOut = null;
	private JspWriter out = null;
	private int SKIP_BODY = 0;
	
	/**
	 * Conctructor (case, when crm identifier is known)
	 * @param htmlOut buffer to write html output, if necessary
	 * @param out JspWriter to write html output, if necessary
	 * @param limitid crm limit identifier
	 * @param SKIP_BODY return value whether to skip body (html output) or not
	 */
	public LimitTreeBuilder (StringBuffer htmlOut, JspWriter out, String limitid, int SKIP_BODY) {
        this.htmlOut = htmlOut; 
        this.out = out;
        this.limitid = limitid;
        this.taskId = null;
        this.SKIP_BODY = SKIP_BODY; 
    }

	/**
     * Conctructor (case, when mdtask id is known (for limit, sublimit or opportunity, not connected to limit))
     * @param htmlOut buffer to write html output, if necessary
     * @param out JspWriter to write html output, if necessary
     * @param taskId mdtask identifier 
     * @param SKIP_BODY return value whether to skip body (html output) or not
     */
	public LimitTreeBuilder (StringBuffer htmlOut, JspWriter out, Long taskId, int SKIP_BODY) {
        this.htmlOut = htmlOut; 
        this.out = out;
        this.limitid = null;
        this.taskId = taskId;
        this.SKIP_BODY = SKIP_BODY; 
    }
	
	private String findRootTask(String upperlimit) throws IOException {
        try {
            while(processor.findCRMLimitById(upperlimit) != null
                  && processor.findCRMLimitById(upperlimit).getParentlimitid() != null){
                upperlimit = processor.findCRMLimitById(upperlimit).getParentlimitid();
                LOGGER.info("upperlimit=" +upperlimit);
            }
        } catch(ModelException e){
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            if (out != null) out.print("Ошибка. Некорректная ссылка. Не существует лимита " + upperlimit);
        }
        return upperlimit;
    }
	
	
	public void makeOutputSPOLoaded(TaskActionProcessor processor, Long taskId) throws ModelException, IOException, MappingException {
        // забираемся вверх по иерарархии лимитов в СПО (по mdtask).
        Task upperLimitTask = processor.getTask(new Task(taskId));
        try{
            while(upperLimitTask.getParent() != null && (upperLimitTask.getParent().longValue() != 0)) {
                upperLimitTask =  processor.getTask(new Task(upperLimitTask.getParent()));
                LOGGER.info("upperlimit= " + upperLimitTask.getId_task());
            }
        } catch(Exception e){
            LOGGER.log(Level.SEVERE, e.getMessage(), e);
            e.printStackTrace();
            out.print("Ошибка. Некорректная ссылка. Не существует лимита");
        }
        upperLimitTask = processor.getTask(new Task(upperLimitTask.getId_task()));
        writeHeaderStart(htmlOut);                
        // записываем корневой элемент (лимит), записываем также рекурсивно и все его сублимиты
        fillTR(htmlOut, "", upperLimitTask);
        writeHeaderEnd(htmlOut, out);
	}
	
	
    public int makeOutput() {
        try {
            processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
            String upperlimit = limitid;
            
            // TODO : write variant, when not limitid, but rather taskId
            // variant when initialized with task identifier
            if ((taskId != null) && (limitid == null)) {
              // считаем, что всегда загружено в СПО
              makeOutputSPOLoaded(processor, taskId);
            }

            // variant when initialized with limitid (crm id) 
            if ((taskId == null) && (limitid != null))
                // Идем по иерархии СПО всегда, когда  загружен в СПО. 
                if (processor.isCRMLimitLoaded(limitid)) {
                    makeOutputSPOLoaded(processor, processor.findByCRMid(limitid).getId_task());
                } else {
                    // TODO : что делать, если не загрузили? что-то я не очень понимаю, как можно обработать!!!
                    // идем по иерархии в CRM, только когда не загружено в СПО.
                    // и еще пробуем забраться вверх по иерарархии лимитов в CRM.
                    CRMLimit limit = processor.findCRMLimitById(findRootTask(upperlimit));
                    if(limit == null){
                        if (out != null) out.print("Ошибка. Некорректная ссылка. Не существует лимита " + upperlimit);
                        return SKIP_BODY;
                    }
                    writeHeaderStart(htmlOut);
                    // записываем корневой элемент (лимит), записываем также рекурсивно и все его сублимиты
                    fillTR(htmlOut, "", limit);
                    writeHeaderEnd(htmlOut, out);
                }
        } catch (Exception ex) {
        	LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
        	ex.printStackTrace();        	
            try {
				if (out != null) out.print("CRMLimitTag doStartTag error " + ex.getMessage());
			} catch (IOException e) {
				LOGGER.log(Level.SEVERE, ex.getMessage(), ex);
	        	ex.printStackTrace();
			}
        }
        return SKIP_BODY;
    }
    
     /**
	 * Рисуем сублимиты в виде CRMLimit 
	 * @param htmlOut
	 * @param processor
	 * @param upperlimit
	 * @throws ModelException
	 */
	private void showSublimits(StringBuffer htmlOut, String prefix, String upperlimit) throws ModelException {
		ArrayList<CRMLimit> sublimits = processor.findCRMSubLimit(upperlimit);
		for(CRMLimit sublimit:sublimits){			
			fillTR(htmlOut, prefix, sublimit);
		}
	}
	/**
	 * Рисуем лимиты \ сублимиты в виде CRMLimit
	 * @param htmlOut
	 * @param prefix
	 * @param sublimit
	 * @throws ModelException
	 */
	private void fillTR(StringBuffer htmlOut, String prefix, CRMLimit sublimit) throws ModelException {
		// лимит был загружен из CRM. Грузим его и отваливаем
	    if(sublimit.getLimitid() != null && processor.isCRMLimitLoaded(sublimit.getLimitid())){
			fillTR(htmlOut, prefix, processor.findByCRMid(sublimit.getLimitid()));
			return;
		}
	    // лимит не был загружен из CRM. Рисуем сублимит \ лимит.
	    printLimit(htmlOut, prefix, sublimit, limitid);
		showSublimits(htmlOut,"~~"+prefix,sublimit.getLimitid());
	}
    
	/**
	 * Рисуем лимиты \ сублимиты в виде Task
	 * @param htmlOut
	 * @param prefix
	 * @param task
	 * @throws ModelException
	 */
	private void fillTR(StringBuffer htmlOut, String prefix, Task task) throws ModelException {
		try{
			String idLimitType = null; 
			if (task.getHeader().getIdLimitType() != null) {
			    CompendiumSpoActionProcessor compenduimspo = (CompendiumSpoActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumSpo");
			    LimitType type = compenduimspo.findLimitType(new LimitType(task.getHeader().getIdLimitType().longValue()));
			    if (type != null)  idLimitType = type.getName();
			}
			
			// рисуем текущий task
			printLimitAsTask(htmlOut, prefix, task, idLimitType, limitid);

			// ищем связанные с текущим task сделки 
			List<Task> opportunities = processor.findChildrenOfCRMid(task.getHeader().getCrmid(), false);
			for (Task  opportunity : opportunities) 
			    fillTR(htmlOut, "~~" + prefix, opportunity);
			// ищем его сублимиты
			ArrayList<Task> sublimits = processor.findTaskByParent(task.getId_task(), false, true);
			for (Task sublimit : sublimits)
				fillTR(htmlOut, "~~" + prefix, sublimit);
			
		}catch(Exception e){
			throw new ModelException(e.getMessage());
		}
	}
    
	/**
	 * Пустая реализация (используется как зацепка для вызова конкретной функции)
	 * @param htmlOut
	 */
	protected void writeHeaderStart(StringBuffer htmlOut) {	}

	/**
	 * Пустая реализация (используется как зацепка для вызова конкретной функции)
	 * @param htmlOut
	 * @param out
	 * @throws IOException
	 */
	protected void writeHeaderEnd(StringBuffer htmlOut, JspWriter out) throws IOException {	}
	
	/**
	 * Пустая реализация (используется как зацепка для вызова конкретной функции)
	 * @param htmlOut
	 * @param prefix
	 * @param sublimit
	 */
	protected void printLimit(StringBuffer htmlOut, String prefix, CRMLimit sublimit, String limitid) {  }

	/**
	 * Пустая реализация (используется как зацепка для вызова конкретной функции)
	 * @param htmlOut
	 * @param prefix
	 * @param task
	 * @param idLimitType
	 */
	protected void printLimitAsTask(StringBuffer htmlOut, String prefix, Task task, String idLimitType, String limitid) {   }
}
