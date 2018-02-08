<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">

<%@ page contentType="text/html; charset=utf-8"%>
<%@ taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@ taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>

<%@ page import="org.uit.director.action.AbstractAction,
	org.uit.director.contexts.WorkflowSessionContext,
	org.uit.director.db.dbobjects.Attribute,
	org.uit.director.db.dbobjects.BasicAttribute,
	org.uit.director.tasks.AssignTaskInfo,
	org.uit.director.tasks.AssignTasksList,
	org.uit.director.db.dbobjects.AttributeStruct,
	org.uit.director.tasks.AttributesStructList,
	org.uit.director.tasks.ProcessInfo,
	ru.md.spo.util.Config, 
	java.util.List,
	java.util.Date,
	org.uit.director.contexts.WPC,
	org.uit.director.db.dbobjects.WorkflowUser"
%>


<%@page import="com.vtb.model.TaskActionProcessor"%>
<%@page import="com.vtb.domain.Task"%>
<%@page import="com.vtb.model.ActionProcessorFactory"%>
<%@page import="java.util.ArrayList"%>
<%@page import="com.vtb.domain.TaskContractor"%>
<html>
<head>
	<%
	WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
	if (wsc.isNewContext()) {
		%>
		<script> document.location = "start.do"; </script>
		<%
		return;
	}
	AssignTasksList list = wsc.getAssignList();
	List assignList = list.getTableAssignTaskList();
	String currPage = "direction";
	String nameCert = Config.getProperty("NAME_SIGN_CENTER");	
	nameCert = new String( nameCert.getBytes("ISO-8859-1"));

	TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");				
	Task task = null;
	%>

	<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
	<title>Переназначение исполнителя</title>
	<link rel="stylesheet" href="style/style.css" />

	<script>
		var CAName;
		CAName = "<%=nameCert%>" ;
		var validateSign;
		validateSign=<%=Config.getProperty("VALIDATE_SIGNUM").equals("true") ? true : false%>;
	</script>

	<script language="JavaScript" src="scripts/sign/MDLib.js"></script>
	<script language="JavaScript" src="scripts/sign/MDSign.js"></script>
	<script language="JavaScript" src="scripts/sign/MDXML.js"></script> 
	<script language="JavaScript" src="scripts/applicationScripts.js"></script> 

	<script type="text/javascript">
		function actionDo(idAssign, idUser, mayReassign) {
		   if (idUser!=''){
				reassign.action = 'reassign.user.do';
				reassign.idUser.value=idUser;
				reassign.idAssign.value=idAssign;
				if (mayReassign)
					reassign.mayReassign.value=1;
				else 
					reassign.mayReassign.value=0;
				reassign.submit();
		   }
		}
	</script>
</head>

