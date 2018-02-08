<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="com.vtb.domain.Task" %>
<%@page import="java.util.logging.*"%>
<%@page import="ru.masterdm.compendium.domain.crm.StatusReturn" %>
<%@page import="ru.masterdm.compendium.model.CompendiumCrmActionProcessor"%>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@page import="ru.md.spo.ejb.PupFacadeLocal" %>
<%@page import="ru.md.helper.TaskHelper" %>
<%@page import="com.vtb.util.Formatter" %>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%
	Logger LOGGER = Logger.getLogger("frame_statusreturn_jsp");
	Task task=TaskHelper.findTask(request);
	boolean editMode = TaskHelper.isEditMode("Статус решения",request);
	boolean showSection = task.getTaskStatusReturn()!=null && task.getTaskStatusReturn().getStatusReturn() != null 
	    && task.getTaskStatusReturn().getStatusReturn().getId()!=null && !task.getTaskStatusReturn().getStatusReturn().getId().isEmpty();
	
	if(showSection){
	CompendiumCrmActionProcessor compenduim = (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
	StatusReturn[] statusReturnList = compenduim.findStatusReturn(null);
	%>
	<table class="pane return_status" id="section_Статус Решения по заявке">
		<thead onclick="doSection('Статус Решения по заявке')" onselectstart="return false">
			<tr>
				<td>
					<div>
						<img alt="Развернуть" src="style/toOpen.gif"  align="middle" id="imgSection">
						<span>Статус Решения по заявке</span>
					</div>
				</td>
			</tr>
		</thead>
		<tbody>
		<%try{ %>
			<tr><td>
			<table>
			<tr><td><label>дата принятия решения<br />
			<md:calendarium name="refuse_date" id="refuse_date" readonly="<%=!editMode %>" 
			value="<%=Formatter.format(task.getTaskStatusReturn().getDateReturn()) %>"/>
			</label>
			</td></tr>
			<%
			Long userid = task.getTaskStatusReturn().getIdUser();
			String authorizedPerson = "";
			TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
			ru.md.spo.dbobjects.TaskJPA taskJPA = taskFacadeLocal.getTask(task.getId_task());
			if (taskJPA.getAuthorizedPerson()!=null){
				authorizedPerson = taskJPA.getAuthorizedPerson().getDisplayName();
			}
			if(authorizedPerson=="" && userid!=null && userid>0l){
			    PupFacadeLocal pupFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
			    authorizedPerson = pupFacadeLocal.getUser(userid).getFullName() + " (" + 
			        pupFacadeLocal.getUser(userid).getDepartment().getShortName() + ")";
			}
			if(authorizedPerson!=""){
			%>
			<tr><td><label>
            Уполномоченное лицо: <%=authorizedPerson %>
            </label></td></tr>
            <%} %>
			<tr><td><label>Статус решения (детализация)<br />
			<%if(editMode){ %>
			<select name="StatusReturn" id="StatusReturn">
                        <%for(StatusReturn statusReturn:statusReturnList){ %>
                            <option value="<%=statusReturn.getId() %>"><%=statusReturn.getDescription() %></option>
                        <%} %>
            </select>
            <%}else{ %>
			<%=task.getTaskStatusReturn().getStatusReturn().getDescription() %><%} %></label>
			</td></tr><tr><td>
			<label>Комментарий к решению<br />
			<%if(editMode){ %>
			<textarea rows="15" name="StatusReturnText" id="StatusReturnText" class="advanced_textarea" style="width: 100%;"><%} %>
			<%=task.getTaskStatusReturn().getStatusReturnText() %>
			<%if(editMode){ %></textarea><%} %>
			</label>
			</td></tr></table>
		</td>
			</tr>
				<%
		} catch (Exception e) {
			out.println("Ошибка в секции  frame_statusreturn_jsp:" + e.getMessage());
			e.printStackTrace();
		}
%>
		</tbody>
	</table>
<%}%>
