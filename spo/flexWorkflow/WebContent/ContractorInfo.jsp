<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<!-- устарела теперь карточка контрагента на новой странице clientInfo.html-->
<%@page import="ru.md.helper.TaskHelper"%>
<%@page import="ru.md.spo.dbobjects.OrgJPA"%>
<%@page import="ru.md.pup.dbobjects.DocumentGroupJPA"%>
<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@page import="java.text.SimpleDateFormat" %>
<%@page import="ru.md.spo.ejb.PupFacadeLocal" %>
<%@page import="ru.masterdm.compendium.domain.crm.Organization"%>
<%@taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@page import="ru.masterdm.compendium.domain.crm.CompanyGroup" %>
<%@page import="ru.masterdm.compendium.domain.spo.Shareholder"%>
<%@page import="ru.masterdm.compendium.domain.spo.Contact"%>
<%@page import="com.vtb.util.Formatter"%>
<%@page import="ru.masterdm.compendium.model.CompendiumCrmActionProcessor"%>
<%@ page import="ru.masterdm.spo.utils.SBeanLocator" %>
<%@ page import="ru.md.domain.Org" %>
<%@ page import="java.util.List" %>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<html:html>
<head>
    <base target="_self">
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
	<title>Информация о контрагенте</title>
	<link rel="stylesheet" href="style/style.css" />
	<link rel="stylesheet" href="style/jquery-ui-1.10.3.custom.min.css" />
	<link rel="stylesheet" href="style/jquery.fancybox-1.3.4.css" />
	<script language="javascript" src="scripts/date.js"></script>
	<script language="javascript" src="scripts/applicationScripts.js"></script>
	<script type="text/javascript" src="scripts/sign/MDLib2.js"></script>
	<script type="text/javascript" src="scripts/jquery/jquery-1.10.2.min.js"></script>
	<script type="text/javascript" src="scripts/jquery/jquery-migrate-1.2.1.js"></script>
    <script type="text/javascript" src="scripts/jquery/jquery-ui-1.10.3.custom.min.js"></script>
	<script type="text/javascript" src="scripts/jquery/jquery.cookie.js"></script>
	<script type="text/javascript" src="scripts/jquery/jquery.easing.1.3.js"></script>
    <script type="text/javascript" src="scripts/jquery/jquery.fancybox-1.3.4.pack.js"></script>
	<script language="javascript" src="scripts/form/frame.js"></script>
	<%  String cryptoIssuers = SBeanLocator.singleton().getCompendiumMapper().getCryptoIssuers();
		if (cryptoIssuers == null)
			cryptoIssuers = "";	%>
	<script type="text/javascript">
		var cryptoIssuers = "";
		cryptoIssuers = "<%=cryptoIssuers%>";
	</script>
	<script type="text/javascript" src="scripts/sign/mdSignature.js"></script>
	<script language="javascript" src="scripts/form/attach.js"></script>
	<link type="text/css" rel="stylesheet" href="/compendium/calendar/dhtmlgoodies_calendar.css">
    <script language="JavaScript" src="/compendium/calendar/dhtmlgoodies_calendar.js"></script>

