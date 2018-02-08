<%@page import="org.uit.director.plugins.commonPlugins.actions.ViewProcessWrapper"%>
<%@page import="org.uit.director.tasks.TaskInfo"%>
<%@page import="ru.md.helper.TaskHelper" %>
<%@page import="ru.md.spo.ejb.PupFacadeLocal" %>
<%@ page import="ru.md.domain.MdTask" %>
<%@ page import="ru.md.spo.dbobjects.TaskJPA" %>
<%@ page import="ru.masterdm.spo.utils.SBeanLocator" %>
<%@ page import="ru.md.persistence.UserMapper" %>
<%@ page import="ru.md.domain.User" %>
<%@ page import="ru.md.pup.dbobjects.UserJPA" %>
<%@ page import="java.util.logging.Logger" %>
<%@ page import="java.util.logging.Level" %>
<%@ page import="ru.masterdm.spo.utils.Formatter" %>
<%@ page import="org.uit.director.contexts.WorkflowSessionContext" %>
<%@ page import="org.uit.director.action.AbstractAction" %>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>

<div id="informaciyaPoOperacii" style="min-width: 280px;">
<%
	MdTask mdtask = TaskHelper.getMdTask(request);
	Logger LOGGER = Logger.getLogger("frame_operationInfo_jsp");
	boolean editMode = TaskHelper.isEditMode(null, request);
try {
	TaskInfo taskInfo = TaskHelper.getCurrTaskInfo(request);
	if (taskInfo != null) {
	%>
		<label>Операция</label>
			<span id="nazvanieOperacii"><%=(taskInfo.getNameStageTo() != null)?taskInfo.getNameStageTo():""%></span> 
			<a target="_blank"
                        href="plugin.action.do?class=<%=ViewProcessWrapper.class.getName()%>&idProcess=<%=taskInfo.getIdProcess()%>"
                        title="Посмотреть путь заявки по всем операциям"><img src="style/shema.png" alt="схема"
            ></a>
			<br />
				<% 
				if (taskInfo.getNameStageTo() != null) {
				%>
					<label>Состояние</label>
					<% 
						String stateOperation = "";
						if (!taskInfo.getDateOfRealCompleteStr().equalsIgnoreCase("-")) {
							stateOperation = "завершена";
						}
						else if (taskInfo.getDateOfTakingStr().equalsIgnoreCase("-")) 
						{
							stateOperation = "новая";
						}
						else if (taskInfo.getDateOfRealCompleteStr().equalsIgnoreCase("-")) 
						{
							stateOperation = "в работе";
						}
						out.print(stateOperation);
					%>
				<%
				}
				%><br>
				<% 
				if (taskInfo.getNameStageTo() != null) {
				%>
					<span class="date"><label>Принята к обработке</label> <%=(taskInfo.getDateOfCommingStr().equalsIgnoreCase("-"))?"—":taskInfo.getDateOfCommingStr().substring(0, taskInfo.getDateOfCommingStr().length())%></span><br />
				<%
				}
				%>
	<%
	}
} catch (Exception e) {
	out.println("Ошибка в секции operationInfo.jsp:" + e.getMessage());
	e.printStackTrace();
}
%>
<%PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
Long idMdtask = TaskHelper.getIdMdTask(request);
	MdTask task = TaskHelper.getMdTask(request);%>
	<label>Приоритет
		<%if(!TaskHelper.isEditMode(null,request)){ %>
		<%=TaskHelper.findTask(request).getHeader().getPriority() %>
		<%}else{ %><pup:select name="Приоритет" style="width:7em;" onChange="fieldChanged();" /><%} %>
	</label><br>
	<label>Срок</label> <span id="header_validto_span"><%=task.getValidtoDisplay()%></span><br />
<%if(TaskHelper.taskFacade().getGlobalSetting("traderApproveEnable").equalsIgnoreCase("true")&&
		task.isProduct() && pupFacadeLocal.isCurrentUserInProjectTeam(idMdtask)
    && pupFacadeLocal.currentUserAssignedAs("Кредитный аналитик", task.getIdPupProcess())
    && !pupFacadeLocal.isCedEnded(idMdtask)){ %>
<button onClick="traderConfirm();return false;">Подтверждено Трейдером</button><br />
<%} %>

