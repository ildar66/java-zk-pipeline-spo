package com.vtb.util;

import java.io.IOException;
import java.util.ArrayList;

import javax.servlet.jsp.JspWriter;

import ru.masterdm.compendium.domain.crm.CompanyGroup;

import com.vtb.domain.CRMLimit;
import com.vtb.domain.LimitTree;
import com.vtb.domain.Task;
import com.vtb.domain.TaskContractor;

/**
 * Helper class to write to Task the content of LimitTree to which an Opportunity is binded   
 * Uses hooks methods to write the content.   
 * @author Michael Kuznetsov
 * 
 * version 2. Show only ONE record. To which Opportunity (Deal) is binded.
 * To Show ALL records, set showOnlyLinkedRecord to false.
 * 
 */
public class TaskInfoLimitTreeBuilder extends LimitTreeBuilder{
	
    private boolean showOnlyLinkedRecord = true;
    private ArrayList<LimitTree> limitTreeList;  //LimitTree
    
    /**
     * Constructor
     * @param crmLimitid crm id of limit \sublimit (not task id!!!) 
     * @param showOnlyLinkedRecord whether to show only one record or all structure of limi\sublimits
     */
	public TaskInfoLimitTreeBuilder (String crmLimitid, boolean showOnlyLinkedRecord) {
        super(new StringBuffer(""), null, crmLimitid, 0);
        this.showOnlyLinkedRecord = showOnlyLinkedRecord;
        limitTreeList = new ArrayList<LimitTree>();
    }

	/**
     * Constructor
     * @param taskId task id of limit \sublimit 
     * @param showOnlyLinkedRecord whether to show only one record or all structure of limi\sublimits
     */
    public TaskInfoLimitTreeBuilder (Long taskId, boolean showOnlyLinkedRecord) {
        super(new StringBuffer(""), null, taskId, 0);
        this.showOnlyLinkedRecord = showOnlyLinkedRecord;
        limitTreeList = new ArrayList<LimitTree>();
    }
	
	@Override
	protected void writeHeaderStart(StringBuffer htmlOut) {	}

	@Override
	protected void writeHeaderEnd(StringBuffer htmlOut, JspWriter out) throws IOException {	}
	
	@Override
	protected void printLimit(StringBuffer htmlOut, String prefix, CRMLimit sublimit, String limitid) {
	    boolean isMarkedRecord =  (sublimit.getLimitid() != null && sublimit.getLimitid().equals(limitid));
	    if ((!showOnlyLinkedRecord)  || ((showOnlyLinkedRecord) && (isMarkedRecord))) {
    	    LimitTree element = new LimitTree();
    	    if (showOnlyLinkedRecord) element.setMarked(false);   // не помечаем никак, он там один
    	    else  element.setMarked(isMarkedRecord);
    	    String name = prefix+(prefix.equals("")?"Лимит":"Сублимит");
    	    element.setName(name);
    	    element.setReferenceId(sublimit.getCode());
    	    element.setCompaniesGroup(null);    // тут Группа компаний. Только есть ли она в базе CRM?
    	    element.setOrganization(sublimit.getOrganisationFormated());
    	    element.setLimitVid(sublimit.getLimit_vid());
    	    element.setSum(sublimit.getSum());
    	    element.setCurrency(sublimit.getCurrencycode());
    	    element.setValidTo(null);                     // тут нет нормальных да. Возможно, sublimit.getCreateDate()
    	    limitTreeList.add(element);
	    }
    }

	@Override
	protected void printLimitAsTask(StringBuffer htmlOut, String prefix, Task task, String idLimitType, String limitid) {
	    boolean isMarkedRecord =  (limitid != null) && (limitid.equals(task.getHeader().getCrmid()));
	    if ((!showOnlyLinkedRecord)  || ((showOnlyLinkedRecord) && (isMarkedRecord))) {
    	    LimitTree element = new LimitTree();
            if (showOnlyLinkedRecord) element.setMarked(false);   // не помечаем никак, он там один
            else  element.setMarked(isMarkedRecord);
    	    String name = prefix + (prefix.equals("")?"Лимит":"Сублимит");
    	    element.setName(name);
    	    
    	    String organisation = ""; StringBuilder sb = new StringBuilder();
    	    boolean groupFlag = false; 
    	    boolean firstOrganization = true;
            for(TaskContractor tc:task.getContractors()){
               if (!firstOrganization) organisation += "; ";  
               organisation += tc.getOrg().getAccount_name();
               firstOrganization = false;
               if (tc.getGroupList() != null)
                   for(CompanyGroup group : tc.getGroupList()) {
                       if (groupFlag) sb.append("; ");  
                       sb.append(Formatter.str(group.getName()));
                       groupFlag = true;
                   }
            }
            element.setId_task(task.getId_task());
            element.setReferenceId(task.getNumberDisplay());
            element.setCompaniesGroup(sb.toString());    
            element.setOrganization(organisation);
            element.setLimitVid(idLimitType);
            element.setSum(task.getMain().getSum());
            element.setCurrency(task.getMain().getCurrency2().getCode());
            element.setValidTo(task.getMain().getValidto());   
            limitTreeList.add(element);
	    }
   }

    public ArrayList<LimitTree> getLimitTreeList() {
        return limitTreeList;
    }
}
