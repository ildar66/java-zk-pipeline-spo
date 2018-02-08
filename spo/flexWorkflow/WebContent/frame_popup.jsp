<!DOCTYPE HTML PUBLIC "-//W3C//DTD HTML 4.01 Transitional//EN" "http://www.w3.org/TR/html4/loose.dtd">
<%@ page contentType="text/html; charset=utf-8"%>
<%@page isELIgnored="true" %>
<%@page import="ru.md.spo.ejb.PupFacadeLocal" %>
<%@page import="ru.md.spo.ejb.TaskFacadeLocal" %>
<%@page import="ru.masterdm.compendium.domain.crm.TargetType" %>
<%@page import="ru.md.domain.ExtraChargeRate" %>
<%@page import="ru.md.domain.OtherGoal" %>
<%@page import="ru.md.pup.dbobjects.DocumentGroupJPA"%>
<%@page import="ru.md.helper.TaskHelper" %>
<%@taglib uri="/WEB-INF/pup-tag.tld" prefix="pup"%>
<%@page import="ru.masterdm.compendium.model.CompendiumCrmActionProcessor"%>
<%@ page import="org.slf4j.Logger" %>
<%@ page import="org.slf4j.LoggerFactory" %>
<%@page import="ru.md.dict.dbobjects.SupplyTypeJPA" %>
<%@page import="java.util.List"%>
<%@ page import="ru.masterdm.spo.utils.Formatter" %>
<%@taglib uri="/WEB-INF/md-tag.tld" prefix="md"%>

<%
response.addHeader("Pragma", "no-cache");
response.addHeader("Expires", "-1");
response.addHeader("Cache-control", "no-cache");

long tstart = System.currentTimeMillis();
TaskFacadeLocal taskFacadeLocal = com.vtb.util.EjbLocator.getInstance().getReference(TaskFacadeLocal.class);
PupFacadeLocal pupFacade = com.vtb.util.EjbLocator.getInstance().getReference(PupFacadeLocal.class);
Long mdtaskid=TaskHelper.getIdMdTask(request);
ru.md.spo.dbobjects.TaskJPA taskJPA = taskFacadeLocal.getTask(mdtaskid);
CompendiumCrmActionProcessor compenduimCRM = (CompendiumCrmActionProcessor) ru.masterdm.compendium.model.ActionProcessorFactory.getActionProcessor("CompendiumCrm");
TargetType[] targetTypeList = compenduimCRM.findTargetTypes("%","c.description");

List<ExtraChargeRate> extraChargeRates = ru.masterdm.spo.utils.SBeanLocator.singleton().getCompendiumMapper().getExtraChargeRates();
List<OtherGoal> otherGoals = ru.masterdm.spo.utils.SBeanLocator.singleton().mdTaskMapper().getOtherGoals(mdtaskid);
%>
<div id="editDocumentForm" title="Изменить документ" style="display: none;">
<input id="attach_unid" value="" type="hidden">
К заявке <%=taskJPA.getNumberDisplay() %>
<table class="regular" style="width: 700px">
<tr><td>Заголовок</td><td><input id="attach_title" value="" size="80"></td></tr>
<tr><td>Срок действия</td><td><input id="attach_period" value="" onFocus="displayCalendarWrapper('attach_period', '', false); return false;"></td></tr>
<tr><td>Группа документа</td><td>
<input id="attach_docGroup" type="hidden"><a class="dialogActivator" href="javascript:;" dialogId="attach_docGroup_popup">
<span id="attach_docGroup_name">Выбрать</span></a>
</td></tr>
<tr><td>Тип документа</td><td>
<input id="attach_doctype" type="hidden"><a class="dialogActivator" href="javascript:;" dialogId="attach_doctype_popup">
<span id="attach_doctype_name">Выбрать</span></a>
</td></tr>
</table>
<br /><a href="javascript:;" onclick="$.post('ajax/editAttach.html',{doctype:$('#attach_doctype').val(),group: $('#attach_docGroup').val(),unid: $('#attach_unid').val(), title:$('#attach_title').val(),exp:$('#attach_period').val()},refreshDocFrame);$('#editDocumentForm').dialog('close');">Изменить</a>
<a href="javascript:;" onclick="$('#editDocumentForm').dialog('close');">Отмена</a>

