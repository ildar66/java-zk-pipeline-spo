<!DOCTYPE HTML>
<%@page import="ru.md.helper.TaskHelper"%>
<%@page language="java" contentType="text/html; charset=utf-8" pageEncoding="utf-8"%>
<%@taglib uri="/WEB-INF/struts-html.tld" prefix="html"%>
<%@taglib uri="/WEB-INF/struts-bean.tld" prefix="bean"%>
<%@page import="ru.md.spo.ejb.PupFacadeLocal"%>
<%@page import="ru.md.pup.dbobjects.DepartmentJPA"%>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>
<%@page import="java.util.logging.*"%>
<%@ page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@ page import="ru.md.domain.dict.FundingCompany" %>
<%@ page import="java.util.List" %>
<%@page import="ru.md.domain.dict.CommonDictionary"%>
<%@page import="ru.md.persistence.PupMapper"%>
<%@page import="ru.md.persistence.CompendiumMapper"%>
<%@page import="java.util.List"%>
<%@page import="ru.md.spo.dbobjects.TaskJPA"%>
<%@page import="ru.md.pup.dbobjects.DepartmentJPA"%>
<%@page import="ru.md.spo.dbobjects.PipelineJPA"%>
<%@ page import="ru.md.domain.MdTask" %>
<%@ page import="ru.masterdm.spo.utils.*" %>
<%@ page import="com.vtb.domain.OrgSearchParam" %>

<%
	(new OrgSearchParam(request)).saveCookies(response);//сбросить фильтры
PupFacadeLocal pupFacade = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
TaskFacadeLocal taskFacade = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);

Long mdTaskId = null;
try {
    mdTaskId = Long.valueOf(request.getParameter("mdTaskId"));
} catch (Exception e) {}

boolean renewProcess = mdTaskId != null;
PupMapper pupMapper = SBeanLocator.singleton().getPupMapper();
CompendiumMapper compendiumMapper = SBeanLocator.singleton().getCompendiumMapper();

List<CommonDictionary<Long>> pipelineProcessTypes = renewProcess ? pupMapper.getPipelineProcessTypes(pupFacade.getCurrentUser().getIdUser()) : null;
List<FundingCompany> fundingCompanies = compendiumMapper.getFundCompaniesFull(null);

TaskJPA mdTask = null;
DepartmentJPA initDep = null;
DepartmentJPA placeDep = null;
DepartmentJPA selectedDep = null;
PipelineJPA pipeline = null;

if (renewProcess) {
    mdTask = taskFacade.getTask(mdTaskId);
    initDep = mdTask.getInitDepartment();
    if (mdTask.getPlaceList() != null && mdTask.getPlaceList().size() > 0) {
        placeDep = mdTask.getPlaceList().get(0);
    }
    pipeline = taskFacade.getPipeline(mdTaskId);
}
%>

<html>
<head>
	<title><%=renewProcess ? "Для передачи в СПО заполните форму" : "Создание заявки"%></title>
	<meta http-equiv="X-UA-Compatible" content="IE=edge" />
	<meta http-equiv="Content-Type" content="text/html; charset=utf-8">
	<link rel="stylesheet" href="style/style.css" />
</head>
<body>
<script type="text/javascript" src="scripts/jquery/jquery.min.js"></script>
<%long tstart=System.currentTimeMillis();
request.setAttribute("startTime", tstart);
Logger logger = Logger.getLogger("createApplication");
boolean filialmode=request.getParameter("filialmode")!=null&&request.getParameter("filialmode").equals("true");
if(filialmode){%>
<jsp:include page="filial_header_and_menu.jsp" />
<%}else{ %>
<jsp:include page="header_and_menu.jsp" />
<%} %>
<script src="scripts/form/input_autochange.js" type="text/javascript"></script>
<%	logger.info("=== createApplication 1 "+(System.currentTimeMillis()-tstart));
	DepartmentJPA operatorDepartment = pupFacade.getCurrentUser().getDepartment();
	boolean changeMainOrgEnable =taskFacade.getGlobalSetting("changeMainOrgEnable").equalsIgnoreCase("true");
    logger.info("=== createApplication 2.1 "+(System.currentTimeMillis()-tstart));
%>
<form action="remote.create.process.do" method="post" id="variables" name="variables" >
        <h1><%=renewProcess ? "Для передачи в СПО заполните форму" : "Создание заявки"%></h1>
	<%	//диалог "справочник организаций":
