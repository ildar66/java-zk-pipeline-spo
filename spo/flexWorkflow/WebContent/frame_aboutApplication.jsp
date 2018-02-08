<%@page import="org.uit.director.tasks.TaskInfo"%>
<%@page import="com.vtb.util.Formatter"%>
<%@page import="ru.md.helper.TaskHelper" %>
<%@page import="java.util.logging.*"%>
<%@page import="java.util.List"%>
<%@page import="java.util.Arrays"%>
<%@page import="ru.md.spo.ejb.PupFacadeLocal" %>
<%@ page import="ru.masterdm.spo.utils.SBeanLocator" %>
<%@ page import="ru.md.persistence.UserMapper" %>
<%@ page import="ru.md.domain.MdTask" %>
<%@ page import="ru.masterdm.spo.service.IDashboardService" %>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%Logger LOGGER = Logger.getLogger("frame_aboutApplication_jsp");
try {
	PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
	MdTask mdtask = TaskHelper.getMdTask(request);
	String processName = "";
	String status = "";
	if (mdtask.getIdPupProcess()!=null) {
	    processName = mdtask.getProcessname();
	    status = pupFacadeLocal.getPUPAttributeValue(mdtask.getIdPupProcess(), "Статус");
	}
	if(!TaskHelper.getCcMapStatus(mdtask).isEmpty()) {status = TaskHelper.getCcMapStatus(mdtask);}
	boolean editMode = TaskHelper.isEditMode(null, request);
	List<String> currentUserRoles = ((UserMapper) SBeanLocator.singleton().getBean("userMapper")).userRoles(TaskHelper.getCurrentUser(request).getIdUser(), mdtask.getIdTypeProcess());
	boolean checkParentStage = false;
	String stageName = "";
	if(editMode){
	  try{
		TaskInfo taskInfo = TaskHelper.getCurrTaskInfo(request);
		stageName = taskInfo.getNameStageTo().trim().toLowerCase();
		List<String> checkParentOperations = Arrays.asList("формирование проекта кредитного решения", "доработка проекта кредитного решения", "отправка проекта кредитного решения на рассмотрение уо/ул", "получение решения уо/ул и направление запросов на проведение экспертиз","Отправка проекта Кредитного решения на рассмотрение УО/УЛ или экспертизы","Получение решения УО/УЛ и направление запроса на экспертизу правового статуса");
		checkParentStage = checkParentOperations.contains(stageName);
      } catch (Exception e) {
		    LOGGER.log(Level.SEVERE, e.getMessage(), e);
      }
	}
    
	%>
<%ru.md.spo.dbobjects.TaskJPA task = TaskHelper.taskFacade().getTask(TaskHelper.getIdMdTask(request));%>
	<input type="hidden" id="stage_name" name="stage_name" value="<%=stageName%>">
	<input type="hidden" id="checkParentStage" name="checkParentStage" value="<%=checkParentStage%>">
	<div id="oZayavke" style="min-width: 280px;">
			<%=processName%><input type="hidden" id="process_name" name="process_name" value="<%=processName %>"><br>
		<%if(!mdtask.isSublimit()){%>
    	    <label>Статус</label>
			<%if(!mdtask.isPipelineProcess()){ %>
    			<span><%=status %></span>
    			<%=TaskHelper.getMemorandumMessage(request) %>
			<%} else {
                    if (!editMode) {%>
                        <%=status%>
                    <%} else {%>
                        <pup:select name="Статус" />
                    <%}%>
            <%}%>
        <% IDashboardService dashboardService = (IDashboardService) SBeanLocator.singleton().getBean("dashboardService");%>
		<!--<br><label>Состояние</label> <span><%=dashboardService.getCurrentStatus(TaskHelper.getIdMdTask(request)) %></span>-->
		<%}%>
	<%if(task.isLimit()){
		editMode=processName.startsWith("Крупный бизнес ГО")?TaskHelper.isEditMode("Лимит включает сублимиты",request):editMode; %>
	    <br>Лимит включает Сублимиты <input type="checkbox" <%=task.isWithSublimit()?"checked=\"checked\"":"" %>  id="with_sublimit"
	    <%if(editMode){%>
	    onclick="with_sublimitOnClick();fieldChanged();" name="with_sublimit" value="y"> 
	    <%} else{ %>
	    disabled><input type="hidden" name="with_sublimit" value="<%=task.isWithSublimit()?"y":"n" %>">
	    <%} %>
	<%}%>

	<%if(mdtask.getParentid()!=null && mdtask.isProduct()){
		editMode=processName.startsWith("Крупный бизнес ГО")?TaskHelper.isEditMode("Индивидуальные условия",request):editMode;%>
	    <br/>Индивидуальные условия <input type="checkbox" <%=task.isIndcondition()?"checked":"" %>
	    <%if(editMode){ %> id="indcondition" name="indcondition" onclick="checkParentAllowedFilled();fieldChanged();" value="y">
	    <%}else{ %> DISABLED><input type="hidden" name="indcondition" value="<%=task.isIndcondition()?"y":"n" %>">
	    <%} %>
	    <input type="hidden" id="parent_sum" name="parent_sum" value="<%=SBeanLocator.singleton().mdTaskMapper().getById(mdtask.getParentid()).getMdtaskSum().toPlainString() %>">
	<%}%>
	<input type="hidden" id="is_product" name="is_product" value="<%=task.isProduct()?"y":"n" %>">
		<div id="trader_approve_message">
		<%if(task.isTraderApprove()){ %>
		Подтверждено Трейдером <%=pupFacadeLocal.getUser(task.getTrader_approve_user()).getFullName() %> <%=Formatter.formatDateTime(task.getTrader_approve_date()) %>
		<%} %></div>
	<%if(task.getCed_approve_login()!=null){ %>
	<br /><div>Подтверждено Пользователем <%=pupFacadeLocal.getUserByLogin(task.getCed_approve_login()).getFullName() %> <%=Formatter.format(task.getCed_approve_date()) %></div>
    <%} %>
	<%if((currentUserRoles.contains("Структуратор") || currentUserRoles.contains("Руководитель структуратора"))
		&& !task.isSublimit() && task.getProcess() != null && task.getProcess().isPaused()){ %>
	<br/><label>Состояние активности</label> <%=task.getProcess().isPaused()?"приостановлена":"активна" %>
	<br/><label>Срок восстановления</label> <%=Formatter.format(task.getProcess().getResumeDate()) %>
	<%if(task.getProcess().isHaveBeenPause()){ %>
	<br/><label><a href="javascript:;" onclick="$('#resumeStatistic').jqmShow()">История приостановления</a></label>
	<div id="resumeStatistic" class="jqmWindow" style="overflow:auto" >
	<h1>История приостановления</h1>
	<table class="regular">
	<thead><tr><th>Дата действия</th><th>Пользователь</th><th>Срок восстановления</th><th>Комментарий</th>
    </tr></thead><tbody>
    <%for(ru.md.pup.dbobjects.ProcessEventJPA event : task.getProcess().getEvents()){
        if(event.getId_process_type_event().equals(2L)||event.getId_process_type_event().equals(9L)){%>
        <tr><td><%=Formatter.format(event.getDate_event()) %></td><td><%=event.getUser().getFullName()%></td>
            <td><%=Formatter.format(event.getPauseDate()) %></td>
            <td><%=event.getPauseResumeCmnt() %></td></tr>
    <%}} %>
    </tbody></table><hr><a href="#" class="jqmClose">Закрыть</a>
    </div>
    <script type="text/javascript">
        $(document).ready(function() {
            $('#resumeStatistic').jqm();
        });
    </script>
	<%}} %>
		<div id="sumCheckWarningPopup" title="Не соответствует сумма сделки" style="display: none;" >
			<div>
				<div id="sumCheckWarningText"></div><br/>
				<button onclick="continueAfterSumCheckWarning();return false;">Продолжить</button>
				<button onclick="$('#sumCheckWarningPopup').dialog('close');">Отмена</button>
			</div>
		</div>
		<input type="hidden" id="completeAfterValidate" name="completeAfterValidate"/>
		<input type="hidden" id="afterSumCheckAction" name="afterSumCheckAction"/>

	</div>
<%} catch (Exception e) {
		LOGGER.log(Level.SEVERE, e.getMessage(), e);
}
%>