<div id="attach_docGroup_popup" title="Группа документа" style="display: none;"><ul>
<% for (DocumentGroupJPA docGroup : pupFacade.findDocumentGroupByOwnerTYpe(0L)) {
if(!docGroup.isActive()){continue;}%>
<li><a href="javascript:;" onclick="$('.attach_doctype').hide();$('#attach_doctype<%=docGroup.getId() %>').show();$('#attach_docGroup').val('<%=docGroup.getId()%>');$('#attach_docGroup_name').html('<%=docGroup.getNAME_DOCUMENT_GROUP()%>');$('#attach_doctype').val('');$('#attach_doctype_name').html('Выбрать');">
<%=docGroup.getNAME_DOCUMENT_GROUP() %></a></li>
<%} %></ul></div>

<div id="attach_doctype_popup" title="Тип документа" style="display: none;">
<% for (DocumentGroupJPA docGroup : pupFacade.findDocumentGroupByOwnerTYpe(0L)) {%>
<div id="attach_doctype<%=docGroup.getId()%>" class="attach_doctype"><ul>
<%for(ru.md.pup.dbobjects.DocumentTypeJPA type : docGroup.getTypes()){
if(!type.isActive() || !pupFacade.isDocumentGroupTypeActive(docGroup.getId(), type.getId())){continue;}%>
<li><a href="javascript:;" onclick="$('#attach_doctype').val('<%=type.getId()%>');$('#attach_doctype_name').html('<%=type.getName()%>');">
<%=type.getName() %></a></li>
<%} %>
</ul></div>
<%} %>
</div>

</div>


<div id="changeSPForm" title="Изменение нормативного срока" style="display: none;">
<div id="changeSPFormValueId">Критерии не загружены</div>
<br />
Ручной ввод срока: <input id="days" value=""> рабочих дней
<br />
Комментарий: <br />
<textarea rows="5" id="cmnt" onkeyup="checkemptyStPerCmnt()"></textarea>
<br /><a href="javascript:;" onclick="StPerChOnClick()" id="chStPerLink">изменить</a>
<a href="javascript:;" onclick="$('#changeSPForm').dialog('close');">Отмена</a>
<div id="emptyStPerCmnt" class="error">Необходимо заполнить поле комментарий</div>
<input id="grid" value="unknown" type="hidden">
</div>

<script id="newTargetTemplate" type="text/x-jquery-tmpl">
<tr><td>
<textarea name="main Иные цели" id="target${id}"></textarea>
<a href="javascript:;" class="dialogActivator" dialogId="select_target_type" onclick="$('#target_type_id').val('target${id}'); dialogHandler();">
<img alt="выбрать из шаблона" src="style/dots.png"></a>
<input type="hidden" name="main Иные цели id" value="" id="target${id}id">
<input type="hidden" name="main Иные цели условие" value="" id="target${id}cond">
</td><td class="delchk"><input type="checkbox" name="main_otherGoalsIdChk"/></td></tr>
</script>

<script id="newTargetGroupLimitType" type="text/x-jquery-tmpl">
<tr>
	<td>
		<textarea
			id="targetGroupLimitType${id}"
			name="targetGroupLimitType_${limitGuid}_name"
			style="width:98%;" 
			class="newTargetGroupLimitType expand50-200"
			readonly="readonly"
			onkeyup="fieldChanged(this);" />
		<a href="javascript:;" 
			class="dialogActivator" 
			dialogId="select_target_group_limit_type"
			onclick="$('#targetGroupLimitTypeChoose').val('targetGroupLimitType${id}');"><img alt="выбрать из списка целевых назначений" src="style/dots.png"></a>
		<input type="hidden" 
			name="targetGroupLimitType_${limitGuid}_id" 
			value="" />
		<input type="hidden" 
			id="targetGroupLimitType${id}_id_target" 
			name="targetGroupLimitType_${limitGuid}_id_target" 
			value="" />									                    
	</td>
	<td class="delchk">
		<input type="checkbox" name="targetGroupLimitTypeChk"/>
	</td>
</tr>
</script>