String dialogProperty = "top=100, left=100, width=800, height=610, scrollbars=yes, resizable=yes"; %>
<div id="create_application">
<%logger.info("=== createApplication 3 "+(System.currentTimeMillis()-tstart));
%>
	<div class="podpis" style="width: 100%;">
		<div class="podpis" id="typeLimit">
			<span class="podpis">Тип заявки</span><br>
			<select style="width:60%" name="Тип кредитной заявки0" id="limitSelect" onchange="onTypeChange()" <%=renewProcess ? "disabled='disabled'" : "" %>>
				<option></option>
				<option <%=renewProcess && mdTask.isProduct() ? "selected" : "" %> value="Сделка">Сделка</option>
				<%if(!filialmode){ %><option value="Лимит" <%=renewProcess && mdTask.isLimit() ? "selected" : "" %> >Лимит</option><%} %>
				<option value="Кросс-селл">Кросс-селл</option>
			</select>
			<span style="white-space:nowrap;display: none;" class="error" id="limitSelect_msg">Заполните поле Тип заявки</span>
			<%if (renewProcess) {%>
			<input type="hidden" name="Тип кредитной заявки0" value="<%=mdTask.isProduct() ? "Сделка" : "Лимит"%>" />
			<%}%>

			<%if(!filialmode){ %>
			<div id="inLimit" class="podpis" style="margin: 0; padding: 0;" >
				<span class="podpis">В рамках лимита</span><br>
				<div>
					<input name="inLimitName" type="text" value="выбрать лимит" readonly="readonly" onclick="selectInLimit()">
				</div>
				<input type="hidden" name="inlimitID">
			</div>
			<%} %>
		</div>
	<table style="width: 100%;">
		<tr>
			<td style="padding-right: 10px;">
				Выдающий Банк <br />
				<select style="width: 10em;" id="issuingBank" name="issuingBank"
						onchange="$('#issuingBank_msg').hide();$('#issuingBank_runProcess_msg').hide();">
					<option></option>
                    <%for(FundingCompany fc : fundingCompanies) { %>
    					<option value="<%=fc.getName()%>"
                                runProcess="<%=fc.getRunProcess()%>"
                                <%=pipeline != null && fc.getName().equalsIgnoreCase(pipeline.getVtb_contractor()) ? "selected" : ""%>><%=fc.getName() %></option>
    				<%}%>
				</select>
				<span style="white-space:nowrap;display: none;" class="error" id="issuingBank_msg">Заполните поле Выдающий Банк</span>
				<span style="white-space:nowrap;display: none;" class="error" id="issuingBank_runProcess_msg">Выберите банк с признаком запуска процесса в СПО:<br />
				<%=TaskHelper.getRunProcessFundingCompanies()%></span>
			</td>
			<td>
				Тип процесса <br />
				<span style="white-space:nowrap;">
					<select style="width: 41em;" name="nameTypeProcess" onchange="nameTypeProcessChange()" id="nameTypeProcess">
						<option></option>
                        <%if (renewProcess) {
                            for (CommonDictionary<Long> pipelineProcessType : pipelineProcessTypes) {%>
                                <option value="<%=pipelineProcessType.getName()%>"><%=pipelineProcessType.getName()%></option>
                            <%}
                        } else {
						for (ru.md.pup.dbobjects.ProcessTypeJPA pt : pupFacade.getStartProcessType(pupFacade.getCurrentUser().getIdUser())) {%>
						<option value="<%=pt.getDescriptionProcess()%>"><%=pt.getDescriptionProcess()%></option>
						<%}%>
						<%}%>
					</select>
				</span>
				<span style="white-space:nowrap;display: none;" class="error" id="nameTypeProcess_msg">Заполните поле Тип процесса</span>
			</td>
            <%if (renewProcess) {
                List<FundingCompany> availableFundingCompanies = compendiumMapper.getFundCompaniesFull(true);
                StringBuilder fundingCompaniesStr = new StringBuilder();
                for (int i = 0; i < availableFundingCompanies.size(); i++) {
                    fundingCompaniesStr.append(availableFundingCompanies.get(i).getName()).append(", ");
                }
                %>

                <td>
                    <span style="white-space:nowrap; display: none;" class="error" id="invalid_funding_company">
                        Для запуска процесса в СПО выберите Выдающий банк из списка:<br/><%=fundingCompaniesStr.substring(0, fundingCompaniesStr.lastIndexOf(","))%>
                    </span>
                </td>
            <%}%>
		</tr>
	</table>
	</span>
	</div>
	<%
		String contractorName ="не выбрано";
		String contractorID = "";
		String openUrl = "popup_org.jsp?first=first&formName=variables&ek=only&fieldNames=IDSPO_Contractors0|selectedName|IDCRM_Contractors0&onMySelect=AddLink()";
		if (renewProcess) {
			MdTask task = SBeanLocator.singleton().mdTaskMapper().getById(mdTaskId);
			if(task.getProjectName() == null){
				contractorName = ru.masterdm.spo.utils.Formatter.strWeb(
						SBeanLocator.singleton().getDictService().getEkNameByOrgId(mdTask.getOrgList().get(0).getId()));
				contractorID = mdTask.getOrgList().get(0).getId();
				openUrl += "&filter=" + contractorName;
			}
		}
	%>
	<input type="hidden" id="CRMcontractorID" name="IDCRM_Contractors0" value="<%=contractorID%>"/>
	<div class="podpis" id="selectedNameDiv">
		<span class="podpis">Контрагент (CRM)</span><br>
		<span style="white-space:nowrap;">
			<input type="text" class="text" readonly="true" name="selectedName" id="selectedName" value="<%=contractorName%>"  style="width:130%;"
			onclick='return openDialog("<%=openUrl%>", "organizationLookupList", "<%= dialogProperty%>");' />
		</span>
		<div style="display: none;color:red" id="empty_CRMcontractor">Заполните поле Контрагент (CRM)</div>
	</div>
	<div class="podpis" id="projectNameDiv">
		<div style="display: none;color:red" id="empty_project_name">Заполните поле Контрагент (CRM)<br /> или<br />Название проекта / Контрагент</div>
		<span class="podpis">Название проекта / Контрагент</span><br>
		<span style="white-space:nowrap;">
			<input type="text" class="text" name="projectName" id="projectName" value=""  style="width:130%;"
				   <%if (renewProcess) {%>
					readonly
					<%} else {%>
				   onfocus="$('#selectedName').attr('value', 'не выбрано');$('#CRMcontractorID').attr('value', '');$('#kz').attr('value', '');$('#place_2_div').hide();$('#selectkzfields').hide();"
					<%}%>
		    />
		</span>
	</div>
	<div class="podpis">
	    <div id="main_org_changeble_div" <%if(!changeMainOrgEnable){%>style="display: none" <%}%>>
		<span class="podpis">Контрагент может быть изменен</span><br>
		<span style="white-space:nowrap;">
			<select style="width:25%" name="main_org_changeble" id="main_org_changeble">
			    <option value="y">Да</option>
			    <option value="n">Нет</option>
			</select>
		</span>
			<span style="white-space:nowrap;display: none;" class="error" id="main_org_changeble_msg">Выбранный заемщик не входит ни в одну Группу Компаний, поэтому после создания заявки заемщик не может быть изменен.</span>
		</div>
	</div>

    <%if (!renewProcess) {%>
    	<div class="podpis">
    		<span class="podpis">Сумма</span><br>
    		<span style="white-space:nowrap;">
    			<md:inputMoney  name="Сумма лимита0" styleClass="money" value="" readonly="false" style="width:25%;text-align: left;"
    			    onBlur="input_autochange(this,'money')"  onChange="$('#empty_sum_msg').hide()" />
    			<select style="width:32%" name="Валюта0">
    				<%logger.info("=== createApplication 2.2,2.3 "+(System.currentTimeMillis()-tstart));
    					for(String currency : TaskHelper.dict().findCurrencyList()){
    				%>
    				<option><%=currency %></option>
    				<%}
    				 %>
    			</select> <span style="white-space:nowrap;display: none;" class="error" id="empty_sum_msg">Заполните поле Сумма</span>
    		</span>
    	</div>
    	<div class="podpis">
    		<span class="podpis">Срок</span><br>
    		<span style="white-space:nowrap;">
    			<md:inputInt name="Срок действия лимита0" styleClass="money" style="width:25%;text-align: left;"
    			onBlur="input_autochange(this,'digitsSpaces')"  onChange="$('#empty_period_msg').hide()"
    			readonly="" value="" />
    			<select style="width:32%" name="periodDimension" onchange="$('#empty_period_msg').hide()">
    			<%for(String periodDimension : TaskHelper.dict().getPeriodDimension()){ %>
    			    <option><%=periodDimension %></option>
    			<%} %>
    			</select> <span style="white-space:nowrap;display: none;" class="error" id="empty_period_msg">Заполните поле Срок</span>
    		</span>
    	</div>
    <%}%>
	<div class="podpis">
		<span class="podpis">Инициирующее подразделение</span><br>
		<span style="white-space:nowrap;">
				<select style="width:130%" name="Инициирующее подразделение0">
				<%logger.info("=== createApplication 2.4 "+(System.currentTimeMillis()-tstart));
				logger.info("=== createApplication 2.5 "+(System.currentTimeMillis()-tstart));

                selectedDep = renewProcess && initDep != null ? initDep : operatorDepartment;
				for (DepartmentJPA d : TaskHelper.dict().getInitialDepList()) {
				%>
					<option value='<%=d.getIdDepartment()%>' <%=(selectedDep.getShortName().equalsIgnoreCase(d.getShortName()))?"selected":""%>><%=d.getShortName()%></option>
				<%
				}
				%>
				</select>
		</span>
	</div>
	<div class="podpis">
		<span class="podpis">Место проведения сделки</span><br>
		<span style="white-space:nowrap;">
				<select name="Место проведения сделки0" id="place" onchange="updateKz();" style="width:130%">
				<%
				logger.info("=== createApplication 2.6 "+(System.currentTimeMillis()-tstart));
				logger.info("=== createApplication 2.7 "+(System.currentTimeMillis()-tstart));

                selectedDep = renewProcess && placeDep != null ? placeDep : operatorDepartment;
				for (DepartmentJPA d : TaskHelper.dict().getExecDepList()) {
				%>
					<option value='<%=d.getIdDepartment()%>' <%=(selectedDep.getShortName().equalsIgnoreCase(d.getShortName()))?"selected":""%>><%=d.getShortName()%></option>
				<%
				}
				%>
				</select>
		</span>
	</div>
	<div id="dialogProjectTeam" title="Вы не в проектной команде" style="display: none;">
		<p class="error">Вы не являетесь участником проектной команды по заявке. Для включения в проектную команду по заявке обратитесь к Структуратору.</p>
		<div id="dialogProjectTeamContent"></div>
	</div>
	<%logger.info("=== createApplication 4 "+(System.currentTimeMillis()-tstart)); %>
	<br><br>
	<button type="button" onclick="javascript:check(this.form);" id="btnCreate"><%=renewProcess ? "Далее" : "Создать"%></button>
