<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%@page import="com.vtb.model.TaskActionProcessor"%>
<%@page import="com.vtb.util.Formatter"%>
<%@page import="ru.md.spo.ejb.CrmFacadeLocal"%>
<%@page import="com.vtb.domain.Task"%>
<%@page import="com.vtb.model.ActionProcessorFactory"%>
<%
TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
CrmFacadeLocal crm = com.vtb.util.EjbLocator.getInstance().getReference(CrmFacadeLocal.class);
Task task=processor.getTask(new Task(Long.valueOf(request.getParameter("mdtaskid"))));
ru.md.crm.dbobjects.NetworkWagerJPA[] wagers = null;
if(task.getHeader().getCrmQueueId()!=null){
    wagers = crm.getNetworkWagerByProductQueueId(task.getHeader().getCrmQueueId());
}
if(wagers==null || wagers.length==0){%>
В системе CRM нет информации о прогнозных значениях по этой сделке
<%}else{ %>
    <%for(ru.md.crm.dbobjects.NetworkWagerJPA w : wagers) {%>
    <%if(wagers.length>0){ %><h4><%=w.getPERIOD_NAME() %> Дата с: <%=Formatter.format(w.getSTART_DATE())%> 
    по: <%=Formatter.format(w.getEND_DATE())%></h4><%}%>
    <table class="regular">
    <thead>
    <tr><th>Тип индикативной ставки</th><th>Переменная часть ставки</th><th>Фиксированная часть ставки</th><th>Результирующая ставка</th></tr>
    </thead>
    <tbody>
    <tr><td><%=w.getWRKLIBORSROK() %></td>
    <td><%=w.getSTAVFLOATFIXEDWRK() %></td>
    <td><%=w.getSTAVRAZWRK() %></td>
    <td><%=w.getSTPLAVWRK() %></td></tr>
    </tbody>
    </table>
    Вид обеспечения: <%=w.getVIDOBESP() %>
    <%}%>
<%}%>