<body class="soria">
<jsp:include page="header_and_menu.jsp" />
				<h1>Список заданий для переопределения исполнителя</h1>
				<table class="regular">
					<thead>
						<tr>
							<th>№ п/п</th>
							<th>Заявка №</th>
							<th>Процесс</th>
							<th>Роль</th>
							<th>Дата</th>
							<th>Исполнитель</th>
							<th>Назначить на исполнение</th>
							<th>Возможность переназначать</th>
							<th>Переназначить исполнителя</th>
							<th>Отозвать назначение</th>
							<th>Атрибуты</th>
							<th>Хронология</th>
							<th>Контрагент</th>
							<th>Сумма лимита</th>
							<th>Валюта</th>
						</tr>
					</thead>
					<tbody>
					<%
					//	int count = taskList.size();
					for (int i = 0; i < assignList.size(); i++) {
						AssignTaskInfo assignTaskInfo = (AssignTaskInfo)assignList.get(i);
						ProcessInfo processInfo = assignTaskInfo.getProcessInfo();
						long idAssign = assignTaskInfo.getIdAssign().longValue();
						long idProcess = assignTaskInfo.getIdProcess().longValue();
						Long idUser = assignTaskInfo.getIdUser();
						boolean mayReassign = assignTaskInfo.isMayReassign();
						String userFIO = WPC.getInstance().getUsersMgr().getFIOUser(idUser);
						AttributesStructList attrs = processInfo.getAttributes();

						task = processor.findByPupID(Long.valueOf(idProcess),true);
					%>
					<tr id="signdata">
						<td>
							<%=i + 1%>
						</td>
						<td>
						<%
							BasicAttribute orderId = attrs.findAttributeByName("Заявка №");
							String strOrderId = orderId == null ? "0" : orderId.getAttribute().getValueAttributeString();
							out.print(strOrderId);
						%>
						</td>
						<td>
							<%=processInfo.getNameTypeProcess()%>
						</td>
						<td>
							<%=WPC.getInstance().getRoleName(assignTaskInfo.getIdRole()) %>
						</td>
						<td>
							<%=assignTaskInfo.getDateAssignUser() %>
						</td>
						<td>
							<%=userFIO%>
						</td>
						<td>
							<%
							List usersToAssign = WPC.getInstance().getUsersForRole(assignTaskInfo.getIdRole(), wsc.getCurrentUserInfo().getDepartament().getId()); 
							//usersToAssign.remove(WPC.getInstance().getUsersMgr().getActiveInfoUserByLogin(Config.getProperty("ADMINISTRATOR")));
							usersToAssign.remove(WPC.getInstance().getUsersMgr().getInfoUserByIdUser(idUser));
							usersToAssign.remove(WPC.getInstance().getUsersMgr().getInfoUserByIdUser(assignTaskInfo.getIdUserFrom()));
							%>
							<select name="idUser_<%=idAssign%>"> 
								<option>-</option>
								<%for (int j = 0; j< usersToAssign.size(); j++){ 
									WorkflowUser wfu = ((WorkflowUser)usersToAssign.get(j));
								%>
								<option value="<%=wfu.getIdUser() %>">
									<%=wfu.getFIO() %>
								</option>
								<%} %>
								</select>
						</td>
						<td>
							<input type="checkbox" name="mayReAssign_<%=idAssign%>" value="" <%if (mayReassign) {%>checked<%}%>>
						</td>
						<td>
							<a href="javascript: actionDo(<%=idAssign%>,idUser_<%=idAssign%>.value, mayReAssign_<%=idAssign%>.checked)"><img src="theme/img/change.png" alt="Переназначить исполнителя"></a>
						</td>
						<td>
							<a href="deleteAssign.do?idAssign=<%=idAssign%>"><img src="theme/img/delete.gif" alt="Отозвать назначение"></a>
						</td>
						<td>
							<a href="report.do?classReport=org.uit.director.report.mainreports.AttributesReport&par1=<%=idProcess%>&par2=<%=wsc.getIdUser()%>"><img src="theme/img/view.png" alt="Атрибуты процесса"></a>
						</td>
						<td>
							<a href="report.do?classReport=org.uit.director.report.mainreports.HistoryReport&par1=<%=idProcess%>"><img src="style/time.png" alt="Хронология выполнения процесса"></a>
						</td>
						
						<td>
							<%
								ArrayList contractors = task.getContractors();
								for (int j = 0; j < contractors.size(); j++) {
									out.print(((TaskContractor)(contractors.get(j))).getOrg().getAccount_name() + "<br/>");
								}
							%>
						</td>
						
						<td class="number">
							<% 	
								out.print(task.getMain().getFormattedSum());
							%>
						</td>
						
						<td>
							<% 	
								out.print(task.getMain().getCurrency2().getCode());
							%>
						</td>
					</tr>
					<%
					}
					%>
				</table>
<jsp:include flush="true" page="footer.jsp" />
	<FORM name="reassign" method="post" action="reassign.user.do">
		<INPUT type="hidden" name="idUser" value="">
		<INPUT type="hidden" name="idAssign" value="">
		<INPUT type="hidden" name="mayReassign" value="">
	</FORM>
	
</body>
</html>