<%
	if(request.getParameter("monitoringmode")!=null){
		TaskHelper.isEditMode(null, request);//side effect
	TaskJPA taskJPA=TaskHelper.taskFacade().getTask(TaskHelper.getIdMdTask(request));%>
	<label>Режим:</label> <%=taskJPA.getMonitoringMode()%><br />
<%if(taskJPA.getMonitoringUserWorkId()!=null){
	User user = ((UserMapper)SBeanLocator.singleton().getBean("userMapper")).getUserById(taskJPA.getMonitoringUserWorkId());
%>
		<label>Операция в работе у пользователя</label> <%=user.getFullName()%> (<%=user.getDepname()%>)
<%}}%>
	<%if(task.isProduct()){
		try{
			editMode=mdtask.getProcessname().startsWith("Крупный бизнес ГО")?TaskHelper.isEditMode("В рамках лимита",request):editMode;
			boolean disabled = !editMode;
			if(!disabled){
				WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
				ru.md.pup.dbobjects.TaskInfoJPA taskInfo = pupFacadeLocal.getTask(wsc.getIdCurrTask());
				disabled = !taskInfo.getStage().isStructuratorStage() && !taskInfo.getStage().isMidleOfficeStage();
			}
	%>
	<br/><label>Продукт</label> <span><%=Formatter.str(mdtask.getProductName())%></span>
	<br/>В рамках Лимита&nbsp;<input type="checkbox" <%=disabled?"disabled=\"disabled\"":""%>
	<%=mdtask.getParentid()!=null?"checked=\"checked\"":"" %> id="productInLimit"
									 onclick="onProductInLimitClick();fieldChanged();">
	<span id="inLimitMessage"></span>
	<input name="inlimitID" id="inlimitID" type="hidden"><input name="inLimitName" id="inLimitName" type="hidden">
	<input type="hidden" name="clearInLimit" id="clearInLimit" value="n">
	<div id="productInLimitConfirm" class="jqmWindow">
		<h1 class="withlimit">Отвязать сделку от Лимита или выбрать другой Лимит?</h1>
		<h1 class="withoutlimit">Выбор Лимита/Сублимита, в рамках которого проводится Сделка</h1><br><br><br>
		<div align="center">
			<a href="javascript:;" onclick="selectInLimit()" class="jqmClose button withlimit">Другой Лимит/Сублимит</a>
			<a href="javascript:;" onclick="selectInLimit()" class="jqmClose button withoutlimit">Да</a> &nbsp;&nbsp;&nbsp;
			<a href="javascript:;" onclick="notInLimit()" class="jqmClose button" id="notInLimit">Вне Лимита</a> &nbsp;&nbsp;&nbsp;
			<a href="javascript:;" class="jqmClose button">Отмена</a></div>
	</div>
	<script type="text/javascript">
		$(document).ready(function() {
			$('#productInLimitConfirm').jqm();
		});
	</script>
	<%} catch (Exception e) {
		LOGGER.log(Level.SEVERE, e.getMessage(), e);
	}%>
	<%}%>
	<%if(!mdtask.isSublimit()){
		UserJPA currentUser = pupFacadeLocal.getCurrentUser();
		boolean favorite = SBeanLocator.singleton().mdTaskMapper().isFavorite(mdtask.getIdMdtask(), currentUser.getIdUser());%>
	<div>
		Избранная
		<%if (favorite) {%>
		<img mdTaskId="<%=mdtask.getIdMdtask()%>" userId="<%=currentUser.getIdUser()%>" favorite="1" src="style/fav.png" style="cursor: pointer" class="favorite" />
		<%} else { %>
		<img mdTaskId="<%=mdtask.getIdMdtask()%>" userId="<%=currentUser.getIdUser()%>" favorite="0" src="style/unfav.png" style="cursor: pointer" class="favorite" />
		<%} %>
	</div>
	<%}%>
	<script language="JavaScript">
		$(favoriteSwitcher("img.favorite", imgSwitcher));
	</script>
</div>
