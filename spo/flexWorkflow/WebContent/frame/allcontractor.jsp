<%@page isELIgnored="true" %>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@page import="ru.md.helper.TaskHelper" %>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%@ page import="ru.md.domain.MdTask" %>
<%@ page import="ru.md.domain.ContractorType" %>
<%@ page import="ru.md.domain.TaskKz" %>
<%@ page import="java.util.*" %>
<%@ page import="com.vtb.domain.Task" %>
<%@ page import="com.vtb.model.TaskActionProcessor" %>
<%@ page import="com.vtb.model.ActionProcessorFactory" %>
<%@ page import="com.vtb.domain.AbstractSupply" %>
<%@ page import="com.vtb.util.CollectionUtils" %>
<%@ page import="ru.masterdm.compendium.domain.spo.Person" %>
<%@ page import="ru.masterdm.compendium.model.CompendiumSpoActionProcessor" %>
<%@ page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@ page import="ru.masterdm.spo.utils.*" %>
<%
response.addHeader("Pragma", "no-cache");
response.addHeader("Expires", "-1");
response.addHeader("Cache-control", "no-cache");
try{
	CompendiumSpoActionProcessor compenduimSPO = (CompendiumSpoActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumSpo");
	TaskActionProcessor processor = (TaskActionProcessor) ActionProcessorFactory.getActionProcessor("Task");
	TaskFacadeLocal taskFacade = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);

	MdTask mdtask = TaskHelper.getMdTask(request);
	Task task=processor.getTask(new Task(Long.valueOf(request.getParameter("mdtaskid"))));
	ru.md.spo.dbobjects.TaskJPA taskJPA=taskFacade.getTask(Long.valueOf(request.getParameter("mdtaskid")));
	HashMap<String,HashSet<String>> orgs = new LinkedHashMap<String,HashSet<String>>();
	HashMap<Long,HashSet<String>> persons = new LinkedHashMap<Long,HashSet<String>>();

	for (TaskKz kz : SBeanLocator.singleton().compendium().getTaskKzByMdtask(mdtask.getIdMdtask())) {
		String ekid = TaskHelper.dict().getOrg(kz.getKzid()).getIdUnitedClient()==null?kz.getKzid():TaskHelper.dict().getOrg(kz.getKzid()).getIdUnitedClient();
		if(!orgs.containsKey(ekid)) {orgs.put(ekid, new LinkedHashSet<String>());}
		for(Long idCT: SBeanLocator.singleton().compendium().getContractorTypeIdByIdR(kz.getIdR())){
			for(ContractorType ct : SBeanLocator.singleton().compendium().findContractorTypeList()) {
				if(idCT.equals(ct.getId()))
					if(kz.getOrderDisp() != null && kz.getOrderDisp().equals(0L)&&ct.getName().equals("Заемщик"))
						orgs.get(ekid).add("Основной заемщик");
				    else
						orgs.get(ekid).add(ct.getName());
			}
		}
	}
	for(AbstractSupply supply : task.getSupply().getAllSupply()) {
		if (supply.getOrg() != null && supply.getOrg().getId() != null && !supply.getOrg().getId().isEmpty()) {
			String ekid = TaskHelper.dict().getOrg(supply.getOrg().getId()).getIdUnitedClient() == null ? supply.getOrg().getId() : TaskHelper.dict().getOrg(supply.getOrg().getId()).getIdUnitedClient();
			if (!orgs.containsKey(ekid)) {orgs.put(ekid, new LinkedHashSet<String>());}
			orgs.get(ekid).add(supply.getSupplyTypeName());
		}
		if(supply.getPerson()!=null && supply.getPerson().getId()!=null && !supply.getPerson().getId().equals(0L)){
			if(!persons.containsKey(supply.getPerson().getId())){persons.put(supply.getPerson().getId(),new LinkedHashSet<String>());}
			persons.get(supply.getPerson().getId()).add(supply.getSupplyTypeName());
		}
	}
%>

<br />
<table class="allcontractorsMain">
	<thead>
		<tr>
			<th style="width: 50%;">Наименование контрагента</th>
			<th style="width: 25%;">Тип контрагента</th>
			<th style="width: 10%;">ИНН</th>
			<th style="width: 15%;">ОГРН</th>
		</tr>
	</thead>
	<tbody>
	    <%if(mdtask.isPipelineProcess() && mdtask.getProjectName()!=null){%>
			<tr>
				<td><%=ru.masterdm.spo.utils.Formatter.str(mdtask.getProjectName())%></td><td></td><td></td><td></td>
			</tr>
		<%} %>
		<%for (String ekId : orgs.keySet()) {%>
			<tr>
				<td><a href="clientInfo.html?id=<%=ekId%>&mdtask=<%=mdtask.getIdMdtask().toString()%>" target="_blank">
					<%=SBeanLocator.singleton().getDictService().getEkNameByOrgId(ekId)%></a></td>
				<td><%=CollectionUtils.hashSetJoin(orgs.get(ekId))%></td>
				<td><%=TaskHelper.dict().getOrg(ekId).getInn() %></td>
				<td><%=TaskHelper.dict().getOrg(ekId).getOgrn() %></td>
			</tr>
		<%} %>
		<%for (Long personId : persons.keySet()) {
			Person person = new Person(personId);
			person = compenduimSPO.findPersonPage(person,0,1,null).getList().get(0);%>
			<tr>
				<td><a href="PersonInfo.jsp?id=<%=personId.toString() %>"
					   target=_blank><%=person.getName() %> <%=person.getLastName() %></a></a></td>
				<td><%=CollectionUtils.hashSetJoin(persons.get(personId))%></td>
				<td><%=ru.masterdm.spo.utils.Formatter.str(person.getTaxIdentificationCode())%></td>
				<td></td>
			</tr>
		<%} %>
		<%for(ru.md.spo.dbobjects.PromissoryNoteJPA pn : taskJPA.getPromissoryNotes()){%>
		<td><%=pn.getHolder() %></td><td>Векселедержатель</td><td></td><td></td>
		<%} %>
	</tbody>
</table>
<%
	} catch (Exception e) {
		out.println("Ошибка в секции allcontractor.jsp:" + e.getMessage());
		e.printStackTrace();
	}
%>
