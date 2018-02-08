<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="ru.masterdm.compendium.domain.cc.CcResolutionStatus"%>
<%@page import="ru.md.spo.ejb.PupFacadeLocal" %>
<%@page import="com.vtb.domain.Task" %>
<%@page import="java.util.logging.*"%>
<%@page import="java.util.ArrayList"%>
<%@page import="ru.md.helper.TaskHelper" %>
<%@page import="com.vtb.util.Formatter"%>
<%@page import="ru.masterdm.compendium.domain.spo.SpoStatusReturn" %>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%
	Logger LOGGER = Logger.getLogger("frame_statusreturnCC_jsp");
	PupFacadeLocal pupFacade = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
	Task task=TaskHelper.findTask(request);
	String decision = pupFacade.getPUPAttributeValue(task.getId_pup_process(),"Decision");
	if(task.getCcStatus().getStatus().getId()!=null
	   &&task.getCcStatus().getStatus().getId().longValue()!=0
	   &&task.getCcStatus().getStatus().getCategoryId()!=null
	   &&task.getTaskStatusReturn().getDateReturn()==null){
	//есть решение кредитного коммитета
	boolean editMode = TaskHelper.isEditMode("Статус решения",request);
	ru.masterdm.compendium.model.CompendiumSpoActionProcessor compenduim = (ru.masterdm.compendium.model.CompendiumSpoActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumSpo");
	ArrayList<CcResolutionStatus> list = compenduim.findCcResolutionStatusList(new CcResolutionStatus(task.getCcStatus().getStatus().getId(),null,null,null),null);
	CcResolutionStatus status = null;
	for (CcResolutionStatus s : list) status = s;
	%>
	
<table class="pane return_status" id="section_Статус Решения по заявкеcc">
		<thead onclick="doSection('Статус Решения по заявкеcc')" onselectstart="return false">
			<tr>
				<td>
					<div>
						<img alt="Развернуть" src="style/toOpen.gif"  align="middle" id="imgSection">
						<span>Статус Решения по заявке</span>
					</div>
				</td>
			</tr>
		</thead>
		<tbody style="display:none">
		<%try{ %>
			<tr><td>
			<table>
			<tr><td><label>дата принятия решения<br />
			<%=Formatter.format(task.getCcStatus().getMeetingDate()) %></label>
			</td></tr>
            <tr><td><label>Решение уполномоченного органа<br />
            <%=task.getCcStatus().getStatus().getName() %> (<%=status.getCategoryName() %>)</label>
            </td></tr><tr><td>
            <%//решение окончательное
            if(task.getCcStatus().getStatus().getCategoryId().longValue()!=2){ %>
            <tr><td><label>Статус решения (детализация)<br />
            <%
            ArrayList<SpoStatusReturn> statusReturnList = compenduim.findSpoStatusReturnList(
                            new CcResolutionStatus(task.getCcStatus().getStatus().getId(),null,null,null),null);
             if(statusReturnList.size()==0){
                 /*если соответствия не найдено вовсе, то из справочника «Статусы возврата заявки» 
                 допускается выбор тех значений, 
                 которых нет в «Справочнике соответствия статусов возврата из КК».*/
                 ArrayList<CcResolutionStatus> allCcResolutionStatus=compenduim.findCcResolutionStatusList(null,null);
                 statusReturnList = compenduim.findSpoStatusReturnList(new SpoStatusReturn(),null);
                 for(CcResolutionStatus ccs: allCcResolutionStatus){
                    ArrayList<SpoStatusReturn> rem = compenduim.findSpoStatusReturnList(ccs,null); 
                    statusReturnList.removeAll(rem);
                 }
             }
             %>
            <select name="SPOStatusReturn" id="SPOStatusReturn" onchange="SPOStatusReturnChange()" <%=editMode?"":"disabled" %>>
                    <%if(statusReturnList.size()>1){ %><option value="0">не выбрано</option><%} %>
                        <%
                        String id=task.getTaskStatusReturn().getStatusReturn().getId();
                        if (id == null) id = "";
                        for(SpoStatusReturn statusReturn:statusReturnList){
                             %>
                            <option value="<%=statusReturn.getId() %>"
                            <%=id.trim().equals(statusReturn.getId().toString().trim())?"selected":"" %>>
                            <%=statusReturn.getName() %></option>
                        <%} %>
            </select>
            </label>
            </td></tr>
            <%} %>
            </table>
		</td>
			</tr>
<%if(false && status.getCategoryId().longValue()!=2){ %>
	<script language="javascript">
		function SPOStatusReturnChange() {
		    try {
			    id=document.getElementById('SPOStatusReturn').value
			    document.getElementById('btnRegister').disabled = id=='0'
			    if (id=='0'){ $('#btnRegister').addClass('disabled');} else {$('#btnRegister').removeClass('disabled');}
		    } catch (Err) {}
		}
		
		SPOStatusReturnChange();
	</script>
<%}else{ %>
	<script language="javascript">
		function SPOStatusReturnChange() {}
	</script>
<%}%>
				<%
		} catch (Exception e) {
			out.println("Ошибка в секции  frame_statusreturnCC_jsp:" + e.getMessage());
			e.printStackTrace();
		}
%>
		</tbody>
	</table>
<%}%>