</div>
<input type="hidden" name="Заявка №0" value="%AutoInc%"/>
<input type="hidden" id="SPOcontractorID" name="IDSPO_Contractors0" value=""/>
<input type="hidden" id="sponame" name="SPO_Contractors0" value=''/>
<input type="hidden" id="crmname" name="CRM_Contractors0" value=''/>
<input type="hidden" name="Тип Contractors0" value='Заемщик'/>

<input type="hidden" name="countProcesses"  value="1"/>
<input type="hidden" name="idUser" value="<%=pupFacade.getCurrentUser().getIdStr()%>"/>
	<input type="hidden" name="check0" value="0"/>

    <%if (renewProcess) {%>
        <input type="hidden" name="mdTaskId" value="<%=mdTaskId%>"/>
    <%}%>
	<div id="matchDiv">
		<h1>Совпадения</h1>
		<div id="matchDivContent">список</div>
	</div>
</form>
<script src="scripts/dist/CreateApplication.js"></script>
<script language="javascript">
function drawMatch(){
	if ($('#limitSelect').val() != '' && $('#kz').val()!=''
		&& ($('#place2').size() == 0 || $('#place2').val()!='')){
        $('#matchDiv').show();
        window.drawMatchDiv();
	} else {
        $('#matchDiv').hide();
	}
}
function openProjectTeamDialog(idmdtask) {
    $('#dialogProjectTeamContent').load('ajax/project_team_page.html?mdtask='+idmdtask,
        function() {$('#dialogProjectTeam').dialog({draggable: false,width: 800,modal: true});});
}
function selectInLimit(){
	var bp = $('#nameTypeProcess option:selected').val();
    if ($('#CRMcontractorID').val()=="" && bp != 'Pipeline'){
        alert('необходимо выбрать контрагента');
        return;
    }
    openDialog("popup_inlimit.jsp?formName=variables&fieldNames=inlimitID|inLimitName&org="+$('#CRMcontractorID').val(), "List", "<%= dialogProperty%>");
}
function onTypeChange(){
    $('#limitSelect_msg').hide();
	var bp = $('#nameTypeProcess option:selected').val();
	var limitSelectVal = $('#limitSelect option:selected').val();
    if(limitSelectVal=="Сделка"){
        $('#inLimit').show();
    } else {
        $('#inLimit').hide();
    }
	//если в поле Тип заявки выбраны кросс-селл, вейвер - в поле Тип процесса должен быть только бп Pipeline
    if (limitSelectVal=="Кросс-селл") {
        $('#nameTypeProcess').val('Pipeline');
        $('#nameTypeProcess').prop( "disabled", true);
    } else {
        $('#nameTypeProcess').prop( "disabled", false);
    }
	/*
	 если пользователь при пустом поле Тип заявки заполнил поле Тип процесса
	 бп КГО/КГОС за МО – в поле Тип заявки в списке типов заявки не показывать кросс-сел, вейвер
	 бп Pipeline – в поле Тип заявки в списке типов заявок показывать сделка, лимит, кросс-селл, вейвер
	 */
    if(bp == 'Pipeline' || bp==''){
        if ($('#limitSelect option[value="Кросс-селл"]').size()==0)
            $('#limitSelect').append($("<option></option>").attr("value","Кросс-селл").text("Кросс-селл"));
    } else {
        $('#limitSelect option[value="Кросс-селл"]').remove();
    }

    drawMatch();
}
window.document.body.onunload = onUnloadCheck;
dialogArray = new Array();  //дочерние окна
function onUnloadCheck()    {
    //закрываем дочерние окна:
    if(dialogArray != null){
        for(var i = 0; i < dialogArray.length; i++){
            if(dialogArray[i]) {
                try {
                    dialogArray[i].close();
                } catch (err) {
                }
            }
        }
    }
}
function openDialog(hrefStr, name, prop){
    var wnd = window.open(hrefStr, name, prop);
    dialogArray[dialogArray.length]=wnd;
    wnd.focus();
    return false;
}
function AddLink() {
    drawMatch();
	$('#empty_CRMcontractor').hide();
	$('#projectName').attr('value', "");
    variables.sponame.value="";
    variables.crmname.value="";
    variables.crmname.value=variables.selectedName.value;
    if (variables.CRMcontractorID.value=="")
        variables.CRMcontractorID.value="spo:"+variables.SPOcontractorID.value;
    $.post('ajax/orgname.html',{id: $('#CRMcontractorID').val()},
					function(data){$('#selectedName').val(data);});
    //проверить группу компаний
    $.post('ajax/org_group_name.html',{idorg: $('#CRMcontractorID').val()},
					function(data){if(data==''){
						$('#main_org_changeble_msg').show();
						$('#main_org_changeble_div').hide();
						$('#main_org_changeble').val('n');
					}else{
						$('#main_org_changeble_msg').hide();
						<%if(changeMainOrgEnable){%>$('#main_org_changeble_div').show();<%}%>
					};});
    drawMatch();
}

