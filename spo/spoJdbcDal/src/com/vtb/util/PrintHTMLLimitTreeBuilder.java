package com.vtb.util;

import java.io.IOException;

import javax.servlet.jsp.JspWriter;

import ru.masterdm.compendium.domain.crm.CompanyGroup;

import com.vtb.domain.CRMLimit;
import com.vtb.domain.Task;
import com.vtb.domain.TaskContractor;

/**
 * Helper class to write to html (in CRMLimitTag) the content of LimitTree to which an Opportunity is binded   
 * Uses hooks methods to write content.   
 * @author Michael Kuznetsov
 * 
 * version 2. Show only ONE record. To which Opportunity (Deal) is binded.
 * To Show ALL records, set showOnlyLinkedRecord to false.
 *  
 */
public class PrintHTMLLimitTreeBuilder extends LimitTreeBuilder{
	
    private final boolean showOnlyLinkedRecord = false;  
    private String taskid;   // id Task
	
    public PrintHTMLLimitTreeBuilder (StringBuffer htmlOut, JspWriter out, String limitid, String taskid, int SKIP_BODY) {
        super(htmlOut, out, limitid, SKIP_BODY);
        this.taskid = taskid;
    }

	@Override
	protected void writeHeaderStart(StringBuffer htmlOut) {
        htmlOut.append("<table class=\"regular\">");
        htmlOut.append("<tr><th></th><th>номер</th><th>Группы компаний</th><th>Контрагенты</th><th>Вид лимита</th><th>Сумма</th><th>Срок сделок</th></tr>");
	}

	@Override
	protected void writeHeaderEnd(StringBuffer htmlOut, JspWriter out) throws IOException {
	    htmlOut.append("</table>");
	    out.print(htmlOut.toString());
	}
	
	@Override
	protected void printLimit(StringBuffer htmlOut, String prefix, CRMLimit sublimit, String limitid) {
	    boolean isMarkedRecord =  (sublimit.getLimitid() != null && sublimit.getLimitid().equals(limitid));
	    if (!showOnlyLinkedRecord)
	        htmlOut.append("<tr align=\"left\""+ (isMarkedRecord ?"class=marked":"")+">");
	    else 
	        if (isMarkedRecord) htmlOut.append("<tr align=\"left\" >");
        
	    if ((!showOnlyLinkedRecord)  || ((showOnlyLinkedRecord) && (isMarkedRecord))) {
	        String name = prefix + (prefix.equals("") ? "Лимит" : "Сублимит");
	        htmlOut.append("<td style=\"width:10%;\">" + name
                +"</td><td align=\"center\" style=\"width:10%;\">"+sublimit.getCode()+
                "</td><td style=\"width:15%;\">"+ // тут Группа компаний. Только есть ли она в базе CRM?
                "</td><td style=\"width:20%;\">"+sublimit.getOrganisationFormated()
                +"</td><td align=\"center\" style=\"width:15%;\">"+sublimit.getLimit_vid()
                +"</td><td class=\"number\" align=\"right\" style=\"width:15%;\">" +Formatter.format(sublimit.getSum())+" "+ sublimit.getCurrencycode()+
                "</td><td align=\"center\" style=\"width:15%;\">" + // ничего не грузим, нет даты. // Formatter.format(sublimit.getCreateDate()) + // других дат тут нет, грузим дату создания.
            "</td></tr>");
	    }
    }

	@Override
	protected void printLimitAsTask(StringBuffer htmlOut, String prefix, Task task, String idLimitType, String limitid) {
	   // old version : mark the limit \ sublimit to which references this opportunity.
	   //boolean isMarkedRecord =  (limitid != null) && limitid.equals(task.getHeader().getCrmid());
	   // new version : mark the opportunity itself. not the limit \ sublimit to which references this opportunity..
       boolean isMarkedRecord =  (taskid != null) && taskid.equals(String.valueOf(task.getId_task()));
	   if (!showOnlyLinkedRecord)
           htmlOut.append("<tr align=\"left\""+ (isMarkedRecord ?"class=marked":"")+">");
       else 
           if (isMarkedRecord) htmlOut.append("<tr align=\"left\" >");
       
	   if ((!showOnlyLinkedRecord)  || ((showOnlyLinkedRecord) && (isMarkedRecord))) {
           String typePrfx = null; 
           if (task.isOpportunity()) typePrfx = "Сделка";
           if (task.isSubLimit())  typePrfx = "Сублимит";
           if (task.isLimit())  typePrfx = "Лимит";

           String name = "<a target=\"printforf\" href=print_form.do?mdtask=" + task.getId_task().toString()
           + ">" +prefix + typePrfx +"</a>";
           String organisation = ""; StringBuilder sb = new StringBuilder();
           boolean groupFlag = false; 
           boolean firstOrganization = true;
           for(TaskContractor tc:task.getContractors()){
               if (!firstOrganization) organisation += "; ";
               organisation+=tc.getOrg().getAccount_name();
               firstOrganization = false;
               if (tc.getGroupList() != null)
                   for(CompanyGroup group : tc.getGroupList()) {
                       if (groupFlag) sb.append("; ");
                       sb.append(Formatter.str(group.getName()));
                       groupFlag = true;
                   }
           }
           String sum = Formatter.format(task.getMain().getSum());
           
           htmlOut.append("<td align=\"left\" style=\"width:10%;\">"+name
               +"</td><td align=\"center\" style=\"width:10%;\">"+task.getNumberDisplay()+
               "</td><td align=\"center\" style=\"width:15%;\">"+ sb.toString() +
               "</td><td style=\"width:20%;\">" + organisation +
               "</td><td align=\"center\" style=\"width:15%;\">"+ Formatter.str(idLimitType) +             
               "</td><td class=\"number\" align=\"right\" style=\"width:15%;\">"+ sum +" "+ task.getMain().getCurrency2().getCode() +
               "</td><td align=\"center\" style=\"width:15%;\">" + Formatter.format(task.getMain().getValidto()) + 
               "</td></tr>");
       }
   }
}
