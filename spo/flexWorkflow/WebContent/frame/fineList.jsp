<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN">
<%@page import="com.vtb.util.Formatter"%>
<%@page import="ru.masterdm.compendium.domain.spo.PunitiveMeasure"%>
<%@page import="ru.md.spo.dbobjects.PunitiveMeasureJPA"%>
<%@page isELIgnored="true" %>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%@page import="com.vtb.domain.Task"%>
<%@page import="com.vtb.domain.Fine"%>
<%@page import="java.util.ArrayList"%>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@page import="ru.md.helper.TaskHelper" %>
<%TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
Task task=TaskHelper.findTask(request);
boolean readOnly = !TaskHelper.isEditMode("Стоимостные условия",request) || request.getParameter("monitoringmode")!=null;
ArrayList<Fine> fineList = task.getFineList();
ru.md.spo.dbobjects.TaskJPA taskJPA = taskFacadeLocal.getTask(task.getId_task());
boolean isCanEditFund = TaskHelper.isCanEditFund(request);
%>
<%if(fineList.size()==0 &&readOnly){ %>Нет санкций<% } %>
<%if (!readOnly || isCanEditFund) { %><input type="hidden" name="fineList_section" value="YES" /><% } %>

<script type="text/javascript" src="scripts/jquery/jquery.textarea-expander.js"></script>

<table class="regular" style="width: 100%;">
    <tr align="left">
    	<th>
	    	Надбавка к процентной ставке за поддержание <br />
	    	кредитовых оборотов менее установленного размера
    	</th>
    	<td id="compare_price_turnover">
    		<md:inputMoney name="rate2" value="<%=Formatter.format(taskJPA.getRate2()) %>" readonly="<%=readOnly && !isCanEditFund%>" onChange="fieldChanged(this)" /> % годовых
    	</td>
    	<td>
    		<span class="comment_header">Комментарии:</span>
    		<span style="display:block;">
    		<%if(!readOnly){%>
                <textarea 
                	id="rate2Note"
                	name="rate2Note"
                	style="width:98%;" 
                	class="expand50-200"
                    onkeyup="fieldChanged(this);" ><%=(taskJPA.getRate2Note()==null)?"":taskJPA.getRate2Note()%></textarea>
                <a href="javascript:;" 
                 class="dialogActivator" 
                 dialogId="select_rate2_note"
                 onclick="$('#rate2NoteDic').val('rate2Note');"><img alt="выбрать из шаблона" src="style/dots.png"></a>
            <%} else {%>
                <span><%=(taskJPA.getRate2Note()==null)?"":taskJPA.getRate2Note()%></span>
            <%} %>
            </span>
    	</td>
    </tr>
</table>
<table id="testShtrafiTableId" class="regular add" style="width: 99%;">
<thead>
    <tr>
        <th style="width:30%">Наименование санкции (Тип штрафной санкции)</th>
        <th style="width:30%">Величина санкции (неустойки, штрафа, пени и т.д.), Валюта / %</th>
        <th style="width:20%">Период оплаты</th>
        <th style="width:19%">Увеличивает ставку по сделке</th>
        <th style="width:1%"></th>
    </tr>