function check(form) {
	var valid = true;

	var bp = $('#nameTypeProcess option:selected').val();
	if(bp == ''){
		$('#nameTypeProcess_msg').show();
		valid = false;
	}
	if($('#limitSelect option:selected').val()=="Сделка" && $('#issuingBank option:selected').val()==''){
		$('#issuingBank_msg').show();
		valid = false;
	}
	if(bp != 'Pipeline' && bp != '' && $('#issuingBank option:selected').val() != '' && $('#issuingBank option:selected').attr('runprocess')=='false') {
		$('#issuingBank_runProcess_msg').show();
		valid = false;
	} else {
		$('#issuingBank_runProcess_msg').hide();
	}

    if ($('input[name="Срок действия лимита0"]').val() == '' || $('select[name="periodDimension"]').val() == '') {
		$('#empty_period_msg').show();
		valid = false;
    }
    if ($('input[name="Сумма лимита0"]').val() == '') {
		$('#empty_sum_msg').show();
		valid = false;
    }

    if (bp == 'Pipeline' && $('input[name="projectName"]').val() == '' && $('#CRMcontractorID').val()=='') {
        $('#empty_project_name').show();
        valid = false;
    }
	if(valid && bp != 'Pipeline') {
		if($('#CRMcontractorID').val()==''){
			$('#empty_CRMcontractor').show();
			valid = false;
		}
	}
	if($('#limitSelect').val() === ''){
		$('#limitSelect_msg').show();
		valid = false;
	}

	if(!valid) {return false;}
	$("body").css("cursor", "progress");
	$('#btnCreate').prop( "disabled", true );
    form.submit();
}

function fieldChanged() {}
function nameTypeProcessChange(){
	var bp = $('#nameTypeProcess option:selected').val();
	if(bp == 'Pipeline' || bp == ''){
		$('#projectName').prop( "disabled", false );
	} else {
		$('#projectName').attr('value', "");
		$('#projectName').prop( "disabled", true );
	}
	onTypeChange();
	$('#nameTypeProcess_msg').hide();
}
nameTypeProcessChange();
<%if(!filialmode){ %>
onTypeChange();
<%}%>
$('#ieDiv').hide();
</script>


<%logger.info("=== createApplication 5 "+(System.currentTimeMillis()-tstart)); %>
<jsp:include flush="true" page="footer.jsp" />
</body>
</html>
