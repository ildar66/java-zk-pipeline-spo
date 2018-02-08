<%@page import="ru.md.helper.ExpertiseValueHelper"%>
<%@page import="org.uit.director.tasks.AttributesStructList"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="org.uit.director.tasks.TaskInfo"%>
<%@page import="ru.md.jsp.tag.IConst_PUP"%>
<%@page import="org.uit.director.db.dbobjects.AttributeStruct"%>
<%@page import="org.uit.director.db.dbobjects.Attribute"%>
<%@page import="org.uit.director.db.dbobjects.BasicAttribute"%>
<%@page import="java.util.Iterator"%>
<%@page import="ru.md.helper.TaskHelper" %>
<%@page import="ru.md.spo.ejb.PupFacadeLocal" %>
<%@ page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<script type="text/javascript" src="scripts/wz_tooltip/wz_tooltip.js"></script>
<%
try {
    PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
    TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
	TaskInfo taskInfo = (TaskInfo) request.getAttribute(IConst_PUP.TASK_INFO);
	AttributesStructList attrs = taskInfo==null?null:taskInfo.getAttributes();
	boolean expertDecisionMode = false;//VTBSPO-1457
	boolean editMode = TaskHelper.isEditMode("",request);
	try{
		if(editMode && taskInfo != null && taskInfo.getNameStageTo()!=null){
		    String nameStage = taskInfo.getNameStageTo();
			String processTypeName = taskFacadeLocal.getTaskByPupID(taskInfo.getIdProcess()).getProcessTypeName();
			if (!processTypeName.contains("Ген. соглашение"))
				if( nameStage.startsWith("Получение решения УО/УЛ и направление запросов на проведение экспертиз")
					||nameStage.startsWith("Акцепт перечня дополнительных экспертиз")
					||nameStage.startsWith("Получение результатов экспертиз")
					||nameStage.startsWith("Получение решения УО/УЛ и направление запроса на экспертизу правового статуса")
					||nameStage.equals("Акцепт экспертизы правового статуса"))
						expertDecisionMode = true;
		}
	} catch (Exception e) {
	    //out.println("Ошибка в секции requestExpertise.jsp относительно expertDecisionMode:" + e.getMessage());
	    e.printStackTrace();
    }
	if (attrs != null  && taskInfo != null) {
		%>
		<div class="content naExpertizy" id="naExpertizy">
		<% 
			Object anyAttr = null;
			Attribute attr = null;
			Iterator<BasicAttribute> it = ExpertiseValueHelper.getSortedAttributes(attrs.getIterator());
			int index = 0;
			boolean show_expertise = false;
			boolean garanty = true;//Требуется экcпертиза гарантий
			boolean accredetive = false;//Требуется экcпертиза аккредитивов
			while (it.hasNext()) {
				anyAttr = it.next();
				if (anyAttr instanceof AttributeStruct) {
					attr = ((AttributeStruct)anyAttr).getAttribute();
					if (attr.isPermissionAdditionView()) {
					    index++;
					    if(index>5){//разбивка по колонкам
					        index=1;
					        %></div><div class="content naExpertizy"><%
					    }
						boolean edit=attr.isPermissionEdit()&editMode;
						String typeElement = attr.getAddition();
						String name = attr.getName();
						String tooltip = attr.getDescription();
						if (tooltip.equals(""))tooltip=name;
						if ("1".equalsIgnoreCase(typeElement)) {
							boolean valBox = attr.getValueAttributeBoolean().booleanValue();
							%>
								<div>
								<!-- Галочка -->
									<input <%if (valBox) {%>checked <%}%> <%if(!edit){ %>disabled <%} %> type="checkbox" onclick="setCheckBoxValue(this, '<%=attr.getNameVariable()%>')"/> <%=name%>
									<%if(edit){ %><input type="hidden" name="<%=attr.getNameVariable()%>"  value="<%=attr.getValueAttributeBoolean().booleanValue()%>"/><%} %>
								</div>
							<%
						}
						if ("100".equalsIgnoreCase(typeElement) || typeElement.startsWith("100;")) {//Jira VTBSPO-707 && VTBSPO-1595
						    boolean valBox = ExpertiseValueHelper.getVal(name, taskInfo.getIdProcess());
							boolean activeExpertise = ExpertiseValueHelper.isActiveExpertise(name, taskInfo.getIdProcess());
						    if(activeExpertise || TaskHelper.blockExpertise(request, name)){
						        edit = false;
								if(!name.contains("меморандум"))
						        	pupFacadeLocal.updatePUPAttribute(taskInfo.getIdProcess(), name, "false");
						    }
						    
						    if (edit || valBox || activeExpertise || TaskHelper.blockExpertise(request, name)){
							%>
	<div class="expertizy_checkbox">
									<p style="display:none;">
										<!-- Да/нет -->
										<input id="<%=name %>t" <%if (valBox) {%>checked <%} if(!edit){%>disabled <%}%> 
										type="radio" 
										onclick="updateRelatedData($(this).attr('name'));<%if(name.startsWith("Требуется экспертиза")) {%>checkExpertus();<%} %>" 
										name="<%=attr.getNameVariable()%>" value="true"
										<%if(name.startsWith("Требуется экспертиза")) {%> class="expertus" <%} %>>Да
										&nbsp;&nbsp;
										<input id="<%=name %>f" <%if (!valBox) {%>checked <%} if(!edit){%>disabled <%}%> 
										type="radio" onclick="updateRelatedData($(this).attr('name'));<%if(name.startsWith("Требуется экспертиза")) {%>checkExpertus();<%} %>" 
										name="<%=attr.getNameVariable()%>" value="false">Нет
									</p>

									<input id="<%=name %>Visible"
										<%if (valBox) {%>checked="checked" <%} if(!edit){%>disabled="disabled" <%}%>
										type="checkbox" 
										onclick="try{ var el=document.getElementById('<%=name %>' + (this.checked ? 't':'f')); el.checked='checked';updateRelatedData($(el).attr('name'));<%if(name.startsWith("Требуется экспертиза")) {%>checkExpertus();<%} %> } catch(err2) {alert('js err when click expertizy_checkbox: ' + err2);}" />
								
									<span onmouseover="Tip('<%=name %>')" onmouseout="UnTip()"><%=name %></span>
								</div>
							<%}
						}
						if ("101".equalsIgnoreCase(typeElement)) {//Jira VTBSPO-707
							%>
								<div>
									<!-- Полон/не полон -->
									<div onmouseover="Tip('<%=name %>')" onmouseout="UnTip()"><%=name %></div>
									<% boolean valBox = attr.getValueAttributeBoolean().booleanValue();%>
									<p><input <%if (valBox) {%>checked <%} if(!edit){%>disabled <%}%> type="radio" name="<%=attr.getNameVariable()%>" value="true">Полон</p>
									<p><input <%if (!valBox) {%>checked <%} if(!edit){%>disabled <%}%> type="radio" name="<%=attr.getNameVariable()%>" value="false">Не полон</p>
								</div>
							<%
						}
						if ("107".equalsIgnoreCase(typeElement)) {
						  if (edit||!pupFacadeLocal.getPUPAttributeValue(taskInfo.getIdProcess(),name).isEmpty()){
							%>
								<div>
									<div onmouseover="Tip('<%=name %>')" onmouseout="UnTip()"><%=name %></div>
									<% boolean valBox = attr.getValueAttributeBoolean().booleanValue();%>
									<p><input <%if (valBox) {%>checked <%} if(!edit){%>disabled <%}%> type="radio" name="<%=attr.getNameVariable()%>" value="true">с замечаниями</p>
									<p><input <%if (!valBox) {%>checked <%} if(!edit){%>disabled <%}%> type="radio" name="<%=attr.getNameVariable()%>" value="false">без замечаний</p>
								</div>
							<%}
						}
						if ("201".equalsIgnoreCase(typeElement)) {//Jira VTBSPO-1292
							String valBox = pupFacadeLocal.getPUPAttributeValue(taskInfo.getIdProcess(),name);
							%>
								<div>
									<div onmouseover="Tip('<%=name %>')" onmouseout="UnTip()"><%=name %></div>
									<p><input <%if (valBox.equals("2")) {%>checked <%} if(!edit){%>disabled <%}%> type="radio" name="<%=attr.getNameVariable()%>201" value="2">отправить на доработку</p>
									<p><input <%if (valBox.equals("1")) {%>checked <%} if(!edit){%>disabled <%}%> type="radio" name="<%=attr.getNameVariable()%>201" value="1">направить на согласование в Мидл-офис</p>
								</div>
							<%
						}
						if ("219".equalsIgnoreCase(typeElement)) {//Jira VTBSPO-1633
							String valBox = pupFacadeLocal.getPUPAttributeValue(taskInfo.getIdProcess(),name);
							%>
								<div>
									<div onmouseover="Tip('<%=name %>')" onmouseout="UnTip()"><%=name %></div>
									<p><input <%if (valBox.equals("1")) {%>checked <%} if(!edit){%>disabled <%}%> type="radio" name="<%=attr.getNameVariable()%>219" value="1">Да</p>
									<p><input <%if (valBox.equals("0")) {%>checked <%} if(!edit){%>disabled <%}%> type="radio" name="<%=attr.getNameVariable()%>219" value="0">Нет</p>
								</div>
							<%
						}
						if ("199".equalsIgnoreCase(typeElement)) {//Jira VTBSPO-707
							//здесь два атрибута по цене одного
							show_expertise = true;
							if (name.equals("Требуется экспертиза гарантии"))
								garanty=attr.getValueAttributeBoolean().booleanValue();
							if (name.equals("Требуется экспертиза аккредитивы"))
								accredetive=attr.getValueAttributeBoolean().booleanValue();
						}
						if ("215".equalsIgnoreCase(typeElement)) {
                          if (edit||!pupFacadeLocal.getPUPAttributeValue(taskInfo.getIdProcess(),name).isEmpty()){
                            %>
                                <div>
                                    <div onmouseover="Tip('<%=name %>')" onmouseout="UnTip()"><%=name %></div>
                                    <% String valBox = pupFacadeLocal.getPUPAttributeValue(taskInfo.getIdProcess(),name);%>
                                    <p><input <%if (valBox.equals("1")) {%>checked <%} if(!edit){%>disabled <%}%> type="radio" name="<%=attr.getNameVariable()%>" value="1">Утвердить и передать на решение</p>
                                    <p><input <%if (valBox.equals("2")) {%>checked <%} if(!edit){%>disabled <%}%> type="radio" name="<%=attr.getNameVariable()%>" value="2">Отправить на доработку</p>
                                </div>
                            <%}
                        }
					}
				}
			}
			if (show_expertise){
				%>
					<div class="guarantee">
						<div onmouseover="Tip('Экспертизы гарантий и аккредитивы')" onmouseout="UnTip()">Экспертизы гарантий и аккредитивы</div>
						<p><input onclick="document.getElementById('garantyexp').value='true';document.getElementById('accredetiveexp').value='false';"
						 <%if (garanty) {%>checked <%}%> 
						 <%if(!editMode){%>disabled <%}%>
						 type="radio" name="difficultcheckbox" value="1">Требуется экспертиза гарантии</p>
						 
						<p><input onclick="document.getElementById('garantyexp').value='false';document.getElementById('accredetiveexp').value='true';"
							<%if (accredetive) {%>checked <%}%>
							<%if(!editMode){%>disabled <%}%>
							type="radio" name="difficultcheckbox" value="2"
							>Требуется экспертиза аккредитивы</p>
						
						<p><input onclick="document.getElementById('garantyexp').value='false';document.getElementById('accredetiveexp').value='false';" 
							<%if (!garanty&&!accredetive) {%>checked <%}%>
							<%if(!editMode){%>disabled <%}%>
							type="radio" name="difficultcheckbox" value="3">Не требуются</p>
						
						<input type="hidden" id="garantyexp" name="Требуется экспертиза гарантии"  value="<%=garanty?"true":"false"%>"/>
						<input type="hidden" id="accredetiveexp" name="Требуется экспертиза аккредитивы"  value="<%=accredetive?"true":"false"%>"/>
					</div>
				<%
			}
		%>
		</div>
		<script type="text/javascript">
		<%if(expertDecisionMode){%>
		$(document).ready(function() {
		    checkExpertus();
		});
		function checkExpertus() {
		    var requestAddExpEmpty=<%=pupFacadeLocal.getPUPAttributeValue(taskInfo.getIdProcess(), "Требуется обновление списка дополнительных экспертиз").isEmpty()%>;
		    if($('.expertus:checked').size()>0 || !requestAddExpEmpty) {
		        $('#btnRegister').show();
		        $('#acceptlink').hide();
		        $('#refuselink').hide();
		    } else {
		        $('#btnRegister').hide();
		        $('#acceptlink').show();
                $('#refuselink').show();
		    }
		}
		<%} else {%>
		function checkExpertus() {}
		<%}%>
		</script>
	<%
	}
} catch (Exception e) {
	out.println("Ошибка в секции requestExpertise.jsp:" + e.getMessage());
	e.printStackTrace();
}
%>