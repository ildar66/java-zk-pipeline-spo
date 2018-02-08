<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="ru.md.helper.TaskHelper"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%@page import="com.vtb.model.TaskActionProcessor"%>
<%@page import="com.vtb.domain.Task"%>
<%@page import="com.vtb.domain.PrincipalPay"%>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@page import="com.vtb.model.ActionProcessorFactory"%>
<%
boolean readOnly = !TaskHelper.isEditMode("R_Стоимостные условия",request);
TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
Task task=processor.getTask(new Task(Long.valueOf(request.getParameter("mdtaskid"))));
String dateAddition = "constraints=\"{datePattern:'dd.MM.yyyy', strict:true}\"";
String numberAddition = "constraints=\"{places:2}\" promptMessage=\"\" required=\"true\" invalidMessage=\"Числа должы быть в виде 99\"";
PrincipalPay pp = task.getPrincipalPay();
TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
ru.md.spo.dbobjects.TaskJPA taskJPA = taskFacadeLocal.getTask(task.getId_task());
boolean isCanEditFund = TaskHelper.isCanEditFund(request);
%>

<%String principalPeriod_Order = (pp.getPeriodOrder()== null) ? "-1" : pp.getPeriodOrder().getId();%>
<table id="graphPrincipalPaymentsTableId" class="regular">
    <tbody>
    <%try{ %>
        <tr>
            <td colspan="1">Периодичность погашения основного долга</td>
            <td id="compare_graph_principal_period" colspan="1">
                <md:CRMRepayment style="width:20em;"  readonly="<%=readOnly && !isCanEditFund%>" 
                    id="principalPeriod_Order" name="prncp Периодичность погашения ОД"
                                 onChange="on_principalPeriod_Order_change()"
                    value="<%=principalPeriod_Order%>"  />
            </td>
            <td id="compare_graph_principal_isfirstPay" colspan="2">
                <input type="checkbox" <%if(pp.isFirstPay()){%>checked="checked"<%}%> 
                <%if(readOnly){%>disabled="disabled"<%}%>
                onclick="if(this.checked){document.getElementById('prncp_firstPay').value='y';} else {document.getElementById('prncp_firstPay').value='n';};fieldChanged(this);" >
                Первый платеж в месяце выдачи
                <input type="hidden" id="prncp_firstPay" name="prncp Первый платеж в месяце выдачи" 
                       value="<%=pp.isFirstPay()?"y":"n"%>" >
            </td>
        </tr>
        <tr>
            <td colspan="1">От даты (дата первой оплаты)</td>
            <td id="compare_graph_principal_firstPay">
                <md:input name="prncp Дата первой оплаты ОД" value="<%=pp.getFirstPayDateFormatted()%>" addition="<%=dateAddition%>" readonly="<%=readOnly%>" 
                id="prncp_firstDay" onFocus="displayCalendarWrapper('prncp_firstDay', '', false); return false;" onChange="fieldChanged(this)" styleClass="text date" onBlur="input_autochange(this,'date')" />
            </td>
            <td>Дата окончательного погашения ОД</td>
            <td id="compare_graph_principal_finalDay">
                <md:input name="prncp Дата окончательного погашения ОД" value="<%=pp.getFinalPayDateFormatted()%>" addition="<%=dateAddition%>" readonly="<%=readOnly%>" 
                id="prncp_finalDay" onFocus="displayCalendarWrapper('prncp_finalDay', '', false); return false;" onChange="fieldChanged(this)" styleClass="text date" onBlur="input_autochange(this,'date')" />
            </td>
        </tr>
        <tr id="graphPrincipalPayments_sum_tr">
            <td colspan="1">Сумма, Валюта платежа</td>
            <td>
	            <div id="compare_graph_principal_sum">
	                <md:inputMoney name="prncp Сумма платежа ОД" value="<%=pp.getAmountAsString()%>" addition="<%=numberAddition%>" readonly="<%=readOnly && !isCanEditFund%>" 
	                			   id="prncp_numPay" styleClass="money" onBlur="input_autochange(this,'money')" />
	            </div>
	            &nbsp;
	            <span id="compare_graph_principal_curr"><md:currencyParent name="prncp Валюта" withoutprocent="true" 
                      readonly="<%=readOnly && !isCanEditFund%>" value="<%=pp.getCurrency()%>" 
                      parentTask="<%=task.getId_task()%>" />
                      
                </span> 
            </td>
            <td id="compare_graph_principal_finalPay" colspan="2">
                <input type="checkbox"
                    <%if(pp.isDepended()){%>checked="checked"<%}%> 
                    <%if(readOnly){%>disabled="disabled"<%}%>
                    onclick="if(this.checked){document.getElementById('prncp_finalPay').value='y';} else {document.getElementById('prncp_finalPay').value='n';};fieldChanged(this);" >
                Сумма платежа зависит от задолженности на дату окончания срока использования
                <input type="hidden" id="prncp_finalPay" name="prncp Сумма платежа зависит от задолженности на дату окончания срока использования" 
                       value="<%=pp.isDepended()?"y":"n"%>" >
            </td>
        </tr>
        <tr>
            <td colspan="1">Амортизация ставки</td>
            <td id="compare_graph_principal_amortized_loan" colspan="3">
                <input name="amortized_loan" id="amortized_loan" type="checkbox" value="y" 
                <%=taskJPA.isAmortized_loan()?"checked":"" %> onclick="onAmortized_loanClick();fieldChanged(this);" <%=readOnly && !isCanEditFund ? "disabled" : "" %>>
            </td>
        </tr>   
        <tr>
            <td colspan="1">Порядок погашения</td>
            <td id="compare_graph_principal_order_desc" colspan="3">
                <%if(!readOnly){%>
                    <textarea name="prncp Комментарии к графику погашения ОД" onkeyup="fieldChanged(this)"><%=(pp.getDescription()==null)?"":pp.getDescription()%></textarea>
                <%} else {%>
                    <span><%=(pp.getDescription()==null)?"":pp.getDescription()%></span>
                <%}%>
            </td>
        </tr>
        <tr>
            <td colspan="1">Комментарии</td>
            <td id="compare_graph_principal_comment" colspan="3">
                <%if(!readOnly){%>
                    <textarea name="prncp Комментарии" onkeyup="fieldChanged(this)"><%=(pp.getComment()==null)?"":pp.getComment()%></textarea>
                <%} else {%>
                    <span><%=(pp.getComment()==null)?"":pp.getComment()%></span>
                <%}%>
            </td>
        </tr>
        <%}catch (Exception e) {out.println("Ошибка в секции  graphPrincipalPayments.jsp:" + e.getMessage());e.printStackTrace();} %>
    </tbody>
</table>
<script language="javaScript">
    function on_principalPeriod_Order_change(){
        if($('#principalPeriod_Order  option:selected').text().indexOf("(Период)") > -1){
            $('#graphPrincipalPayments_sum_tr').hide();
        } else {
            $('#graphPrincipalPayments_sum_tr').show();
        }
    }
    $(document).ready(function() {
        on_principalPeriod_Order_change();
    });
</script>