<script id="newTargetTypeControl" type="text/x-jquery-tmpl">
	<tr>
		<td>
			<table id="target_type_control_type${id}" class="target_type_control" style="width: 100%;">
				<tbody>
				</tbody>
				<tfoot>
					<tr>
						<td colspan="2" class="add">
							<button onclick='var limitTypeNextId=getNextId(); $( "#newTargetGroupLimitType" ).tmpl({id:limitTypeNextId, limitGuid:"${id}"}).appendTo( "#target_type_control_type${id}  > TBODY" ); dialogHandler(); $("#targetGroupLimitType"+limitTypeNextId).TextAreaExpander(); return false;' class="add"></button>
							<button onclick="DelRowWithLast('target_type_control_type${id}', 'targetGroupLimitTypeChk'); return false;" class="del"></button>
						</td>
					</tr>
				</tfoot>
			</table>
		</td>
		<td>
			<span>
				<md:input id="targetGroupLimitAmount" 
					name="targetGroupLimitAmount"
					styleClass="money"
					value=""
					readonly="false"
					onBlur="input_autochange(this,'money'); " /><br />

					<md:currencyParent readonly="false" 
						id="targetGroupLimitAmountCurrency" 
						value=""
						name="targetGroupLimitAmountCurrency"
						parentTask="<%=mdtaskid%>" 
						withoutprocent="true"
						with_empty_field="true" />
				<input type="hidden" 
					name="targetGroupLimitGuid" 
					value="targetGroupLimitType_${id}" />
			</span>							
		</td>
		<td>
			<textarea 
				id="targetGroupLimitNote${id}"
				name="targetGroupLimitNote"
				style="width:100%;" 
				class="expand50-200"
				onkeyup="fieldChanged(this);" ></textarea>
		</td>
		<td class="delchk">
			<input type="checkbox" name="targetGroupLimitChk"/>
		</td>
	</tr>
</script>

<div id="select_target_type" title="выбрать целевое назначение" style="display: none;">
<ul>
<%for(TargetType tt : targetTypeList){ %>
    <li><a href="javascript:;" onclick="$('#'+$('#target_type_id').val()).val('<%=tt.getName() %>');$('#'+$('#target_type_id').val()+'cond').val('<%=tt.getId() %>');"><%=tt.getName() %></a></li>
<%} %>
</ul>
</div>


<script id="newIllegalTargetTemplate" type="text/x-jquery-tmpl">
<tr><td>
<textarea class="expand50-200" name="main Forbiddens" id="illegal_target${id}"></textarea>
<a href="javascript:;" class="dialogActivator" dialogId="select_illegal_target_type" onclick="$('#illegal_target_type_id').val('illegal_target${id}'); dialogHandler();">
<img alt="выбрать из шаблона" src="style/dots.png"></a>
</td><td class="delchk"><input type="checkbox" name="main_forbiddensIdChk"/></td></tr>
</script>
<div id="select_illegal_target_type" title="выбрать запрещенную цель кредитования" style="display: none;">
<ul>
<%for(String s : TaskHelper.dict().getIllegalLendingTargets()){ %>
    <li><a href="javascript:;" onclick="$('#'+$('#illegal_target_type_id').val()).val('<%=Formatter.strWeb(s) %>');"><%=s %></a></li>
<%} %>
</ul>
</div>

<div id="select_rate2_note" title="выбрать надбавку к процентной ставке" style="display: none;">
<ul>
<% for(ExtraChargeRate extraChargeRate : extraChargeRates) { %>
    <li><a href="javascript:;" onclick="$('#'+$('#rate2NoteDic').val()).val('<%=extraChargeRate.getName() %>');"><%=extraChargeRate.getName() %></a></li>
<%} %>
</ul>
</div>

<div id="select_target_group_limit_type" title="выбрать целевое назначение" style="display: none;">
<ul>
<% for(OtherGoal otherGoal : otherGoals) { %>
    <li><a href="javascript:;" onclick="$('#'+$('#targetGroupLimitTypeChoose').val()).val('<%=otherGoal.getGoal() %>').removeAttr('readonly'); $('#'+$('#targetGroupLimitTypeChoose').val() + '_id_target').val('<%=otherGoal.getIdTarget() %>');"><%=otherGoal.getGoal() %></a></li>
<%} %>
</ul>
</div>