</thead>
<tbody>
<%try{
for (int j=0; j<fineList.size(); j++) {
Fine fine = (Fine)fineList.get(j);
String cl = "PunitiveMeasure"+j;
String cl2=cl+" text money";
String display = fine.getDescription().isEmpty()?"display:inline":"display:inline";
String displayValueText = !fine.getDescription().isEmpty()?"display:inline":"display:inline";
String valPunitiveMeasureId = "valPunitiveMeasure"+j;
String curPunitiveMeasureId = "curPunitiveMeasure"+j;
%>
    <tr>
        <td id="compare_price_fine<%=fine.getId()%>">
            <%if(!readOnly){%>
                <textarea id="PunitiveMeasure<%=j %>" name="Штрафные санкции" style="width:98%;" 
                    onkeyup="fieldChanged(this);" ><%=fine.getPunitiveMeasure()%></textarea>
                <a href="javascript:;" class="dialogActivator" dialogId="punitiveMeasureSelectTemplateDiv" onclick="punitiveMeasureSelectTemplate('PunitiveMeasure<%=j %>','punitiveMeasureSelectTemplateDiv');">
                    <img alt="выбрать из шаблона" src="style/dots.png" /></a>
            <%} else {%>
                <span><%=(fine.getPunitiveMeasure()==null)?"":fine.getPunitiveMeasure()%></span>
            <%} %>
       </td>
       <td>
         <div id="compare_price_fine<%=fine.getId()%>_desc" class="compare-elem">
           <%if(readOnly){ %><%=fine.getDescription() %><%}else{ %>
           <textarea id="DescPunitiveMeasure<%=j %>" name="fine_value_text" onkeyup="fieldChanged(this)" style="<%=displayValueText %>"><%=fine.getDescription() %></textarea>
           <%} %>
         </div>
           <input id="dPunitiveMeasure<%=j %>" name="descPunitiveMeasure" type="hidden" value="<%=fine.getDescription() %>">
         <div id="compare_price_fine<%=fine.getId()%>_value" class="compare-elem">
           <md:inputMoney name="fine_value" styleClass="<%=cl2 %>" style="<%=display %>"
               id="<%=valPunitiveMeasureId %>" onChange="fieldChanged(this)"
               value="<%=fine.getFormattedValue()%>" readonly="<%=readOnly%>" />
         </div>
         <div id="compare_price_fine<%=fine.getId()%>_curr" class="compare-elem">
           <md:currency readonly="<%=readOnly %>" value="<%=fine.getCurrencyCode() %>" with_empty_field="true" id="<%=curPunitiveMeasureId %>"
               name="fine_currency" withoutprocent="false" styleClass="<%=cl %>" style="<%=display %>" with365="true" onChange="fieldChanged(this)" />
         </div>
           <input type="hidden" name="fine_id_punitive_measure" value="<%=Formatter.str(fine.getId_punitive_measure())%>" id="idDictPunitiveMeasure<%=j %>">
       </td>
       <td id="compare_price_fine<%=fine.getId()%>_period">
           <md:inputInt name="fine_period" readonly="<%=readOnly%>" value="<%=fine.getFormattedPeriod() %>" onChange="fieldChanged(this)" />
           <select name="fine_periodtype" <%=(readOnly ? "DISABLED" : "") %> onChange="fieldChanged(this)">
               <option value=""></option>
               <option value="workdays" <%if(fine.getPeriontype().equals("workdays")){ %>selected<%} %>>рабочих дней</option>
               <option value="alldays" <%if(fine.getPeriontype().equals("alldays")){ %>selected<%} %>>календарных дней</option>
           </select>
       </td>
       <td id="compare_price_fine<%=fine.getId()%>_rate_enlarge"><input type="checkbox" <%if(fine.isProductRateEnlarge()){ %>checked="checked"<%} %> <%if ((readOnly)) {%>DISABLED<%} %>
       onclick="if(this.checked){$(this).parent().find('input[name=fine_productrate]').val('y');}else{$(this).parent().find('input[name=fine_productrate]').val('n');}" />
       <input name="fine_productrate" type="hidden" value="<%=fine.isProductRateEnlarge()?"y":"n" %>" >
       </td>
       <td></td>
        <%if ((!readOnly)) {%><td class="delchk" style="width: 13px;"><input type="checkbox" name="testShtrafiRowChk"/></td><%}%>
    </tr>
<%}} catch (Exception e) {out.println("Ошибка в секции  frame_priceConditionLimit.jsp:" + e.getMessage());e.printStackTrace();} %>
</tbody>
<tfoot>
    <tr>
        <td class="compare-list-removed" id="compare_list_price_fine" colspan="5"></td>
    </tr>
<%if ((!readOnly)) {%>
    <tr>
        <td class="add" colspan="5">
            <button onmouseover="Tip(getToolTip('Добавить штрафную санкцию'))" onmouseout="UnTip()" onclick="insertPunitiveMeasureTR('','punitiveMeasureSelectTemplateDiv'); dialogHandler();fieldChanged();return false;" class="add"></button>
            &nbsp;
            <button onmouseover="Tip(getToolTip('Удалить отмеченное'))" onmouseout="UnTip()" onclick="DelRowWithLast('testShtrafiTableId', 'testShtrafiRowChk'); return false;" class="del"></button>
        </td>
    </tr>
<%}%>
</tfoot>
</table>

<div id="punitiveMeasureSelectTemplateDiv" title="Выбрать санкцию" style="display: none;">
	<ol>
		<%for(PunitiveMeasureJPA pm : taskFacadeLocal.findPunitiveMeasure(PunitiveMeasure.SanctionType.VALUESANCTION.getDescription())){ %>
		    <li>
		        <a href="javascript:;" class="disable-decoration" onclick="onPunitiveMeasureSelectTemplateClick('<%=pm.getName_measure() %>','<%=pm.getSumdesc()%>','<%=pm.getId()%>','<%=pm.getSumFormated()%>','<%=pm.getCurrency()%>');">
		            <%=pm.getName_measure() %>
		        </a>
		    </li>
		<%}%>
	</ol>
</div>


