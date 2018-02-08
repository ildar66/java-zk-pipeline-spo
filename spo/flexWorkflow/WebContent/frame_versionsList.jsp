<%@page import="ru.md.spo.dbobjects.TaskJPA"%>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal"%>
<%@page import="ru.md.pup.dbobjects.UserJPA"%>
<%@page import="com.vtb.util.Formatter"%>
<%@page import="ru.md.spo.ejb.PupFacadeLocal"%>
<%@page import="java.util.logging.Level"%>
<%@page import="java.util.logging.Logger"%>
<%@page import="com.vtb.util.ApplProperties"%>
<%@page import="com.vtb.domain.Main" %>
<%@page import="java.util.List"%>
<%@page import="com.vtb.model.ActionProcessorFactory"%>
<%@page import="com.vtb.model.TaskActionProcessor"%>
<%@page import="ru.md.helper.TaskHelper"%>
<%@page import="com.vtb.domain.Task"%>
<%@page import="com.vtb.domain.Comment"%>
<%@page isELIgnored="true"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<!--Выводит информацио о версиях сделки-->
<%
	Logger logger = Logger.getLogger(this.getClass().getName());
	Task taskMain = TaskHelper.findTask(request);
	TaskActionProcessor taskProc = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
	PupFacadeLocal pup = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
	TaskFacadeLocal taskFacade = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
	List<TaskJPA> versions = taskFacade.getVersions(taskMain.getHeader().getNumber().toString());
	if (versions != null && versions.size() > 0) {

 	//текущая и следующая версии
	Long currentVersionId = null;
	Long nextVersionId = null;
	boolean isLastVersion = false;
	if (versions != null) {
		int i = 0;
		for (TaskJPA version : versions) {
			// пропускаем версии КОД (без процесса)
			if (version.getIdProcess() == null && taskMain.getId_pup_process() > 0
					|| version.getIdProcess() != null && taskMain.getId_pup_process() == 0)
				continue;
			if (currentVersionId != null) {//уже найдена текущая версия, а это следующая
				nextVersionId = version.getId();
				break;
			}
			if (version.getId().equals(taskMain.getId_task())) {
				currentVersionId = version.getId();
				if (i == 0)
					isLastVersion = true; //первая же версия в списке == текущей => версия актуальная
			}
			i++;
		}
	}
%>
<script language="javascript">
	var currentVersion = <%=(currentVersionId==null)?null:currentVersionId.toString()%>;
	var nextVersion = <%=(nextVersionId==null)?null:nextVersionId.toString()%>;
	var idVersions = [];
	if (nextVersion != null && currentVersion != null) {
		idVersions.push(currentVersion);
		idVersions.push(nextVersion);
		document.getElementById('compareApproved').style.display = 'block';
	}
</script>
<%
	}
%>
		<%
			try {
		%>
				<div id='compareApproved' name='compareApproved' style='display:none'>
					<button type="button" onclick="goToCompare2Versions();" style='padding-left:0px'>Сравнить с предыдущей</button>
					<script type="text/javascript">
						function goToCompare2Versions() {
							if (idVersions.length == 0) {
								alert('Нет предыдущей версии');
								return false;
							}
							var objType = ($('#tasktype').val()!= 'p') ? 'limit' : 'product';
							location.href = 'formCompare.jsp?objectType=' + objType + '&ids=' + idVersions.join('|');
					}
					</script>
				</div>
				<table class="regular" id="idTableVersion" name="idTableVersion">
					<thead>
						<tr>
							<th>Версия заявки</th>
							<th>Контрагент</th>
							<th>Сумма</th>
							<th>ФИО Инициатора</th>
							<th>Обоснование изменения параметров</th>
							<th>Статус</th>
							<th></th>
						</tr>
					</thead>
					<tbody>
						<!--вывод таблицы для версий-->
						<%
							for (TaskJPA version : versions) {
								// пропускаем версии КОД (без процесса)
								if (!version.isHasProcess() && taskMain.getId_pup_process() > 0
										|| version.isHasProcess() && taskMain.getId_pup_process() == 0)
									continue;
								String initiator = "";
								UserJPA initUser = version.getIdProcess()==null?null:pup.getProcessInitiator(version.getIdProcess());
								if (initUser != null)
									initiator = initUser.getFullName();
								Task versionJDBC = taskProc.getTask(new Task(version.getId()));
								Main main = versionJDBC.getMain();
								String sumAndCurrency = null;
								if (main.isLimitIssue()) {//выдача
									sumAndCurrency = Formatter.format(main.getLimitIssueSum());
									if (sumAndCurrency != null && !sumAndCurrency.isEmpty() && main.getCurrency2() != null)
										sumAndCurrency += (" " + main.getCurrency2().getCode());
								}
								else
									sumAndCurrency = version.getSumWithCurrency();
								String reason = "Обоснование изменения параметров: ";
								String commentBody = "";
								List<Comment> comments = versionJDBC.getComment();
								if (comments != null) {
									for (Comment comment:comments) {
										commentBody = comment.getBody();
										if (commentBody != null && commentBody.startsWith(reason)) {
											commentBody = commentBody.substring(reason.length());
											break;
										}
										else
											commentBody = "";
									}
								}
								String status = version.getProcess()==null ? "" : pup.getPUPAttributeValue(version.getProcess().getId(), "Статус");
						%>
						<tr <%=(version.getId().equals(taskMain.getId_task()) ? " style='color:blue' " : "")%> >
							<td><a
								href='/<%=ApplProperties.getwebcontextFWF()%>/form.jsp?mdtask=<%=version.getId()%>'
							> <%=version.getVersion().toString()%>
							</a></td>
							<td>
								<%=version.getOrganisation().replaceAll(",", "<br />") %>
							</td>
							<td><%=sumAndCurrency%></td>
							<td><%=Formatter.str(initiator)%></td>
							<td>
								<%=commentBody%>
							</td>
							<td><%=status %></td>
							<td class="delchk">
								<%
									if (!version.getId().equals(taskMain.getId_task())) {
								%>
									<input type="checkbox" class="chbCompare" value="<%=version.getId() %>" />
								<%
									}
								%>
							</td>
						</tr>
						<%
							}
						%>
					</tbody>
				</table>
				<p align="right"><button type="button" onclick="goToCompare();">История Изменений</button></p>
				<script type="text/javascript">
					var ids = [<%=taskMain.getId_task().toString() %>];
					function goToCompare() {
						if (ids.length === 0) {
							alert('Необходимо выбрать элементы для сравнения');
							return false;
						}
						
						var objType = ($('#tasktype').val() != 'p') ? 'limit' : 'product';
						location.href = 'formCompare.jsp?objectType=' + objType + '&ids=' + ids.join('|');
					}
					$(document).ready(function(){
						$('.chbCompare').on('change', function() {
							if (this.checked) {
								ids.push($(this).val());
							}
							else {
								var pos = $.inArray($(this).val(), ids);
								if (pos > -1)
									ids.splice(pos, 1);
							}
						});
						$('.chbCompare').each(function() {
							$(this).prop('checked', false);
						})
					})
				</script>
		<%
			} catch (Exception e) {
				out.println("Ошибка в секции frame_versionsList.jsp:" + e.getMessage());
				logger.log(Level.SEVERE, e.getMessage(), e);
				e.printStackTrace();
			}
		%>