<div id="select_pay_int" title="выбрать порядок уплаты процентов" style="display: none">
<ul>
<%for(String s : TaskHelper.dict().getPayInt()){ %>
    <li><a href="javascript:;"  onclick="$('#pay_int').val('<%=s %>');fieldChanged();"><%=s %></a></li>
<%} %>
</ul>
</div>

<div id="select_com_base" title="выбрать порядок уплаты процентов" style="display: none">
<ul>
<%for(String s : TaskHelper.dict().getComBase()){ %>
    <li><a href="javascript:;"  onclick="$('#' + document.getElementById('select_com_base_target_id').value).val('<%=s %>');"><%=s %></a></li>
<%} %>
</ul>
</div>

<div id="supplySelection" title="Выбрать группу обеспечения" style="display: none;">
    <ul>
        <li>
            <a class="disable-decoration" onClick="document.getElementById(document.getElementById('supplyid').value).value='-1';document.getElementById('sp'+document.getElementById('supplyid').value).innerHTML=document.getElementById('spansupply0').innerHTML;">
                <span style="cursor: pointer;" id="spansupply0">не выбрана</span>
            </a>
        </li>
        
        <%List<SupplyTypeJPA> allst = ru.md.helper.TaskHelper.dict().findSupplyType();
        for(int i=0;i<allst.size();i++){
            SupplyTypeJPA supplyType=(SupplyTypeJPA)allst.get(i);%>
            <li>
                <a class="disable-decoration" onClick="document.getElementById(document.getElementById('supplyid').value).value='<%=supplyType.getId().toString() %>';document.getElementById('sp'+document.getElementById('supplyid').value).innerHTML=document.getElementById('spansupplyvalue<%=supplyType.getId().toString() %>').innerHTML;">
                    <span style="cursor: pointer;" id="spansupplyvalue<%=supplyType.getId().toString() %>"><%=supplyType.getName() %></span>
                </a>
            </li>
        <%} %>
    </ul>
</div>
<script id="newPunitiveMeasureTemplate" type="text/x-jquery-tmpl">
<tr>
<td>
    <textarea id="PunitiveMeasure${nextid}" name="Штрафные санкции${guid}" style="width:98%;" onkeyup="fieldChanged(this);" ></textarea>
    <a href="javascript:;" class="dialogActivator" dialogId="${template_name}" onclick="punitiveMeasureSelectTemplate('PunitiveMeasure${nextid}','${template_name}');">
       <img alt="выбрать из шаблона" src="style/dots.png"></a>
</td>
<td>
    <textarea id="DescPunitiveMeasure${nextid}" name="fine_value_text${guid}"></textarea>
    <input id="dPunitiveMeasure${nextid}" name="descPunitiveMeasure${guid}" type="hidden">
    <md:inputMoney name="fine_value${guid}" styleClass="text money PunitiveMeasure${nextid}" value="0" readonly="false" id="valPunitiveMeasure${nextid}"/>
    <md:currency readonly="false" value="" name="fine_currency${guid}" withoutprocent="false" with365="true"  styleClass="PunitiveMeasure${nextid}" with_empty_field="true" id="curPunitiveMeasure${nextid}" />
    <input type="hidden" name="fine_id_punitive_measure${guid}" value="" id="idDictPunitiveMeasure${nextid}">
</td>
<td>
           <md:inputInt name="fine_period${guid}" readonly="false" value="10"/>
           <select name="fine_periodtype${guid}">
               <option value=""></option>
               <option value="workdays">рабочих дней</option>
               <option value="alldays" selected>календарных дней</option>
           </select>
</td>
<td><input type="checkbox" onclick="if(this.checked){$(this).parent().find('input[name=fine_productrate${guid}]').val('y');}else{$(this).parent().find('input[name=fine_productrate${guid}]').val('n');}" />
<input name="fine_productrate${guid}" value="n"  type="hidden">
</td>
<td class="delchk"><input type="checkbox" name="testShtrafiRowChk${guid}"/></td>
</tr>
</script>

<%Logger LOGGER = LoggerFactory.getLogger(this.getClass().getName());
    Long loadTime = System.currentTimeMillis()-tstart;
    LOGGER.warn("*** frame_popup.jsp time "+loadTime);
%>
