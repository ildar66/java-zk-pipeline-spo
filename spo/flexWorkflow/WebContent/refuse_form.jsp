<%@page	language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@page import="ru.masterdm.compendium.domain.crm.StatusReturn" %>
<%@page import="com.vtb.util.Formatter" %>
<%@page import="ru.masterdm.compendium.model.CompendiumCrmActionProcessor"%>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@ page import="ru.md.helper.TaskHelper" %>
<%@ page import="ru.md.domain.MdTask" %>
<%@ page import="ru.masterdm.spo.utils.SBeanLocator" %>

<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<html:html>
<%
	response.addHeader("Pragma", "no-cache");
	response.addHeader("Expires", "-1");
	response.addHeader("Cache-control", "no-cache");

	CompendiumCrmActionProcessor compenduim = (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
Long processTypeId = request.getParameter("processType")==null?null:Long.valueOf(request.getParameter("processType"));
TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
	MdTask mdtask = TaskHelper.getMdTask(request);
boolean accept = request.getParameter("accept")!=null;
StatusReturn[] statusReturnList = compenduim.findStatusReturn(accept?"1":"0");
String onClick = "";
if(request.getParameter("without_form")!=null){
    if(accept){onClick = "accept_button_click()";}
    else{onClick = "refuse_button()";}
}
 %>
<div id="statusReturn" style="overflow:hidden">
	<%if(request.getParameter("without_form")==null){ %><form class="refuse" action="clientRefuse.do" method="post"><%} %>
		<h2>Решение</h2>
		<input type="hidden" id="mdtaskid" name="mdtaskid" value="<%=request.getParameter("mdtaskid") %>">
		<label>Дата решения
			<md:calendarium name="refuse_date" id="refuse_date" readonly="false" value="<%=Formatter.format(new java.util.Date()) %>"/>
		</label><br /><br />
		Статус решения
			<%if(request.getParameter("mo")==null){%>
			<select name="StatusReturn" id="StatusReturn">
				<%for(StatusReturn statusReturn:statusReturnList){ %>
					<option value="<%=statusReturn.getId() %>"><%=statusReturn.getDescription() %></option>
				<%} %>
			</select>
			<%}else{%>
				<select name="nameStatusReturn" id="nameStatusReturn">
					<option value=""></option>
				<%for(String name : SBeanLocator.singleton().compendium().getMoStatusReturnList()){%>
					<option value="<%=name %>"><%=name %></option>
				<%}%>
				</select>
			<%}%>
		<br />
		<%if(processTypeId!=null){ %>
		Уполномоченное лицо
			<select name="abody" id="abody">
				<option value=""></option>
				<%for(ru.md.spo.dbobjects.AuthorizedPersonJPA a:taskFacadeLocal.getAuthorizedPersonJPAList(processTypeId)){ %>
					<option value="<%=a.getId() %>"><%=a.getDisplayName()%></option>
				<%} %>
			</select>
		<%} %>
	<%if(accept && TaskHelper.showSpecialDecision(mdtask)){%>
	<div id="section_decision_accept_div">
		<table class="regular"><tbody>
		<tr><th>Требуется формирование Дополнительного соглашения</th>
			<td><input type="checkbox" value="1" name="additional_contract_decision">
			</td></tr>
		<tr><th>Решение влияет на мониторинг Сделки</th>
			<td><input type="checkbox" value="1" name="product_monitoring_decision">
			</td></tr>
		</tbody>
		</table>
		<input type="hidden" name="section_decision">
	</div>
	<%}%>
		<div style="clear:both">
			<label>Текст решения</label>
			<textarea style="height: 350px; width: 100%;" name="StatusReturnText" id="StatusReturnText" class="advanced_textarea"></textarea>
		</div>
		<div class="commands">
			<input type="submit" class="refuse button" value="Подтвердить решение" onClick="<%=onClick.equals("")?"return refuseFormValidate()":onClick %>">
			<button onclick="$.fancybox.close();return false;">Отмена</button>
		</div>
    <div id="refuse_form_error_message"  class="error" style="display:none;">ошибка</div>
	<%if(request.getParameter("without_form")==null){ %></form><%} %>
</div>
	<script type="text/javascript">
		calendarInit();
        function refuseFormValidate() {
            if($('#StatusReturnText').val().length >= 3700){
                $('#refuse_form_error_message').text('Превышен размер поля "Текст решения" 4000 символов.');
                $('#refuse_form_error_message').show();
                return false;
            }
            if($('#nameStatusReturn').val()==''){
                $('#refuse_form_error_message').text('Атрибут "Статус решения" обязателен для заполнения');
                $('#refuse_form_error_message').show();
                return false;
            }
            return true;
        }
	</script>
</html:html>
