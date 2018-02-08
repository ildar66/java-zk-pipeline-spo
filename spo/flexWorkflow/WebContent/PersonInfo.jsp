<!DOCTYPE html PUBLIC "-//W3C//DTD HTML 4.01//EN" "http://www.w3.org/TR/html4/strict.dtd">
<%@page language="java" contentType="text/html; charset=utf-8"
	pageEncoding="utf-8"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-html" prefix="html"%>
<%@taglib uri="http://jakarta.apache.org/struts/tags-bean" prefix="bean"%>
<%@page import="ru.masterdm.compendium.model.CompendiumSpoActionProcessor"%>
<%@page import="java.text.SimpleDateFormat"%>
<%@page import="ru.masterdm.compendium.domain.spo.Person"%>
<%@page import="ru.masterdm.compendium.domain.spo.RightToStayDoc"%>
<%@page import="ru.masterdm.compendium.domain.spo.MigrationCard"%>
<%@page import="ru.masterdm.compendium.domain.spo.ActivityLicense"%>
<%@page import="com.vtb.util.Formatter"%>
<%@page import="ru.md.pup.dbobjects.DocumentGroupJPA"%>
<%@page import="ru.md.spo.ejb.PupFacadeLocal" %>
<%@ page import="ru.masterdm.spo.utils.SBeanLocator" %>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<html:html>
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<meta http-equiv="X-UA-Compatible" content="IE=Edge" />
	<title>Информация о физическом лице</title>
	<script type="text/javascript" src="scripts/sign/MDLib2.js"></script>
	<link rel="stylesheet" href="style/style.css" />
	<link rel="stylesheet" href="style/jquery.fancybox-1.3.4.css" />
	<link rel="stylesheet" href="style/jquery-ui-1.10.3.custom.min.css" />
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
    <base target="_self">
