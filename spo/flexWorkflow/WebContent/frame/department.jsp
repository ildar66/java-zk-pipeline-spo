<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="ru.md.pup.dbobjects.DepartmentJPA" %>
<%@page import="ru.md.spo.dbobjects.TaskJPA" %>
<%@page import="java.util.logging.*"%>
<%@page import="ru.md.spo.ejb.PupFacadeLocal" %>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@page import="ru.md.helper.TaskHelper"%>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>

<%
response.addHeader("Pragma", "no-cache");
response.addHeader("Expires", "-1");
response.addHeader("Cache-control", "no-cache");
	Logger LOGGER = Logger.getLogger("department_jsp");
	TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
	PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
	TaskJPA task=taskFacadeLocal.getTask(Long.valueOf(request.getParameter("mdtaskid")));
	boolean readOnly = !pupFacadeLocal.isPermissionEdit(Long.valueOf(request.getParameter("pupTaskId")),"R_Ответственные подразделения")
			   &&!TaskHelper.isSpecialEditMode("", request);
	boolean editMode= !readOnly;

	%>
	
		<%try{ %>
		<!-- скрытые поля. Буфер для вставки нового элемента. Используется при добавлении нового департамента и нового менеджера. -->
		
<div style="display:none;"><span id="spnewentity"></span><input type="hidden" id="newentity" value="">
		<a id="anewentity" href="" class="fancy"></a>
		<input type="text" name="userId" id="userId"><input type="text" name="userFIO" id="userFIO">
		</div>
				<h3>Основное инициирующее подразделение</h3>
				<table class="regular leftPadd" style="width: 99%;">
					<thead>
						<tr>
							<th style="width: 50%;">Подразделение&nbsp;
								<a href="#" onclick='return openDialog("departmentChangeReport.jsp?idmdtask=<%=task.getId()%>", "departmenChangetPopupDialog", "top=100, left=100, width=870, height=500, scrollbars=yes, resizable=yes");'>
									(История изменений)
								</a>
							</th>
							<th style="width: 50%;">Менеджеры <!--  <img width=400px height=1px src="">--></th>
						</tr>
					</thead>
					<tr>
						<td>
							<%if (editMode == true) {%>
							<a href="#" onclick='return openDialog("popup_initdep.jsp?idmdtask=<%=task.getId()%>", "departmentPopupDialog", "top=100, left=100, width=800, height=550, scrollbars=yes, resizable=yes");'>
							<%}%>
								<span id="currentDepartment">
										<%=(task.getInitDepartment()==null)?"не определено":task.getInitDepartment().getFullName()%>
								</span>
								<span id="newDepartment">
								</span>
							<%if (editMode == true) {%>
							</a>
							<%}%>
						</td>
						<td>
							<table id="idTablesManager" style="width: 99%;">
								<colgroup />
								<colgroup width="10px"/>
								<tbody>
								<%try{
									for (ru.md.spo.dbobjects.ManagerJPA manager : task.getManagers()) {
										if (manager.getStartDepartment()!=null){continue;}
										%>
										<tr>
											<td>
												<%=manager.getUser().getFullName() %>
											</td>
										</tr>
									<%} %>
								<%} catch (Exception e) {	out.println("Ошибка в секции frame_department.jsp:" + e.getMessage());	e.printStackTrace();} %>
								</tbody>
							</table>
						</td>
					</tr>
				</table>
			<%//для сделок %>
				<h3>Место проведения сделки&nbsp;
					<a href="#" onclick='return openDialog("placeChangeReport.jsp?idmdtask=<%=task.getId()%>", "placeChangePopupDialog", "top=100, left=100, width=850, height=500, scrollbars=yes, resizable=yes");'>
						(История изменений)
					</a>
				</h3>
				<%if (editMode == true && false) {%>
				<a href="#" onclick='return openDialog("popup_place.jsp?idmdtask=<%=task.getId()%>", "placePopupDialog", "top=100, left=100, width=1200, height=690, scrollbars=yes, resizable=yes");'>
				<%}%>
					<span id="currentPlace">
						<%=(task.getPlace()==null || task.getPlace().getFullName()==null || task.getPlace().getFullName().isEmpty())?"не определено":task.getPlace().getFullName()%>
					</span>
					<span id="newPlace">
					</span>
				<%if (editMode == true && false) {%>
				</a>
				<%}%>
			<%%>
			<br />
			<input type="hidden" name="selectedPlaceName" id="selectedPlaceName"	value="">
			<input type="hidden" name="selectedPlaceId" id="selectedPlaceId"	value="">
			<input type="hidden" name="selectedCRMId" id="selectedCRMId"	value="">
			<input type="hidden" name="currentUserId" id="currentUserId"	value="<%=TaskHelper.getCurrentUser(request).getIdUser() %>">
			<input type="hidden" name="selectedDeartmentName" id="selectedDeartmentName"	value="">
			<input type="hidden" name="selectedDeartmentId" id="selectedDeartmentId"	value="">
			
		<%} catch (Exception e) {	out.println("Ошибка в секции frame_department.jsp:" + e.getMessage());	e.printStackTrace();} %>
<script language="javascript">
	function onSelectPlace() {
		$('#newPlace').text($('#selectedPlaceName').val());
		$('#currentPlace').hide();
		$('#newPlace').show();
     	return;
	}
	function onSelectDepartment() {
		$('#newDepartment').text($('#selectedDeartmentName').val());
		$('#currentDepartment').hide();
		$('#newDepartment').show();
     	return;
	}
</script>
		