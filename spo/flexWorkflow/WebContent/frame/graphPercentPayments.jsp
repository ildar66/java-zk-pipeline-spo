<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%@page import="com.vtb.model.TaskActionProcessor"%>
<%@page import="com.vtb.domain.Task"%>
<%@page import="com.vtb.domain.InterestPay"%>
<%@page import="com.vtb.model.ActionProcessorFactory"%>
<%@ page import="ru.md.helper.TaskHelper" %>
<%
boolean readOnly = !TaskHelper.isEditMode("R_Стоимостные условия", request);
TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
Task task=processor.getTask(new Task(Long.valueOf(request.getParameter("mdtaskid"))));
InterestPay ip = task.getInterestPay();
String dateAddition = "constraints=\"{datePattern:'dd.MM.yyyy', strict:true}\"";
String numberAddition = "constraints=\"{places:2}\" promptMessage=\"\" required=\"true\" invalidMessage=\"Числа должы быть в виде 99\"";
%>
<script type="text/javascript" src="scripts/jquery/jquery.textarea-expander.js"></script>

<table id="graphPercentPaymentsTableId" class="regular">
    <tbody>
    <%try{ %>
        <tr>
            <td colspan="1">Периодичность</td>
            <td id="compare_graph_percent_period" colspan="3">
                 <%if(!readOnly){%>
                    <textarea name="pay_int" onkeyup="fieldChanged(this)" id="pay_int"><%=ip.getPay_int()%></textarea>
                    <a href="javascript:;" class="dialogActivator" dialogId="select_pay_int">
					    <img alt="выбрать из шаблона" src="style/dots.png"></a>
                <%} else {%>
                    <span><%=ip.getPay_int()%></span>
                <%}%>
            </td>
        </tr>
        <tr>
            <td>От даты (дата первой оплаты)</td>
            <td id="compare_graph_percent_firstDay" colspan="3">
                <md:input name="int_pay Дата первой оплаты процентов" value="<%=ip.getFirstPayDateFormatted()%>" addition="<%=dateAddition%>" readonly="<%=readOnly%>" 
                id="int_pay_firstDay" onFocus="displayCalendarWrapper('int_pay_firstDay', '', false); return false;" onkeyup="fieldChanged(this)" styleClass="text date" onBlur="input_autochange(this,'date')" />
                <%if(readOnly){%>
                <%=(ip.getFirstPayDateComment()==null)?"":ip.getFirstPayDateComment()%>
                <%}else{%>
                <textarea name="FirstPayDateComment" onkeyup="fieldChanged(this)" class="expand50-200" style="margin-top: 5px;"><%=(ip.getFirstPayDateComment()==null)?"":ip.getFirstPayDateComment()%></textarea>
                <%}%>
            </td>
        </tr>
        <tr>
        	 <td>До даты (дата последней оплаты)</td>
            <td id="compare_graph_percent_finalDay" colspan="3">
                <md:input name="int_pay Дата окончательного погашения процентов" value="<%=ip.getFinalPayDateFormatted()%>" addition="<%=dateAddition%>" readonly="<%=readOnly%>" 
                id="int_pay_finalDay" onFocus="displayCalendarWrapper('int_pay_finalDay', '', false); return false;" onkeyup="fieldChanged(this)" styleClass="text date" onBlur="input_autochange(this,'date')" />
            </td>
        </tr>
        <tr>
            <td colspan="1">Число уплаты процентов</td>
            <td id="compare_graph_percent_int_pay">
                <md:input name="int_pay Число уплаты процентов" value="<%=ip.getNumDayAsString()%>" addition="<%=numberAddition%>" readonly="<%=readOnly%>" 
                id="int_pay_numPay" styleClass="money" onBlur="input_autochange(this,'digitsSpaces');checkErrors(this)" 
                />
            </td>
            <td id="compare_graph_percent_finalPay" colspan="2">
                <input type="checkbox" <%if(ip.isFinalPay()){%>checked="checked"<%}%> 
                <%if(readOnly){%>disabled="disabled"<%}%>
                onclick="if(this.checked){document.getElementById('int_pay_finalPay').value='y';} else {document.getElementById('int_pay_finalPay').value='n';}" >
                Последняя оплата в дату фактического погашения задолженности по основному долгу
                <input type="hidden" id="int_pay_finalPay" name="int_pay Последняя оплата в дату фактического погашения задолженности" 
                       value="<%=ip.isFinalPay()?"y":"n"%>" >
            </td>
        </tr>   
        <tr>
            <td colspan="1">Порядок погашения процентов</td>
            <td id="compare_graph_percent_order_pay" colspan="3">
                <%if(!readOnly){%>
                    <textarea onkeyup="fieldChanged(this)" name="int_pay Комментарии к графику погашения процентов"><%=(ip.getDescription()==null)?"":ip.getDescription()%></textarea>
                <%} else {%>
                    <span><%=(ip.getDescription()==null)?"":ip.getDescription()%></span>
                <%}%>
            </td>
        </tr>
        <tr>
            <td colspan="1">Комментарии</td>
            <td id="compare_graph_percent_comment" colspan="3">
                <%if(!readOnly){%>
                    <textarea name="int_pay Комментарии" onkeyup="fieldChanged(this)"><%=(ip.getComment()==null)?"":ip.getComment()%></textarea>
                <%} else {%>
                    <span><%=(ip.getComment()==null)?"":ip.getComment()%></span>
                <%}%>
            </td>
        </tr>
        <%}catch (Exception e) {out.println("Ошибка в секции  frame_priceCondition.jsp:" + e.getMessage());e.printStackTrace();} %>
    </tbody>
</table>
<script type="text/javascript">
	if ($('#lastApprovedVersion').val() != "" && prevApprovedDiffShown)
		displayPrevApprovedDiff();
</script>