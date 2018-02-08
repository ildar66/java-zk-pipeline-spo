<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@page import="java.util.Collection"%>
<%@page import="java.util.Iterator"%>
<%@page import="org.uit.director.contexts.WorkflowSessionContext"%>
<%@page import="org.uit.director.action.AbstractAction"%>
<%@page import="org.uit.director.tasks.TaskList"%>
<%@page import="ru.md.spo.util.Config"%>
<%@page import="org.uit.director.tasks.TaskInfo"%>
<%@page import="org.uit.director.contexts.WPC"%>
<%@page import="java.util.Date"%>
<%@page import="java.util.List"%>
<%@page import="org.uit.director.db.dbobjects.Attribute"%>
<html:html>
<head>
	<%@ page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
	<title>Информация по операциям заявки</title>
	<link rel="stylesheet" href="style/style.css" />
</head>
<body class="soria">
	<%
		WorkflowSessionContext wsc = AbstractAction.getWorkflowSessionContext(request);
		if (wsc.isNewContext()) {
			return;
		}
		String ID_PROCESS = request.getParameter("ID_PROCESS");
		long id_process = 0;
		if (ID_PROCESS != null) {
			id_process = Long.parseLong(ID_PROCESS.trim());
		}
		boolean bShowDataApplication=true;
			TaskList taskList = wsc.getTaskList();
		String typeList = request.getParameter("typeList");
		if (typeList == null) typeList = "noAccept";
		boolean isLoadAllPages = taskList.isLoadAllTaskList();
		String nameCert = Config.getProperty("NAME_SIGN_CENTER");	
		nameCert = new String( nameCert.getBytes("ISO-8859-1"));			
	%>	
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
	<h2><a id="idLinkShowData_<%=ID_PROCESS%>" style="display:none" onclick="window.open('edit.process.do?idProcess=<%=ID_PROCESS%>')" href="#">Просмотреть заявку</a>
	
	<h3>История операций</h3>
	<table class="regular">
		<thead>
			<tr>
				<td>
					№
				</td>
				<td>
					операция
				</td>
				<td>
					состояние
				</td>
				<td>
					начало
				</td>
				<td>
					плановое завершение
				</td>
				<td>
					фактическое завершение
				</td>
			</tr>
		</thead>
		<tbody>
			<form name="processGrid" method="post">
				<%
				long oldProccessID=0;
				long newProccessID=0;
				long countOfApplication = 0;
				long countOfTask = 0;
				int arr[] = {0,11,2,7,8,10,11,2,7,8,10};
				String navigation = taskList.getNavigation();
				%>
			<!--	<%=navigation%> -->
				<%
					//    int count = taskList.size();
					for (int i = taskList.leftBoundPage(); i < taskList.rightBoundPage(); i++) {
						TaskInfo task = taskList.getTaskIdx(i);
						long idTask = task.getIdTask().longValue();
						int stroc = 0;
						newProccessID = task.getIdProcess().longValue();
						if (newProccessID != id_process)
							continue;
						countOfTask++;
				%>
			<!--	<tr bgcolor="<%=task.getColorTask()%>"  id="signdata">		-->
					<tr id="signdata">
						<%
							List atrView = task.getAttributes().getAttributesOrder();
							//task.getOrderView());
							int countAtr = atrView.size();
							if (typeList.equalsIgnoreCase("noAccept"))
								countAtr = 6;
							int j = 0;
							Object valueAttr = null;	
							boolean isWorking = false;
							//if (task.getDateOfTakingStr().equalsIgnoreCase("-")) isWorking = true;
							//else if (task.getDateOfRealCompleteStr().equalsIgnoreCase("-")) isWorking = true;
							if (!task.getDateOfRealCompleteStr().equalsIgnoreCase("-")) {
								isWorking = false;
							}
							else if (task.getDateOfTakingStr().equalsIgnoreCase("-")) 
							{
								isWorking = true;
							}
							else if (task.getDateOfRealCompleteStr().equalsIgnoreCase("-")) 
							{
								isWorking = true;
							}
							for (int l = 0; l < countAtr; l++) {
								if (l == 0)
									valueAttr = String.valueOf(countOfTask);	
								if (l == 1)
									valueAttr = WPC.getInstance().findStage(new Long((int)task.getIdStageTo())).getNameStage();					
								if (l==2) {
									if (!task.getDateOfRealCompleteStr().equalsIgnoreCase("-")) {
										valueAttr = "Завершено";
									}
									else if (task.getDateOfTakingStr().equalsIgnoreCase("-")) 
									{
										valueAttr = "Новая";
										bShowDataApplication=false;
										isWorking = true;
									}
									else if (task.getDateOfRealCompleteStr().equalsIgnoreCase("-")) {
										valueAttr = "В работе";
										bShowDataApplication=false;
										isWorking = true;
									}
								}
								if (l == 3)						
									valueAttr = task.getDateOfCommingStr().substring(0, task.getDateOfCommingStr().length() - 5);
								if (l == 4)						
									valueAttr = task.getDateOfMustCompleteStr().substring(0, task.getDateOfMustCompleteStr().length() - 5);
								if (l == 5)
									valueAttr = task.getDateOfRealCompleteStr().equalsIgnoreCase("-")?"---":task.getDateOfRealCompleteStr().substring(0, task.getDateOfRealCompleteStr().length() - 5);
						%>
						<td valign="middle" <%if (l!=1) {%> align="center" <%} else {%> align="left" <%}%> >
							<%
								if (isWorking) {
							%>
								<a target="_parent" href="task.context.do?id=<%=idTask%>" > <b>	
							<%
								}
							%>
							
								<%=valueAttr %>
							<%
								if (isWorking) {
							%>
									</b></a>	
							<%
								}
							%>
						</td>
						<%
							}
						%>
					</tr>
						<input type="hidden" name="<%="user" + i%>" value="<%=wsc.getIdUser()%>"  id="user"> 
						<input type="hidden" name="<%="data" + i%>" value="<%= WPC.getInstance().dateFormat.format(new Date())%>" id="data"> 	
						<input type="hidden" name="<%="id" + i%>" value="<%=idTask%>" id="id"> 	
						<input type="hidden" name="<%="idProc" + i%>" value="<%=task.getIdProcess().longValue()%>" id="idProc"> 				
						<input type="hidden" name="<%="sign" + i%>" value="" id="sign">
				<%	
						oldProccessID = newProccessID;
				}
				%>
				<input type="hidden" name="typeList"/>	
			</form>
		</tbody>
	</table>
	<%
	if (bShowDataApplication == true) {
	%>
		<script type="text/javascript">
			idLinkShowData_<%=ID_PROCESS%>.style.display="";
		</script>
	<%
	}%>
</body>
</html:html>