</head>
<body class="soria">
    <script type="text/javascript" src="scripts/tooltip.js"></script>
    <script type="text/javascript" src="scripts/wz_tooltip/wz_tooltip.js"></script>
	<input type="hidden" name="id_contractor" value="<%=ru.masterdm.spo.utils.Formatter.str(request.getParameter("crmid"))%>">
	<input type="hidden" id="mdtaskid" value="<%=ru.masterdm.spo.utils.Formatter.str(request.getParameter("mdtask"))%>">
	<%if(request.getParameter("mdtask")!=null){%>
		<input type="hidden" id="ID_INSTANCE" value="<%=Formatter.str(TaskHelper.getMdTask(request).getIdInstance())%>" />
	<%} else {%>
		<input type="hidden" id="ID_INSTANCE" value="" />
	<%}%>
	<%
	CompendiumCrmActionProcessor compenduim = (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
	PupFacadeLocal pupFacade = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
	com.vtb.model.TaskActionProcessor processor = (com.vtb.model.TaskActionProcessor) com.vtb.model.ActionProcessorFactory.getActionProcessor("Task");
	String idMdtask = request.getParameter("mdtask");

        String contractorCrmid = request.getParameter("crmid");
        if(request.getParameter("hideRating")==null){ %>
		<div class="controlPanel">
			<button iconClass="flatScreenIcon" id="close" onclick="window.close()">
				Закрыть
			</button>
			&nbsp;
			<button iconClass="flatScreenIcon" id="rating" onclick="javascript:window.open('/RATING/view_partner.faces?SPO=1&id=<%=contractorCrmid%>&mdtask=<%=idMdtask != null ? idMdtask : ""%>','','')">
				Рейтинг
			</button>
		</div>
	<%}
	Organization org = null;
	String id = null;
	boolean fkr=false;
try {
        OrgJPA currentOrg = TaskHelper.dict().getOrg(contractorCrmid);

        Organization kzOrg = processor.getOrganizationFullData(contractorCrmid);
        if (kzOrg == null)
            kzOrg = compenduim.findOrganization(contractorCrmid);

    String idUnitedClient = currentOrg.getIdUnitedClient();
		//начало ЧАСТИ ПО ЕДИНОМУ КЛИЕНТУ===========================================================
		id = idUnitedClient == null? contractorCrmid : idUnitedClient;
		try{
			fkr = ru.masterdm.integration.ServiceFactory.getService(ru.masterdm.integration.monitoring.MonitoringService.class).isFkrExcludeWrongIdentifiedByContractor(id);
		} catch (Throwable e) {
			System.out.println("fkr ERROR ON ContractorInfo.jsp, united client:" + e.getMessage());
			e.printStackTrace();
		}
		org = processor.getOrganizationFullData(id);
		OrgJPA orgJPA = TaskHelper.dict().getOrg(id);
		if (org == null) org = compenduim.findOrganization(id);
		Org ek = SBeanLocator.singleton().compendium().getEkById(id);
		%>
		<div style="display: block;">
		<h1 class="contractor"><span class="podpis">Информация о едином клиенте</span> <%=(org != null) ? org.getAccount_name() : ""%>
			<%if(fkr){ %> <a href="/km-web/fkr/list/contractor/<%=org.getAccountid()%>"><span class="error">ФКР</span></a><%} %></h1>
		<h2>Основная информация</h2>
		<div>
				<table class="regular">
					<tr>
						<td>Наименование</td>
						<td><%=(org != null && org.getAccount_name() != null) ? org.getAccount_name() : ""%></td>
					</tr>
            <% if (org != null) { %>
                <% if (org.getClientCategory() != null && !org.getClientCategory().isEmpty()) {%>
					<tr>
						<td>Категория клиента</td>
						<td><%=(org != null && org.getClientCategory() != null) ? org.getClientCategory() : ""%></td>
					</tr>
                <% } %>
                <% if (org.getRating() != null && org.getRating().getRating() != null && !org.getRating().getRating().isEmpty()) {%>
					<tr>
						<td>Рейтинг кредитного подразделения</td>
						<td><%=(org != null && org.getRating() != null && org.getRating().getRating() != null) ? org.getRating().getRating() : ""%></td>
					</tr>
                <% } %>
                <% if (orgJPA.getIndustry() != null && !orgJPA.getIndustry().isEmpty()) {%>
					<tr>
						<td>Отрасль</td>
						<td><%=orgJPA.getIndustry()%></td>
					</tr>
                <% } %>
                <% if (org.getBusinessDescription() != null && !org.getBusinessDescription().isEmpty()) {%>
					<tr>
						<td>Описание бизнеса</td>
						<td><%=(org != null && org.getBusinessDescription() != null) ? org.getBusinessDescription() : ""%></td>
					</tr>
                <% } %>
                <% if (orgJPA.getOgrn() != null && !orgJPA.getOgrn().isEmpty()) {%>
					<tr>
						<td>ОГРН</td>
						<td><%=orgJPA.getOgrn()%></td>
					</tr>
                <% } %>
                <% if (org.getWorkPhone() != null && !org.getWorkPhone().isEmpty()) {%>
					<tr>
						<td>Телефон компании</td>
						<td><%=(org != null && org.getWorkPhone() != null) ? org.getWorkPhone() : ""%></td>
					</tr>
                <% } %>
                <% if (org.getFax() != null && !org.getFax().isEmpty()) {%>
					<tr>
						<td>Факс компании</td>
						<td><%=(org != null && org.getFax() != null) ? org.getFax() : ""%></td>
					</tr>
                <% } %>
                <% if (org.getEmail() != null && !org.getEmail().isEmpty()) {%>
					<tr>
						<td>E-Mail организации</td>
						<td><%=(org != null && org.getEmail() != null) ? org.getEmail() : ""%></td>
					</tr>
                <% } %>
                <% if (org.getInn() != null && !org.getInn().isEmpty()) {%>
					<tr>
						<td>ИНН</td>
						<td><%=(org != null && org.getInn() != null) ? org.getInn() : ""%></td>
					</tr>
                <% } %>
            <% } %>
				</table>
		</div>
			<!--группы-->
			<%if (ek != null && !ek.getGroupname().isEmpty()) {
			%>
			<table class="pane" id="section_Группы_ek" style="width:99%">
				<thead onclick="doSection('Группы_ek')" onselectstart="return false">
				<tr>
					<td>
						<img alt="Развернуть" src="style/toOpen.gif" align="middle" id="imgSection">
						<span>Группа, в состав которой входит организация</span>
					</td>
				</tr>
				</thead>
				<tbody style="display:none">
				<tr>
					<td>
						<table class="regular">
							<thead>
							<tr>
								<th>Имя группы</th>
								<th>Тип группы</th>
								<th>Описание</th>
							</tr>
							</thead>
							<tbody>
							<tr>
								<td style="width: 350px"><%=ek.getGroupname() %></td>
								<td></td>
								<td></td>
							</tr>
							</tbody>
						</table>
					</td>
				</tr>
				</tbody>
			</table>
			<%} %>
		<%
		if (org == null || org.getShareholders() ==null || org.getShareholders().isEmpty())
			{
			%><%--Акционеры организации неизвестны<br/>--%><%
			}else{
			%>
		<table class="pane" id="section_АкционерыЕК" style="width:99%">
			<thead onclick="doSection('АкционерыЕК')" onselectstart="return false">
				<tr>
					<td>
						<img alt="Развернуть" src="style/toOpen.gif" align="middle" id="imgSection">
						<span>Акционеры (<%=org.getShareholders().size()%>)</span>
					</td>
				</tr>
			</thead>
			<tbody style="display:none">
				<tr>
					<td>
						<table class="regular">
							<thead>
								<tr>
									<th>Название</th>
									<th>Страна</th>
									<th>Город </th>
									<th>Дата начала владения</th>
									<th>Дата окончания владения</th>
									<th>Процент участия</th>
								</tr>
							</thead>
							<tbody>
								<% for (int i=0;i<org.getShareholders().size();i++){
								Shareholder s=(Shareholder)org.getShareholders().get(i);%>
								<tr><td><%=s.getFio() %></td><td><%=s.getCOUNTRY() %></td><td><%=s.getCITY() %></td>
								<td><%=s.getDATE_BEG()==null?"":s.getDATE_BEG() %></td>
								<td><%=s.getDATE_END()==null?"":s.getDATE_END() %></td>
								<td><%=s.getPart() %></td></tr>
								<%} %>
							</tbody>
						</table>
					</td>
				</tr>
			</tbody>
		</table>
		<%} %>

		<%
            List<ru.md.domain.dict.Contact> contacts = SBeanLocator.singleton().compendium().getContactList(id);
            if (contacts != null && !contacts.isEmpty()) {
			%>
		<table class="pane" id="section_КонтактыЕК" style="width:99%">
			<thead onclick="doSection('КонтактыЕК')" onselectstart="return false">
				<tr>
					<td>
						<img alt="Развернуть" src="style/toOpen.gif" align="middle" id="imgSection">
						<span>Контактные лица (<%=contacts.size()%>)</span>
					</td>
				</tr>
			</thead>
			<tbody style="display:none">
				<tr>
					<td>
						<table class="regular">
							<thead>
								<tr>
									<th>Должность</th>
									<th>Имя</th>
									<th>Отчество</th>
									<th>Фамилия</th>
									<th>Подразделение</th>
									<th>Описание</th>
								</tr>
							</thead>
							<tbody>
								<% for (ru.md.domain.dict.Contact m : contacts){%>
									<tr>
										<td><%=Formatter.str(m.getTitle()) %></td>
										<td><%=Formatter.str(m.getFirstname()) %></td>
										<td><%=Formatter.str(m.getMiddlename()) %></td>
										<td><%=Formatter.str(m.getLastname()) %></td>
										<td><%=Formatter.str(m.getDepartment()) %></td>
										<td><%=Formatter.str(m.getDescription()) %></td>
									</tr>
								<%} %>
							</tbody>
						</table>
					</td>
				</tr>
			</tbody>
		</table>
		<%} %>
		<%if (org == null || org.getCompanyGovernances() == null || org.getCompanyGovernances().isEmpty())
			{
			%><%--Нет информации об органах управления компанией<br/>--%><%
			} else {
			%>
		<table class="pane" id="section_УправлениеЕК" style="width:99%">
			<thead onclick="doSection('УправлениеЕК')" onselectstart="return false">
				<tr>
					<td>
						<img alt="Развернуть" src="style/toOpen.gif" align="middle" id="imgSection">
						<span>Управление компанией (<%=org.getCompanyGovernances().size() %>)</span>
					</td>
				</tr>
			</thead>
			<tbody style="display:none">
				<tr>
					<td>
						<table class="regular">
							<thead>
								<tr>
									<th>Название</th>
									<th>Периодичность действия</th>
									<th>Тип документа</th>
								</tr>
							</thead>
							<tbody>
								<% for (int i=0;i<org.getCompanyGovernances().size();i++){
									ru.masterdm.compendium.domain.crm.CompanyGovernance g=org.getCompanyGovernances().get(i);%>
									<tr>
										<td><%=g.getName() %></td>
										<td><%=g.getActivityPeriod() %></td>
										<td><%=g.getDocType() %></td>
									</tr>
								<%} %>
							</tbody>
						</table>
					</td>
				</tr>
			</tbody>
		</table>
		<%} %>

		</div>
		<%//КОНЕЦ ЧАСТИ ПО ЕДИНОМУ КЛИЕНТУ===========================================================
		id = contractorCrmid;
		fkr=false;
		try{
		    fkr = ru.masterdm.integration.ServiceFactory.getService(ru.masterdm.integration.monitoring.MonitoringService.class).isFkrExcludeWrongIdentifiedByContractor(id);
		} catch (Throwable e) {
		    System.out.println("fkr ERROR ON ContractorInfo.jsp:" + e.getMessage());
	        e.printStackTrace();
	    }
		org = kzOrg;
		orgJPA = TaskHelper.dict().getOrg(id);
            if(orgJPA.getIdUnitedClient()!=null){
		String title = orgJPA.getIdUnitedClient()==null?"Информация о контрагенте":"Информация о клиенте, указанная по месту проведения сделки";
		%>
		<div style="display: block;">
		<h1 class="contractor"><span class="podpis"><%=title %></span> <%=(org != null) ? org.getAccount_name() : ""%>
		<%if(fkr){ %> <a href="/km-web/fkr/list/contractor/<%=org.getAccountid()%>"><span class="error">ФКР</span></a><%} %></h1>
		<h2>Основная информация</h2>
		<div>
				<table class="regular">
					<tr>
						<td>Наименование</td>
						<td><%=(org != null && org.getAccount_name() != null) ? org.getAccount_name() : ""%></td>
					</tr>
            <% if (org != null) { %>
                <% if (org.getClientCategory() != null && !org.getClientCategory().isEmpty()) {%>
					<tr>
						<td>Категория клиента</td>
						<td><%=(org != null && org.getClientCategory() != null) ? org.getClientCategory() : ""%></td>
					</tr>
                <% } %>
                <% if (org.getRating() != null && org.getRating().getRating() != null && !org.getRating().getRating().isEmpty()) {%>
					<tr>
						<td>Рейтинг кредитного подразделения</td>
						<td><%=(org != null && org.getRating() != null && org.getRating().getRating() != null) ? org.getRating().getRating() : ""%></td>
					</tr>
                <% } %>
                <% if (orgJPA.getIndustry() != null && !orgJPA.getIndustry().isEmpty()) {%>
					<tr>
						<td>Отрасль</td>
						<td><%=orgJPA.getIndustry()%></td>
					</tr>
                <% } %>
                <% if (org.getBusinessDescription() != null && !org.getBusinessDescription().isEmpty()) {%>
					<tr>
						<td>Описание бизнеса</td>
						<td><%=(org != null && org.getBusinessDescription() != null) ? org.getBusinessDescription() : ""%></td>
					</tr>
                <% } %>
                <% if (org.getDivision() != null && !org.getDivision().isEmpty()) {%>
					<tr>
						<td>Обслуживающее подразделение</td>
						<td><%=(org != null && org.getDivision() != null) ? org.getDivision() : ""%></td>
					</tr>
                <% } %>
                <% if (org.getDepartment() != null && !org.getDepartment().isEmpty()) {%>
					<tr>
						<td>Филиал</td>
						<td><%=(org != null && org.getDepartment() != null) ? org.getDepartment() : ""%></td>
					</tr>
                <% } %>
                <% if (org.getCorpBlock() != null && !org.getCorpBlock().isEmpty()) {%>
					<tr>
						<td>Корпоративный блок</td>
						<td><%=(org != null && org.getCorpBlock() != null) ? org.getCorpBlock() : ""%></td>
					</tr>
                <% } %>
                <% if (orgJPA.getOgrn() != null && !orgJPA.getOgrn().isEmpty()) {%>
					<tr>
						<td>ОГРН</td>
						<td><%=orgJPA.getOgrn()%></td>
					</tr>
                <% } %>
                <% if (org.getRating() != null && org.getRating().getRegion() != null && !org.getRating().getRegion().isEmpty()) {%>
					<tr>
						<td width="15%">Регион</td>
						<td width="35%"><%=(org != null && org.getRating() != null && org.getRating().getRegion() != null) ? org.getRating().getRegion() : ""%></td>
					</tr>
                <% } %>
                <% if (org.getWorkPhone() != null && !org.getWorkPhone().isEmpty()) {%>
					<tr>
						<td>Телефон компании</td>
						<td><%=(org != null && org.getWorkPhone() != null) ? org.getWorkPhone() : ""%></td>
					</tr>
                <% } %>
                <% if (org.getFax() != null && !org.getFax().isEmpty()) {%>
					<tr>
						<td>Факс компании</td>
						<td><%=(org != null && org.getFax() != null) ? org.getFax() : ""%></td>
					</tr>
                <% } %>
                <% if (org.getEmail() != null && !org.getEmail().isEmpty()) {%>
					<tr>
						<td>E-Mail организации</td>
						<td><%=(org != null && org.getEmail() != null) ? org.getEmail() : ""%></td>
					</tr>
                <% } %>
                <% if (org.getInn() != null && !org.getInn().isEmpty()) {%>
					<tr>
						<td>ИНН</td>
						<td><%=(org != null && org.getInn() != null) ? org.getInn() : ""%></td>
					</tr>
                <% } %>
                <% if (org.getKpp() != null && !org.getKpp().isEmpty()) {%>
					<tr>
						<td>КПП</td>
						<td><%=(org != null && org.getKpp() != null) ? org.getKpp() : ""%></td>
					</tr>
                <% } %>
                <% if (org.getDateOfRegistration() != null) {%>
					<tr>
						<td>Дата учреждения компании</td>
						<td><%=(org != null && org.getDateOfRegistration() != null) ? Formatter.format(org.getDateOfRegistration()) : ""%></td>
					</tr>
                <% } %>
             <% } %>
				</table>
		</div>
		</div>
<!--группы-->
		<%if (org == null || org.getCompanyGroups() == null || org.getCompanyGroups().isEmpty())
			{
			%><%--Организация не входит ни в одну группу<br/>--%><%
			}else{
			%>
		<table class="pane" id="section_Группы" style="width:99%">
			<thead onclick="doSection('Группы')" onselectstart="return false">
				<tr>
					<td>
						<img alt="Развернуть" src="style/toOpen.gif" align="middle" id="imgSection">
						<span>Группы, в состав которых входит организация (<%=org.getCompanyGroups().size() %>)</span>
					</td>
				</tr>
			</thead>
			<tbody style="display:none">
				<tr>
					<td>
						<table class="regular">
							<thead>
								<tr>
									<th>Имя группы</th>
									<th>Тип группы</th>
									<th>Описание</th>
								</tr>
							</thead>
							<tbody>
							<%for (CompanyGroup group : org.getCompanyGroups()){%>
									<tr>
										<td><%=(group.getName()!= null)?group.getName():"" %></td>
										<td><%=(group.getGroupType()!= null)?group.getGroupType():""  %></td>
										<td><%=(group.getDescription() != null)?group.getDescription():"" %></td>
									</tr>
								<%
								}
							%>
							</tbody>
						</table>
					</td>
				</tr>
			</tbody>
		</table>
		<%} %>


		<%
		SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
		if (org == null || org.getShareholders() ==null || org.getShareholders().isEmpty())
			{
			%><%--Акционеры организации неизвестны<br/>--%><%
			}else{
			%>
		<table class="pane" id="section_Акционеры" style="width:99%">
			<thead onclick="doSection('Акционеры')" onselectstart="return false">
				<tr>
					<td>
						<img alt="Развернуть" src="style/toOpen.gif" align="middle" id="imgSection">
						<span>Акционеры (<%=org.getShareholders().size()%>)</span>
					</td>
				</tr>
			</thead>
			<tbody style="display:none">
				<tr>
					<td>
						<table class="regular">
							<thead>
								<tr>
									<th>Название</th>
									<th>Страна</th>
									<th>Город </th>
									<th>Дата начала владения</th>
									<th>Дата окончания владения</th>
									<th>Процент участия</th>
								</tr>
							</thead>
							<tbody>
								<% for (int i=0;i<org.getShareholders().size();i++){
								Shareholder s=(Shareholder)org.getShareholders().get(i);%>
								<tr><td><%=s.getFio() %></td><td><%=s.getCOUNTRY() %></td><td><%=s.getCITY() %></td>
								<td><%=s.getDATE_BEG()==null?"":s.getDATE_BEG() %></td>
								<td><%=s.getDATE_END()==null?"":s.getDATE_END() %></td>
								<td><%=s.getPart() %></td></tr>
								<%} %>
							</tbody>
						</table>
					</td>
				</tr>
			</tbody>
		</table>
		<%} %>

		<%
		if (org == null || org.getContacts() == null || org.getContacts().isEmpty())
			{
			%><%--Нет информации о контактных лицах компании<br/>--%><%
			}else{
			%>
		<table class="pane" id="section_Контакты" style="width:99%">
			<thead onclick="doSection('Контакты')" onselectstart="return false">
				<tr>
					<td>
						<img alt="Развернуть" src="style/toOpen.gif" align="middle" id="imgSection">
						<span>Контактные лица (<%=org.getContacts().size() %>)</span>
					</td>
				</tr>
			</thead>
			<tbody style="display:none">
				<tr>
					<td>
						<table class="regular">
							<thead>
								<tr>
									<th>Должность</th>
									<th>Имя</th>
									<th>Отчество</th>
									<th>Фамилия</th>
									<th>Подразделение</th>
									<th>Описание</th>
								</tr>
							</thead>
							<tbody>
								<% for (int i=0;i<org.getContacts().size();i++){
									Contact m=(Contact)org.getContacts().get(i);%>
									<tr>
										<td><%=m.getTitle() %></td>
										<td><%=m.getFirstName() %></td>
										<td><%=m.getMiddleName() %></td>
										<td><%=m.getLastName() %></td>
										<td><%=m.getDepartment() %></td>
										<td><%=m.getDescription() %></td>
									</tr>
								<%} %>
							</tbody>
						</table>
					</td>
				</tr>
			</tbody>
		</table>
		<%} %>
		<%if (org == null || org.getCompanyGovernances() == null || org.getCompanyGovernances().isEmpty())
			{
			%><%--Нет информации об органах управления компанией<br/>--%><%
			} else {
			%>
		<table class="pane" id="section_Управление" style="width:99%">
			<thead onclick="doSection('Управление')" onselectstart="return false">
				<tr>
					<td>
						<img alt="Развернуть" src="style/toOpen.gif" align="middle" id="imgSection">
						<span>Управление компанией (<%=org.getCompanyGovernances().size() %>)</span>
					</td>
				</tr>
			</thead>
			<tbody style="display:none">
				<tr>
					<td>
						<table class="regular">
							<thead>
								<tr>
									<th>Название</th>
									<th>Периодичность действия</th>
									<th>Тип документа</th>
								</tr>
							</thead>
							<tbody>
								<% for (int i=0;i<org.getCompanyGovernances().size();i++){
									ru.masterdm.compendium.domain.crm.CompanyGovernance g=org.getCompanyGovernances().get(i);%>
									<tr>
										<td><%=g.getName() %></td>
										<td><%=g.getActivityPeriod() %></td>
										<td><%=g.getDocType() %></td>
									</tr>
								<%} %>
							</tbody>
						</table>
					</td>
				</tr>
			</tbody>
		</table>
		<%} %>
		<%} %>

<!--Документы по контрагенту-->
		<%
		if (org!=null) {
		%>
	<div id="docs">
<md:frame mdtaskid="<%=id %>" readOnly="false"
                frame_name="documents" empty="false" mdtask="<%=idMdtask %>"
                header="Документы по контрагенту" pupTaskId="1" />
	</div>
		<%
		}
		%>
	<%
	} catch (Exception e) {
		out.println("ERROR ON ContractorInfo.jsp:" + e.getMessage());
		e.printStackTrace();
	}
	%>


<div id="editDocumentForm" title="Изменить документ" style="display: none;">
<input id="attach_unid" value="" type="hidden">
К контрагенту <%=org.getAccount_name() %>
<table class="regular" style="width: 700px">
<tr><td>Заголовок</td><td><input id="attach_title" value="" size="80"></td></tr>
<tr><td>Срок действия</td><td><input id="attach_period" value="" onFocus="displayCalendarWrapper('attach_period', '', false); return false;"></td></tr>
<tr><td>Группа документа</td><td>
<input id="attach_docGroup" type="hidden"><a href="javascript:;" onclick="$('#attach_docGroup_popup').dialog({draggable: false, width:500, modal: true});">
<span id="attach_docGroup_name">Выбрать</span></a>
</td></tr>
<tr><td>Тип документа</td><td>
<input id="attach_doctype" type="hidden"><a href="javascript:;" onclick="$('#attach_doctype_popup').dialog({draggable: false, width:700, modal: true});">
<span id="attach_doctype_name">Выбрать</span></a>
</td></tr>
</table>
<br /><a href="javascript:;" onclick="$.post('ajax/editAttach.html',{doctype:$('#attach_doctype').val(),group: $('#attach_docGroup').val(),unid: $('#attach_unid').val(), title:$('#attach_title').val(),exp:$('#attach_period').val()},refreshDocFrame);$('#editDocumentForm').dialog('close');">Изменить</a>
<a href="javascript:;" onclick="$('#editDocumentForm').dialog('close');">Отмена</a>

<div id="attach_docGroup_popup" title="Группа документа" style="display: none;"><ul>
<% for (DocumentGroupJPA docGroup : pupFacade.findDocumentGroupByOwnerTYpe(1L)) {
if(!docGroup.isActive()){continue;}%>
<li><a href="javascript:;" onclick="$('.attach_doctype').hide();$('#attach_doctype<%=docGroup.getId() %>').show();$('#attach_docGroup').val('<%=docGroup.getId()%>');$('#attach_docGroup_name').html('<%=docGroup.getNAME_DOCUMENT_GROUP()%>');$('#attach_doctype').val('');$('#attach_doctype_name').html('Выбрать');$('#attach_docGroup_popup').dialog('close');">
<%=docGroup.getNAME_DOCUMENT_GROUP() %></a></li>
<%} %></ul></div>

<div id="attach_doctype_popup" title="Тип документа" style="display: none;">
<% for (DocumentGroupJPA docGroup : pupFacade.findDocumentGroupByOwnerTYpe(1L)) {%>
<div id="attach_doctype<%=docGroup.getId()%>" class="attach_doctype"><ul>
<%for(ru.md.pup.dbobjects.DocumentTypeJPA type : docGroup.getTypes()){
if(!type.isActive() || !pupFacade.isDocumentGroupTypeActive(docGroup.getId(), type.getId())){continue;}%>
<li><a href="javascript:;" onclick="$('#attach_doctype').val('<%=type.getId()%>');$('#attach_doctype_name').html('<%=type.getName()%>');$('#attach_doctype_popup').dialog('close');">
<%=type.getName() %></a></li>
<%} %>
</ul></div>
<%} %>
</div>

</div>


<!-- Это календарик: -->
            <iframe width="174" height="189" name="gToday:normal:agenda.js" id="gToday:normal:agenda.js" src="calendar/ipopeng.jsp" scrolling="no" frameborder="0" style="visibility:visible; z-index:999; position:absolute; left:-500px; top:0px;"></iframe>
</body>
</html:html>