</head>
<body>
<script type="text/javascript" src="scripts/tooltip.js"></script>
<script type="text/javascript" src="scripts/wz_tooltip/wz_tooltip.js"></script>
<jsp:include flush="true" page="headerVTB.jsp" />
<%String id=request.getParameter("id");
PupFacadeLocal pupFacade = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
SimpleDateFormat df = new SimpleDateFormat("dd.MM.yyyy");
CompendiumSpoActionProcessor compenduimSPO = (CompendiumSpoActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumSpo");
Person p = new Person(new Long(id)); 
p = compenduimSPO.findPersonPage(p,0,1,null).getList().get(0);
 %>
 <br />
 <h1 class="contractor"><span class="podpis">Информация о физическом лице</span> <%=p.getLastName()%></h1>
 <div class="controlPanel">
			<button iconClass="flatScreenIcon" id="close" onclick="window.close()">
				Закрыть
			</button>
</div>
		<div>
			<div style="float:left;">
				<table class="regular">
					<tr><th colspan="2">Основная информация</th></tr>
					<tr>
						<td>Фамилия</td>
						<td><%=Formatter.str(p.getLastName())%></td>
					</tr>
					<tr>
						<td>Имя</td>
						<td><%=Formatter.str(p.getName())%></td>
					</tr>
					<tr>
						<td>Отчество</td>
						<td><%=Formatter.str(p.getMiddleName())%></td>
					</tr>
					<tr>
						<td>Дата рождения</td>
						<td><%=(p.getBirthday() != null) ? df.format(p.getBirthday()) : ""%></td>
					</tr>
					<tr>
						<td>ИНН</td>
						<td><%=Formatter.str(p.getTaxIdentificationCode())%></td>
					</tr>
					<tr>
						<td>Гражданин РФ</td>
						<td><%=p.getIsRussian()?"да":"нет"%></td>
					</tr>
					<tr>
						<td>Гражданство</td>
						<td><%=Formatter.str(p.getCountry().getShortName()) %></td>
					</tr>
					<tr>
						<td>Является предпринимателем</td>
						<td><%=p.getIsEntrepreneur()?"да":"нет"%></td>
					</tr>
					<tr>
						<td>Лицензируемая деятельность</td>
						<td><%=p.getIsLicensableActivity()?"да":"нет"%></td>
					</tr>
					<tr>
						<td>Номер телефона 1</td>
						<td><%=Formatter.str(p.getPhone1()) %></td>
					</tr>
					<tr>
						<td>Номер телефона 2</td>
						<td><%=Formatter.str(p.getPhone2()) %></td>
					</tr>
					<tr>
						<td>Номер факса 1</td>
						<td><%=Formatter.str(p.getFax1()) %></td>
					</tr>
					<tr>
						<td>Номер факса 2</td>
						<td><%=Formatter.str(p.getFax2()) %></td>
					</tr>
					<tr><th colspan="2">Документ, удостоверяющий личность</th></tr>
					<tr>
						<td>Тип документа, удостоверяющего личность</td>
						<td><%=Formatter.str(p.getIdentityDocType().getName()) %></td>
					</tr>
					<tr>
						<td>Серия документа, удостоверяющего личность</td>
						<td><%=Formatter.str(p.getIdentityDocSeries())%></td>
					</tr>
					<tr>
						<td>Номер документа, удостоверяющего личность</td>
						<td><%=Formatter.str(p.getIdentityDocNumber())%></td>
					</tr>
					<tr>
						<td>Кем выдан документ</td>
						<td><%=Formatter.str(p.getIdentityDocWho())%></td>
					</tr>
					<tr>
						<td>Дата выдачи документа, удостоверяющего личность</td>
						<td><%=p.getIdentityDocDate()==null?"":df.format(p.getIdentityDocDate()) %></td>
					</tr>
					<tr>
						<td>Код подразделения, выдавшего документ, удостоверяющий личность</td>
						<td><%=Formatter.str(p.getIdentityDocSubdivisionCode()) %></td>
					</tr>
					<tr>
						<td>Место выдачи документа, удостоверяющего личность</td>
						<td><%=Formatter.str(p.getIdentityDocWhere()) %></td>
					</tr>
					<tr>
						<td>Адрес регистрации</td>
						<td><%=Formatter.str(p.getRegistrationAddress())%></td>
					</tr>
					<tr>
						<td>Фактический адрес</td>
						<td><%=Formatter.str(p.getHomeAddress())%></td>
					</tr>
					<% if (p.getIsEntrepreneur()) { %>
						<tr><th colspan="2">Индивидуальный предприниматель</th></tr>
						<tr>
							<td>Дата регистрации индивидуального предпринимателя</td>
							<td><%=p.getEntrepreneurRegDate()==null?"":df.format(p.getEntrepreneurRegDate()) %></td>
						</tr>
						<tr>
							<td>Государственный регистрационный номер индивидуального предпринимателя</td>
							<td><%=Formatter.str(p.getEntrepreneurRegNumber()) %></td>
						</tr>
						<tr>
							<td>Наименование регистрирующего органа у этого индивидуального предпринимателя</td>
							<td><%=Formatter.str(p.getEntrepreneurRegBody()) %></td>
						</tr>
						<tr>
							<td>Место регистрации индивидуального предпринимателя</td>
							<td><%=Formatter.str(p.getEntrepreneurRegPlace()) %></td>
						</tr>
					<% } %>
					<% if ((p.getRightToStayDocList().size() > 0) || (p.getMigrationCardList().size() > 0)) {%> 
						<tr><th colspan="2">Документы иностранного гражданина</th></tr>
					<% } %>
					<%for(RightToStayDoc r :p.getRightToStayDocList()){  %>
					<tr>
						<td>Документ, подтверждающий право иностранного гражданина или лица без гражданства на пребывание (проживание) в РФ</td>
						<td>серия <%=r.getSeries()%>, номер <%=r.getNumber() %>
						<br>Документ действителен  с <%=Formatter.format(r.getStartDate()) %> по <%=Formatter.format(r.getEndDate()) %>  
						</td>
					</tr>
					<%} %> 
					<%
					 for(MigrationCard m : p.getMigrationCardList()){ %>
					<tr>
						<td>Миграционная карта</td>
						<td>номер <%=m.getNumber() %>
							<br>Документ действителен  с <%=Formatter.format(m.getStartDate()) %> по <%=Formatter.format(m.getEndDate()) %>
						</td>
					</tr>
					<%} %>
					<% if (p.getActivityLicenseList().size() > 0) {%> 
						<tr><th colspan="2">Лицензируемая деятельность</th></tr>
					<% } %>
					<%for(ActivityLicense al : p.getActivityLicenseList()){%>
					<tr>
						<td>Лицензия на осуществление лицензируемого вида деятельности</td>
						<td>тип <%=al.getType()%>, номер <%=al.getNumber() %>
                        	<br>Документ действителен  с <%=Formatter.format(al.getStartDate()) %> по <%=Formatter.format(al.getEndDate()) %>
                        </td>
					</tr>
					<tr>
						<td>Перечень видов лицензируемой деятельности</td>
						<td><%=Formatter.str(al.getActivitiesTypeList())%></td>
					</tr>
					<%} %>
					
					<tr><th colspan="2">&nbsp;&nbsp;</th></tr>
					<tr>
						<td>Дополнительный текстовый комментарий</td>
						<td><%=Formatter.str(p.getNote()) %></td>
					</tr>
				</table>
			</div>
<br />
<md:frame mdtaskid="<%=id %>" readOnly="false"
                frame_name="documents" empty="false" 
                header="Документы по физическому лицу" pupTaskId="2" />
</div>


<div id="editDocumentForm" title="Изменить документ" style="display: none;">
<input id="attach_unid" value="" type="hidden">
К контрагенту <%=p.getLastName() %>
<table class="regular">
<tr><td>Заголовок</td><td><input id="attach_title" value=""></td></tr>
<tr><td>Срок действия</td><td><input id="attach_period" value="" onFocus="displayCalendarWrapper('attach_period', '', false); return false;"></td></tr>
<tr><td>Группа документа</td><td>
<select id="attach_docGroup" onchange="$('.attach_doctype').hide();$('#attach_doctype'+$('#attach_docGroup').val()).show()">
<% for (DocumentGroupJPA docGroup : pupFacade.findDocumentGroupByOwnerTYpe(2L)) {
if(!docGroup.isActive()){continue;}%>
<option value="<%=docGroup.getId() %>"><%=docGroup.getNAME_DOCUMENT_GROUP() %></option>
<%} %>
</select>
</td></tr>
<tr><td>Тип документа</td><td>
<% for (DocumentGroupJPA docGroup : pupFacade.findDocumentGroupByOwnerTYpe(2L)) {%>
<select id="attach_doctype<%=docGroup.getId()%>" class="attach_doctype">
<%for(ru.md.pup.dbobjects.DocumentTypeJPA type : docGroup.getTypes()){
if(!type.isActive() || !pupFacade.isDocumentGroupTypeActive(docGroup.getId(), type.getId())){continue;}%>
<option value="<%=type.getId() %>"><%=type.getName() %></option>
<%} %>
</select>
<%} %>
</td></tr>
</table>
<br /><a href="javascript:;" onclick="$.post('ajax/editAttach.html',{doctype:$('#attach_doctype'+$('#attach_docGroup').val()).val(),group: $('#attach_docGroup').val(),unid: $('#attach_unid').val(), title:$('#attach_title').val(),exp:$('#attach_period').val()},refreshDocFrame);$('#editDocumentForm').dialog('close');">изменить</a>
<a href="javascript:;" onclick="$('#editDocumentForm').dialog('close');">Отмена</a>
</div>

</body>
</html:html>