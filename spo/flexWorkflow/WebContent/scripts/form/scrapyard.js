//вызываем при загрузке страницы
$(document).ready(function() {
	$('#section_menu').show();
	$("#show_hide_task_list_btn").show();
	try{
		unfade();
		//страница загружена. Если есть кнопка передачи в кк, то откроем секцию
		if (document.getElementById('btnCC')!=null)
			$('#section_conclusion > thead').click();
	} catch (Err) {
		//console.error(Err.message);
	}
	try{
		$('#current_role_dialog').load('roleslist.jsp?login='+$('#login').val()+'&id='+$('#userid').val());
	} catch (e) {
		//ignore
	}
	try{
		$('#statistic').jqm({ajax: 'statistic.html', trigger: 'a.statistic'});
	} catch (e) {
		//ignore
	}
	//можно сделать еще что-то
	updateOrg();
	with_sublimitOnClick();
	trance_graphOnClick();
	try{
		$('#goToDiv').jqm();
	} catch (e) {
		//ignore
	}
    
    try {
	   restoreSection();
	} catch (e) {
	   // ignore
	}
	// ну да. Раскрасим неверные поля родительского контроля (если родительский лимит изменился, например)  
	checkParentAllowedFilled();
	changed=false;
});
function trance_graphOnClick(){
	try{
		if (document.getElementById('trance_graph')!=null)
			if($('#trance_graph').is(':checked')){
				$('.trancediv').show();
				$('.newtrance').show();
				$('.withouttrancediv').hide();
			} else {
				$('.trancediv').hide();
				$('.newtrance').hide();
				$('.withouttrancediv').show();
			}
	} catch (Err) {
		//console.error(Err.message);
	}
}
function onChangeLimitType(){
	fieldChanged();
	$('.allProductType').hide();
	$('.productTypePeriod').hide();
	var selectedLimitTypeID = "";
	if($("#limitType :selected").size()>0){
		selectedLimitTypeID = $("#limitType :selected").val();
	} else {
		selectedLimitTypeID = $("#limitTypeHidden").val();
	}
	if(selectedLimitTypeID=="-1"){
		$('.allProductType').show();//показываем все виды продукта
	}
	if(document.getElementById('with_sublimit')!=null && $('#with_sublimit').is(':checked')){
		$('.section_product_type').show();
		$('.allProductType').show();//показываем все виды продукта
		$('.allProductType > input:checked').each(function(){
			$('.productTypePeriod'+this.value).show();
		});
	} else {
		$('.productType4lt'+selectedLimitTypeID).show();//показываем только нужные виды продукта
		$('.productType4lt'+selectedLimitTypeID+' > input:checked').each(function(){
		    $('.productTypePeriod'+this.value).show();
		});
	}
}
function with_sublimitOnClick(){
	try{
		if (document.getElementById('with_sublimit')!=null)
			if($('#with_sublimit').is(':checked')){
				$('#limitType').hide();
				$('#section_inLimit').show();
				$('#section_supply').show();
				$('#section_priceConditionLimit').show();
			} else {
				$('#limitType').show();
				$('#section_inLimit').hide();
				$('#section_supply').show();
				$('#section_priceConditionLimit').show();
			}
		onChangeLimitType();
	} catch (Err) {
		//console.error(Err.message);
	}
}
//Это для календарика
function popCalInFrame(dateCtrl) {
                    fieldChanged();//поле поменялось
                    var w=gfPop;
                    //w.gbFixedPos=true;    // enable fixed positioning
                    //w.gPosOffset=[70,0];  // set position
                    w.fPopCalendar(dateCtrl);   // pop calendar
}
function myCustomOnChangeHandler(inst) {
    alert("Some one modified something");
    alert("The HTML is now:" + inst.getBody().innerHTML);
}
var loadNumber=0;
var filesToSend = -1;
//хак для IE6
//if (!window.console) console = {info: function() {},warn: function() {},error: function() {}};
function openHref(href) {
	window.open(href,"","");
}
function setCheckBoxValue(chk, name) {
	try
	{
		if (chk.checked) {
			document.getElementsByName(name)[0].value='TRUE'
		} else {
			document.getElementsByName(name)[0].value='FALSE'
		} 
	}
	catch (rr)
	{
		alert('error')
	}
}
function DeleteFile(tid,Row)
{
	var script = "DeleteFile"
	try
	{
		var tbl=getNode(null,tid)
		if (!tbl){
			return
		}
		var chkbox
		var Rows = tbl.tBodies[0].rows
		if (!Rows){
			return
		}
		for (var j = Rows.length-1; j>=0;j--)
		{
			chkbox = getNode(Rows[j],Row)
			if (chkbox.checked)
			{
			tbl.tBodies[0].removeChild(Rows[j])
			}
		}
	}
	catch (Err)
	{
		alert(Err.description)
		return false
	}
}
function AddAttFile(tableName) {
	var script = "DeleteFile"
	try
	{
	  var tbl=getNode(null,tableName)
		if (!tbl){
			return
		}
		var Rows = tbl.tBodies[0].rows
		var child = Rows[Rows.length-1]
		var myTR = child.cloneNode(true)
	  tbl.tBodies[0].appendChild(myTR)
	}
	catch (Err)
	{
		alert(Err.description)
		return false
	}
}
function deleteRow(tid,Row,name)
{
	try
	{
		var tbl=getNode(null,tid)
		if (!tbl){
			return
		}
		var chkbox
		var Rows = tbl.tBodies[0].rows
		if (!Rows){
			return
		}
		for (var i = Rows.length-1; i>=0;i--)
		{
			chkbox = getNode(Rows[i],Row)
			if (chkbox.checked)
			{
			tbl.tBodies[0].removeChild(Rows[i])
			}
		}
		if (Rows.length == 0) {
			if (tid == 'tblChief') {
				AddRow('tblChief','checkChief',name,'Chief')
			} else if (tid == 'tblHolders') {
				AddRow('tblHolders','checkHolder',name,'Holder')
			} else if (tid == 'tblPurposesOfCrediting') {
				AddRow('tblPurposesOfCrediting','checkPurposesOfCrediting',name,'PurposesOfCrediting')
			} 
		}																	
	}
	catch (Err)
	{
		alert(Err.description)
		return false
	}
}
function AddRow(tableId,checkId,name,colName) {
	fieldChanged();
	var tbl=getNode(null,tableId)
	if (!tbl){
		return
	}
	var myTR=document.createElement('tr')
	myTR.id="TR"+colName
	var myTD=document.createElement('td')
	myTD.id="TD"+colName
	var checkbox = document.createElement("<INPUT TYPE='checkbox' NAME='" + checkId + "' ID='" + checkId + "' style='font-size:8pt'>")
	myTD.appendChild(checkbox)
	var myTD1=document.createElement('td')
	
	if (tableId=='tblChief' || tableId=='tblHolders') {
		myTD1.align="center"
		var inp1 = document.createElement("<INPUT TYPE='text' NAME='" + name + "' ID='" + name + "'  style='width:99%;font-size:8pt'>")
		myTD1.appendChild(inp1)
	} else if (tableId=='tblPurposesOfCrediting') {
		var refPurposes = document.getElementById('idRefPurposesOfCrediting')
		var newRefPurposes = refPurposes.cloneNode(true)
		newRefPurposes.style.display="block"
		newRefPurposes.style.width="100%"
		myTD1.appendChild(newRefPurposes)

		myTD1.appendChild(document.createElement("<INPUT TYPE='text' NAME='" + name + "' ID='" + name + "' style='display:none;width:99%;font-size:8pt'>"))		
	}


	var myTD2=document.createElement('td')
	myTD2.align="center"
	
	if (tableId=='tblChief')
	{
		var chkRank = document.getElementById('rank')
		var newChkRank = chkRank.cloneNode(true)
		newChkRank.style.display="block"
		newChkRank.style.width="100%"
		myTD2.appendChild(newChkRank)

		myTD2.appendChild(document.createElement("<INPUT TYPE='text' NAME='" + name + "' ID='" + name + "' style='width:99%;font-size:8pt;display:none'>"))
	} else if (tableId=='tblHolders') {
		myTD2.appendChild(document.createElement("<INPUT TYPE='text' NAME='" + name + "' ID='" + name + "' style='width:99%;font-size:8pt'>"))
	}
	
	var myTD3=document.createElement('td')
	myTD3.align="center"
	myTD3.appendChild(document.createElement("<INPUT TYPE='text' NAME='" + name + "' ID='" + name + "' style='width:99%;font-size:8pt'>"))

	myTR.appendChild(myTD)
	myTR.appendChild(myTD1)

	if (tableId=='tblChief' || tableId=='tblHolders') {
		myTR.appendChild(myTD2)
		myTR.appendChild(myTD3)
	}

	// alert(myTR.innerHTML)
	
	tbl.tBodies[0].appendChild(myTR)
}

function DelRow(tableId, chkName, clearFunc) {
	fieldChanged();
	try
	{
	 	 var tbl = document.getElementById(tableId);
	     var body = tbl.getElementsByTagName("TBODY")[0];
		 if (!tbl){
			return
		 }	
		var chk = document.getElementsByName(chkName);
		var Rows = body.rows;
		for (i=chk.length-1; i>=0; i--)
			if (chk[i].checked)				
				if (chk.length>1) {
					body.removeChild(Rows[i]);
				} else{
					chk[i].checked = false;
				}
	}
	catch (Err)
	{
		alert(Err.description)
		return false
	}

}
function DelRowWithLast(tableId, chkName) {
	fieldChanged();
	$('#'+tableId+' > tbody > tr > td.delchk > input:checked').parent().parent().remove();
	try
	{
	 	 var tbl = document.getElementById(tableId);
	     var body = tbl.getElementsByTagName("TBODY")[0];
		 if (!tbl){
			return
		 }
		var chk = tbl.getElementsByName(chkName);
		var Rows = body.rows;
		for (i=chk.length-1; i>=0; i--) 
			if (chk[i].checked)				
				body.removeChild(Rows[i]);
	}
	catch (Err)
	{
		//alert(Err.description);
		return false;
	}
}
function validateProductSum(){
	var valid = true;
	var warningText = "";
	var sumCheckStages =["формирование проекта кредитного решения",
			"согласование проекта кредитного решения",
			"доработка проекта кредитного решения",
			"отправка проекта кредитного решения на рассмотрение уо/ул",
			"получение решения уо/ул и направление запросов на проведение экспертиз"];
	var sumCheckProcesses = ["Крупный бизнес ГО"];
	try {
		var isLimitIssueChk = document.getElementById("isLimitIssue").checked;
		var isDebtLimitChk = document.getElementById('isDebtLimit').checked;
		var limitSum = document.getElementById('limit_sum');
		var limitIssue = document.getElementById('limitIssue');
		var debtLimit = document.getElementById('debtLimit');
		var isProduct = document.getElementById('is_product').value;
		var processName = document.getElementById('process_name').value;
		var stageName = document.getElementById('stage_name').value;
		var parentSumElem = document.getElementById("parent_sum");
		// проверка сумм
		if (isProduct == 'y' && $.inArray(processName, sumCheckProcesses) != -1
				&& $.inArray(stageName, sumCheckStages) != -1 && parentSumElem) {
			var parentSum = parseNumber(parentSumElem.value);
			if (isLimitIssueChk) {
				if (parseNumber(limitIssue.value) > parentSum) {
					warningText = '"Сумма лимита выдачи" в секции "Основные параметры сделки" '
							+ 'не должна превышать сумму родительского лимита / сублимита.<br /><br />';
					valid = false;
				}
			}
			else if (isDebtLimitChk) {
				if (parseNumber(debtLimit.value) > parentSum) {
					warningText = '"Сумма лимита задолженности" в секции "Основные параметры сделки" '
							+ 'не должна превышать сумму родительского лимита / сублимита.<br /><br />';
					valid = false;
				}
			}
			else {
				if (parseNumber(limitSum.value) > parentSum) {
					warningText = '"Сумма сделки" в секции "Основные параметры сделки" '
							+ 'не должна превышать сумму родительского лимита / сублимита.<br /><br />';
					valid = false;
				}
			}
		}
	} catch (Err) {	}
	if (!valid)
		$('#sumCheckWarningText').html(warningText + " Все равно продолжить?");
	return valid;
}
function checkBeforeAccept() {
	if (!validateProductSum()) {
		$('#afterSumCheckAction').val('goAcceptLink');
		$('#sumCheckWarningPopup').dialog({draggable: false,width: 400});
	}
	else
		goAcceptLink();
	return false;
}
function disableSubmitButton(){
	$('#btnRegister').prop( "disabled", true );
	$('#b1save').prop( "disabled", true );
}
function enableSubmitButton(){
	$('#btnRegister').prop( "disabled", false );
	$('#b1save').prop( "disabled", false );
}
function submitData(isComplete) {
	disableSubmitButton();
	$('#completeAfterValidate').val(isComplete);
	if (!validateProductSum()) {
		$('#afterSumCheckAction').val('submitDataStep2');
		$('#sumCheckWarningPopup').dialog({draggable: false,width: 400});
		enableSubmitButton();
	}
	else
		submitDataStep2();
	return false;
}
function submitDataStep2() {
	if($('#pipelineReadonly').val()=='false'){
		try{
			savePipeline();
		} catch (Err) {}
		
		if($('#redirecturl').val()!='' && $('#formReadonly').val()=='true'){
			window.location.replace($('#redirecturl').val());
		}
	}
	//if(isComplete && $('#nazvanieOperacii').text()=='Дополнение заявки')
	//if(isComplete && $('#nazvanieOperacii').text()=='Дополнение информации по заявке')
		//alert('sfsf');
	if($('#formReadonly').val()=='false'){
		var isComplete = $('#completeAfterValidate').val()=='true'
		// поставим панель загрузки. Уберем кнопки.
		// сначала автозаполним некоторые поля
		computeOpportunityPeriodAndDate();
		document.getElementById('isWithComplete').value = isComplete;
		valid=true;
		if (validate_variablesOnSave())	{document.variables.submit();}
	}
	return false;
}
/**
 * called by the thread when all sections are loaded (COMPLETE the operation button pressed) 
 */
function submitDataCallback() {
	if (validate_variables()) {
		document.variables.submit();
		return false;
	}
	else {
		unfade();
		return false;	
	}
}
//Действие после проверки сумм
function continueAfterSumCheckWarning() {
	$('#sumCheckWarningPopup').dialog('close');
	if ($('#afterSumCheckAction').val() == 'submitDataStep2') 
		submitDataStep2();
	else if ($('#afterSumCheckAction').val() == 'goAcceptLink')
		goAcceptLink();
}

// Zoom для картинок, Написал Сергей Полевич. Работает только в
// интернет-эксплорерах.
function zoomer(img_id) {
	var img = document.getElementById(img_id);
	if (img.style.zoom=='50%') {
		img.style.zoom='100%'
	} else {
		img.style.zoom='50%'
	}
}


// Очистить форму взято с
// http://www.javascript-coder.com/javascript-form/javascript-clear-form-example.htm
function clearForm(oForm) {
	var elements = oForm.elements;
	oForm.reset();
	for(i=0; i<elements.length; i++) {
		field_type = elements[i].type.toLowerCase();
		switch(field_type) {
			case "text":
			case "password":
			case "textarea":
			case "hidden":

			elements[i].value = "";
			break;

			case "radio":
			case "checkbox":
			if (elements[i].checked) {
			elements[i].checked = false;
			}
			break;

			case "select-one":
			case "select-multi":
			elements[i].selectedIndex = -1;
			break;

			default:
			break;
		}
	}
}


function AddRowToStandardPriceConditionTable(){
	var nextid=getNextId();
	var tr = '<tr><td>'+
	'<a href="javascript:;" '+
	'onclick="$(\'#supplyid\').val(\''+nextid+'\');$(\'#StandardPriceConditionDiv\').jqmShow();"><span id="sp'+
	nextid+'">не выбрано</span><input type="hidden" id="'+nextid+'" name="Стандартные стоимостные условия" value="-1" readonly="false"> </a>'+
	'</td><td class="delchk" style="width:auto;">'+
	'<input type="checkbox" name="idStandardPriceConditionTableChk"/></td></tr>'
	$('#idStandardPriceConditionTable > tbody').append(tr)
}
function refuse_button(){
	$.fancybox.close();
	$('#refuse_date').appendTo('#variables');
	$('#StatusReturnText').appendTo('#variables');
	$('#StatusReturn').appendTo('#variables');
	$('#abody').appendTo('#variables');
	$('#refuseMode').val('true');
	buttonClick('Отказать');
}
function accept_button_click(){
	$.fancybox.close();
	$('#refuse_date').appendTo('#variables');
	$('#StatusReturnText').appendTo('#variables');
	$('#StatusReturn').appendTo('#variables');
	$('#abody').appendTo('#variables');
	$('#refuseMode').val('true');
	if($('#section_decision_accept_div').size()>0){
		$('#section_decision_accept_div').appendTo('#variables');
	}
	buttonClick('Одобрить');
}

/* sets corresponding fields of opportunity date, proposed date and period */
function computeOpportunityPeriodAndDate() {
	try {
		var proposedDateSigningAgreement = $('#proposedDateSigningAgreement').val();
		if (proposedDateSigningAgreement != "" && $('#mdtask_period').val() != "") {
			$.post('ajax/deltaDate.html',{delta: removeWhiteSpace($('#mdtask_period').val()), 
				from:proposedDateSigningAgreement, deltaDimension:$('#periodDimension').val()},
					function(data){
						$('#mdtask_date').val(data);
						$('#header_validto_span').text(data);
					});
		} else {
		  // планируемая дата не задана. Не можем вычислить поле Дата сделки или поле Срок сделки. 
 		  // Запретим задавать одновременно.  
			var mdtask_period = document.getElementById('mdtask_period');
		  if ((mdtask_period.value != null) && (mdtask_period.value.length != 0)) mdtask_date.value = "";  
		}
	} catch (Err) {} 
}
dialogArray = new Array();	// дочерние окна
function openDialog(hrefStr, name, prop){
	var wnd = window.open(hrefStr, name, prop);
	dialogArray[dialogArray.length]=wnd;
	wnd.focus();
	return false;
}
/**
 * Обновляет рейтинги и количество документов по организации.
 */
function updateOrg() {
	$('.org').each(function(index) {
		var id = $(this).attr('id');
		$.post('ajax/rating.do',{org: id},updateOrgRating);
		$.post('ajax/attachCount.do',{org: id},updateOrgAttachCount);
	});
}
function updateOrgAttachCount(xml) {
	var id = $('id:first',xml).text();
	$('#Attach'+id).html($('count:first',xml).text());
}
function updateOrgRating(xml) {
	var id = $('id:first',xml).text();
	$('#Region'+id).html($('region:first',xml).text());
	$('#Branch'+id).html($('branch:first',xml).text());
	$('#ClientCategory'+id).html($('ClientCategory:first',xml).text());
	$('rating',xml).each(function(index) {
		if($('value:first',this).text()!=""){
			var rating='<strong>'+$('value:first',this).text()+'</strong><br />'
				+ $('date:first',this).text();
			$('#Rating'+$('type:first',this).text()+id).html(rating);
		}
	});
}

function openMainBorrowerChangeReport(){
	window.open('mainBorrowerChangeReport.jsp?id='+$('#mdtaskid').val(), 'mainBorrowerChangeReport','scrollbars=yes, top=100, left=100, width=800, height=710');
}
/**
 * вызываем всплывающее окно выбора единого клиента.
 */
function addOrganisationStage1(){
	window.open('popup_org.jsp?formName=variables&ek=only&fieldNames=selectedID|selectedName|CRMID&onMySelect=addOrganisationStage2()&first=first', 'orgek','scrollbars=yes, top=100, left=100, width=800, height=710');
}
function addOrganisationStage2(){
	$.post('ajax/orgname.html',{id: $('#CRMID').val()}, addOrganisationStage4);
}
/**
 * выбор контрагента сделали, теперь добавляем его в таблицу.
 */
function addOrganisationStage4(data){
	fieldChanged();
	var param = {id:$('#CRMID').val(),orgname:data};
	$("#newOrgTemplate").tmpl(param).appendTo("#idTableContractor > TBODY");
	updateOrg();
}
function changeMainOrganisationStage1(){
	//var id=$('#idTableContractor > tbody > tr.main').attr('id');
	//window.open('popup_org.jsp?formName=variables&ek=only&fieldNames=selectedID|selectedName|CRMID&mainorg='+id+'&onMySelect=changeMainOrganisationStage2()', 'orgek','top=100, left=100, width=800, height=710');
	window.open('changeMainOrganisation.html?mdtaskid='+$('#mdtaskid').val(), 'orgek','scrollbars=yes, top=100, left=100, width=800, height=770');
}
function changeMainOrganisationStage2(){
	$.post('ajax/orgname.html',{id: $('#CRMID').val()}, changeMainOrganisationStage4);
}
function changeMainOrganisationStage4(data){
	fieldChanged();
	var param = {id:$('#CRMID').val(),orgname:data};
	$('#idTableContractor > tbody > tr.main').remove();
	$("#mainOrgTemplate").tmpl(param).prependTo("#idTableContractor > TBODY");
	updateOrg();
}
/**
 * Подписывает все ссылки на странице с классом fancy на обработчик клика
 * fancyBox.
 */
function fancyClassSubscribe(){
	$("a.fancy").fancybox({
		'zoomOpacity' : true,
		'zoomSpeedIn' : 500,
		'zoomSpeedOut' : 500,
		'hideOnContentClick': false,
		'frameWidth': 800, 
		'frameHeight': 600,
		'showCloseButton': true
	});
}
function DelTableRow(tableId, chkName) {
	fieldChanged();
	var child;
	var script = "DeleteFile";
	try
	{
		var tbl = document.getElementById(tableId);
		var body = tbl.getElementsByTagName("TBODY")[0];
		var chk = document.getElementsByName(chkName);
		var Rows = body.rows;
		for (i=chk.length-1; i>=0; i--)
			if (chk[i].checked){
				body.removeChild(Rows[i]);}
	}
	catch (Err)
	{
		alert(Err.description);
		return false;
	}
}
/**
 * Функция возвращает уникальный id.
 */
function getNextId(){
	generatedID=generatedID+1;
	return 'generatedID'+generatedID;
}
var generatedID=0;

/**
 * Событие - изменение поля
 */
function fieldChanged(htmltag) {
	changed=true;
//	$('#b1save').attr('disabled', '');
//	$('#b1save').removeClass('disabled');
	if(htmltag != null) {
		$(htmltag).addClass("edited");
	}
}
changed=false;//глобальная переменная. Признак, что поле изменилось
//$('#b1save').attr('disabled', 'disabled');
//$('#b1save').addClass('disabled');
function conditionSelectTemplate(id, condType){
	$('#otherTemplateHiddenInput').val('condition'+condType+'_'+id);
	$('#otherTemplateHiddenInputId').val('idCond_'+condType+'_'+id);
	return false;
}
function AddRowToOtherTable(condType) {
	try
	{
		fieldChanged();
		var nextid=getNextId();
   		var tr = '<tr><td><input type="hidden" id="idCond_'+condType+'_'+nextid+'" '+
   		    'name="idCond_'+condType+'" value=""><input type="hidden" name="condition'+condType+'id" value=""><textarea  rows="6" id="condition'+condType+'_'+nextid+'" name="condition'+condType+'" '+
			'onkeyup="fieldChanged(this)"></textarea>';
   		//if(condType=='1' || condType=='2' || condType=='3' || condType=='6' || condType=='9'){
   			tr+= '<a href="javascript:;" class="dialogActivator" dialogId="condition' + condType + 'SelectTemplateDiv" onclick="conditionSelectTemplate(\''+nextid+'\',\''+condType+'\');">'+
	   		'<img alt="выбрать из шаблона" src="style/dots.png"></a></td>';
   		//}
   		tr+='<td class="delchk"><input type="checkbox" name="condition'+condType+'TableChk"/></td></tr>';
	  	
	  	$('#condition'+condType+'Table > tbody').append(tr);
	  	dialogHandler();
	}
	catch (Err)
	{
		alert(Err.description);
		return false;
	}
}
function earlyPaymentSelectTemplate(id){
	$('#earlyPaymentTemplate').val(id);
	$('#earlyPaymentSelectTemplateDiv').jqmShow();
	return false;
}
function AddRowToTableEarlyPayment() {
	fieldChanged();
	var nextid=getNextId();
	var param = {id:nextid};
	$("#earlyPaymentTrTemplate").tmpl(param).appendTo("#idEarlyPayment > TBODY");
}
function operationDecisionSelectTemplate(id){ 
	$('#operationDecisionTemplate').val(id);
	$('#operationDecisionSelectTemplateDiv').jqmShow();
	return false;
}
var parent_class = "";
var tree_level = 0;
function addinLimitRow(xml) {
    var parent = $('parent:first',xml).text();
    $('task',xml).each(function(index) {
        var id = $('id:first',this).text();
        var current = "";
        if(id==currentSublimit){current=" markedlimit";}
        var indent = "";
        for (var i=0; i<tree_level; i++){indent += "&nbsp;&nbsp;&nbsp;";}
        var param = {id:id,type:$('type:first',this).text(),number:$('number:first',this).text(),indent:indent,lvl:tree_level,
            org:$('org:first',this).text(),sum:$('sum:first',this).text(),period:$('period:first',this).text(),
			trcl:parent_class,current:current,title:$('title:first',this).text()};
        var newtr = $("#newInLimitTemplate").tmpl(param);
        if ($('#tr'+parent).size()>0){
            $('#tr'+parent).after(newtr);
        } else {
            newtr.appendTo( "#inLimit > TBODY" );
        }
        var tmpl_name = "hasChildInLimitTemplate_" + $('hasChild:first',this).text();
        $("#td"+id).html($("#"+tmpl_name).tmpl(param));
        if(selectLimitMode && $('hasSublimits:first',this).text()=="false" && $('type:first',this).text()!="Сделка") {
        	$("#nbr"+id).html('<a href="javascript:;" onclick="selectLimit(\''+
        			id+'\',\''+$('type:first',this).text()+' '+$('number:first',this).text()
        			+'\');">'+$('number:first',this).text()+'</a>');
        }
        if(!selectLimitMode && $('#tasktype').val()=='Сделка' && sublimitEditMode){//проверяем что открыта на редактирование
       		//делаем кнопку сделка сублимита
        	if($('type:first',this).text()!='Сделка' && $('hasSublimits:first',this).text()=='false'){
        		if(inLimit==id){
        			$('#sublimitadd'+id).html('<input type="checkbox" checked disabled>');
        		} else {
        			$('#sublimitadd'+id).html('<input type="checkbox" onclick="inSubLimit('+id+');return false;">');
        		}
        		$('#inLimitCol6').html('Сделка сублимита');
        	}
        }
        if(!selectLimitMode && $('#tasktype').val()!='Сделка' && $('type:first',this).text()!='Сделка'){//делаем ссылку по номеру
        	if($('#idTask').val()!='0'){//ссылка на редактирование заявки
    			$('#nbr'+id).html('<a href="javascript:;" onclick=goTo("task.context.do?id='+$('#idTask').val()+'&mdtask='+id+'")>'+$('number:first',this).text()+'</a>');
        	} else {//ссылку по номеру на просмотр заявки
        		idListProcess='';
        		if($('#idListProcess').size()>0){
        			idListProcess='&idListProcess='+$('#idListProcess').val();
        		}
        		var other_param = '';
        		if(getParameterByName("ced_id")!=""){other_param+="&ced_id="+getParameterByName("ced_id");}
        		if(getParameterByName("ced_type")!=""){other_param+="&ced_type="+getParameterByName("ced_type");}
        		if(getParameterByName("ced_idBpmsEntity")!=""){other_param+="&ced_idBpmsEntity="+getParameterByName("ced_idBpmsEntity");}
    			$('#nbr'+id).html('<a href="form.jsp?mdtask='+id+idListProcess+other_param+'">'+$('number:first',this).text()+'</a>');
        	}
        	if($('#section_sublimit_edit').size()>0){//секция редактируемая
        		//кнопка удаления
        		if($('showDel:first',this).text()=='true'){
        			var onCl = "delSubLimit('"+id+"','"+$('number:first',this).text()+
        			"',"+$('hasChild:first',this).text()+");return false;";
        			$('#sublimitdel'+id).html('<button class="del" onclick="'+onCl+'"></button>');
        		}
        		//кнопка создания
        		$('#sublimitadd'+id).html('<button class="add" onclick="newSubLimit('+id+');return false;"></button>');
        	}
        }
      	// отображение отличий, если требуется
        try{
    		if ($('#lastApprovedVersion').val() != "" && prevApprovedDiffShown)
    			displayPrevApprovedDiff();
    	} catch (e) {
    		//ignore
    	}
	});
}
function goTo(url){
	//alert('goTo. changed='+changed);
	globalurl=url;
	if(changed){
		$('#goToDiv').jqmShow();
		return false;
    }
	location.href=url;
}
function inSubLimit(par){
	tree_level = 0;
	parent_class = "";
	if(confirm('изменить родительский сублимит?')){
		$.post('ajax/inSublimit.do',{id: $('#mdtaskid').val(), parentid: par},refreshSublimitFrame);
	}
}
//кнопка создания
function newSubLimit(parentid){
	sublimitparentid=parentid;
	var wnd = window.open('popup_org.jsp?formName=variables&ek=only&fieldNames=selectedID|selectedName|CRMID&onMySelect=newSubLimitKz()', 'org','top=100, left=100, width=800, height=710');
}
function newSubLimitKz(){
	var url = 'popup_org.jsp?formName=variables&ek='+$('#CRMID').val()+'&fieldNames=selectedID|selectedName|CRMID&onMySelect=newSubLimit2()';
	window.open(url, 'orgkz','top=100, left=100, width=800, height=710');
}
function newSubLimit2(){
	$.post('ajax/addSublimit.do',{id: sublimitparentid, org: variables['CRMID'].value},newSubLimit3);
}
function newSubLimit3(){
	if($('#img'+sublimitparentid).size()<1){//нет плюсика
		var img = '<img lvl="'+tree_level+'" alt="-" src="theme/img/collapse.jpg" onclick="toggleLimit('+sublimitparentid+')" id="img'+sublimitparentid+'">';
		var indent = "";
        for (var i=0; i<tree_level; i++){indent += "&nbsp;&nbsp;&nbsp;";}
		$('#td'+sublimitparentid).html(indent+img+' Сублимит');
	}
	if($('#img'+sublimitparentid).attr("alt")=="-"){
		toggleLimit(sublimitparentid);
		toggleLimit(sublimitparentid);
	}
	if($('#mdtaskid').val()==sublimitparentid){
		setTimeout("refreshSublimitFrame()", 1000);
		if($('#img'+sublimitparentid).attr("alt")=="-"){
			setTimeout("toggleLimit("+sublimitparentid+")", 1700);
		}
	}
}
//кнопка удаления
function delSubLimit(id,number,hasChild){
	var message = "Сублимит № "+number+" будет удален из структуры Лимита.";
	if(hasChild){message = "Сублимит № "+number+" будет удален вместе с дочерними.";}
	if(confirm(message)){
		$.post('ajax/delSublimit.do',{id: id});
		$('#tr'+id).remove();
		$('.tr'+id).remove();
	}
}
//обновить секцию
function refreshSublimitFrame(){
	tree_level = 0;
	parent_class = "";
	$('#inLimit').load('frame/inLimit.jsp?'+$('#md_frame_params').val(),sectionTiming);
}
function selectLimit(id, number){
	var outform = window.opener.document.forms[$('#formNameParam').val()];
	outform["inlimitID"].value = id;
	outform["inLimitName"].value = number;
	window.close();
	//скрипт выполнить
	if($('#scriptParam').val()!="null" && $('#scriptParam').val()!=""){
		if (opener.execScript) {
			opener.execScript($('#scriptParam').val()); //for IE
		} else {
			eval('self.opener.' + $('#scriptParam').val()); //for Firefox
		}
	}
}
function toggleLimit(cl) {
    if($('#img'+cl). attr("alt")=="+"){// закрыта, открываем
    	$('#img'+cl).attr({src: "theme/img/collapse.jpg",alt: "-"});
    	tree_level=parseInt($('#img'+cl). attr("lvl"))+1;
    	parent_class = $('#tr'+cl).attr('class')+" tr"+cl;
    	parent_class = parent_class.replace(" markedlimit","");
    	$.post('ajax/limittree.do',{id: cl,child:'true'},addinLimitRow);//подгрузить аяксом
	} else {// открыта, закрываем
		$('.tr'+cl).remove();
		$('#img'+cl).attr({src: "theme/img/expand.jpg",alt: "+"});
	}
}
function onEarly_payment_prohibitionClick(){
	if($('#early_payment_prohibition').prop("checked")){
		$('#early_payment_prohibition_period').show();
	} else {
		$('#early_payment_prohibition_period').hide();
	}
}
function onProductInLimitClick(){
	//отменяем перестановку галочки
	$('#productInLimit').attr('checked', !$('#productInLimit').prop("checked"));
	$('#productInLimitConfirm').jqmShow();
	if($('#productInLimit').prop("checked")){
		$('#notInLimit').show();
		$('.withlimit').show();
		$('.withoutlimit').hide();
	} else {
		$('#notInLimit').hide();
		$('.withlimit').hide();
		$('.withoutlimit').show();
	}
}
function selectInLimit2(){
	$('#inLimitMessage').html(' ('+$('#inLimitName').val()+')');
	$('#productInLimit').attr('checked', true);
	$('#clearInLimit').val('n');
	$('#section_inLimit').hide();
}
function selectInLimit(){
	var mainOrgID = $('#ekID').val();
    openDialog("popup_inlimit.jsp?formName=variables&fieldNames=inlimitID|inLimitName&script=selectInLimit2()&org="+mainOrgID, "List", "top=100, left=100, width=800, height=610, scrollbars=yes, resizable=yes");
}
function notInLimit(){
	$('#productInLimit').attr('checked', false);
	$('#inLimitMessage').html('');
	$('#section_inLimit').hide();
	$('#clearInLimit').val('y');
	// подсветим поля, недопустимые из-за вышестоящего лимита
	checkParentAllowedFilled();
}

function onAmortized_loanClick(){
	if($('#amortized_loan').prop("checked")){
		$('.pmn_fondrate').show();
		$('.pmn_notfondrate').hide();
		$('.graphPaymentsTableFirstDate').html('Дата оплаты');
		$('.periodFondRateTH').html('Ставка фондирования с амортизацией');
	} else {
		$('.pmn_fondrate').hide();
		$('.pmn_notfondrate').show();
		$('.graphPaymentsTableFirstDate').html('Период оплаты <br />(с даты)');
		$('.periodFondRateTH').html('Ставка фондирования');
	}
	$('.graphPaymentsTable').each(function(i) {
		$(this).find('tbody tr').each(function(j) {
			$(this).find('td:first').text((j+1)+".");
		});
	});
	recalculateFondRate();
}
function parseNumber(str){
	if (typeof str == 'undefined'){
		return 0.0;
	}
	var s = str.replace(',','.').replace(/[^0-9\.]+/g,"");
	if(s==''){
		return 0.0;
	}
	return Number(s);
}
function getNewSumGraphPayment(id){
	var sum = parseNumber($('#limit_sum').val());
	if($('#isLimitIssue').prop('checked')){
		sum = parseNumber($('#limitIssue').val());
	}
	if($('#isDebtLimit').prop('checked') && !$('#isLimitIssue').prop('checked')){
		sum = parseNumber($('#debtLimit').val());
	}
	//if(id!=''){
	//	sum=parseNumber($('#trancesum'+id).val());
	//}
	$('#graphPaymentsTableId'+id+' tbody tr input.sum').each(function(i) {
		sum=sum-parseNumber($(this).val());
		if(sum<0){
			sum=0.0;
		}
	});
	return sum;
}
function AddIndCondition(){
	$( "#indConditionTemplate" ).tmpl({id:getNextId()}).appendTo( "#main_indConditionsTableId > TBODY" );
}
function AddGraphPayments(id){
	$( "#graphPaymentTemplate").tmpl({id:getNextId(),sum:getNewSumGraphPayment(id),trid:id}).appendTo( "#graphPaymentsTableId"+id+" > TBODY" );
	onAmortized_loanClick();
	$('.autosize').autosize();
	dialogHandler();
	fieldChanged();
	calendarInit();
}
function AddRowToTableOperationDecision() {
	$( "#OperationDecisionTemplate" ).tmpl({id:getNextId()}).appendTo( "#main_operationDecisionTableId > TBODY" );
}
function onPremiumTypeClick(id){
	if($("#premiumtype"+id+" :selected").val()=='0'){//не выбрана премия
		$('#premiumSizeTr'+id).hide();
	} else {
		$('#premiumSizeTr'+id).show();
		var v = $("#premiumtype"+id+" :selected").val();
		var ptype = $('#premiumTradeValue'+v).html();
		$('#premiumvalue'+id).hide();
		$('#premiumcurr'+id).hide();
		$('#premiumcurrpercent'+id).hide();
		$('#premiumtext'+id).hide();
		$('#premiumSize'+id).html('');
		if(ptype=='Валюта'){
			$('#premiumvalue'+id).show();
			$('#premiumcurr'+id).show();
		}
		if(ptype=='Валюта/ %'){
			$('#premiumvalue'+id).show();
			$('#premiumcurrpercent'+id).show();
		}
		if(ptype=='Формула'){
			$('#premiumtext'+id).show();
		}
		if(ptype!='Валюта'&&ptype!='Валюта/ %'){
			$('#premiumSize'+id).html(ptype);
		}
	}
}
function onUpdateGraphPaymentDateResponse(ans){
	$('#period'+ans.id).val(ans.interval);
	$('#fondrate'+ans.id).val(ans.fondrate);
	$('#fondrate'+ans.id).next('input').val('n');
	$('#fondrate'+ans.id).next('input').next('span').hide();
	recalculateFondRate();
}
function recalculateGraphPaymentDate(){
	//пересчитать графики погашения
	if($('#amortized_loan').prop("checked")){
		$('#graphPaymentsTableId > tbody > tr > td > .datefrom').each(function(index) {
			var id = $(this).attr('id');
			onUpdateGraphPaymentDate(id);
		});
	}
}
function getCurr(){
	if($('#isLimitIssue').prop('checked')){
		$('#currency_LimitIssue').val();
	}
	if($('#isDebtLimit').prop('checked') && !$('#isLimitIssue').prop('checked')){
		 $('#currency_DebtLimit').val();
	}
	return $('#currency_Sum').val();
}
function onUpdateGraphPaymentDate(id){
	graphid=id;
	var indRate = $("#indRate :selected").text();
	if(indRate==""){indRate=$("#indrateoriginal").val();}
	if(indRate==null){indRate="";}
	var rateTypeFixed=$("#ratetypefixedoriginal").val();
	if($("#RateTypeFixed").size()>0){rateTypeFixed=$("#RateTypeFixed").prop("checked");}
	$.getJSON('ajax/graphPaymentFondRate.html',{date1: $('#proposedDateSigningAgreement').val(),date2:$('#'+id).val(),
		cur:getCurr(),id:id,rateTypeFixed:rateTypeFixed,indRate:indRate},
		onUpdateGraphPaymentDateResponse);
}
function showChangeSPForm(grid){
	$('#grid').val(grid);
	$.post('ajax/standardPeriodValue.html',{grid: grid},showChangeSPFormOnAjax);
}
function showChangeSPFormOnAjax(xml){
	$('#changeSPFormValueId').html('');
	$("#cmnt").val('');
	$("#days").val('');
	$('spval',xml).each(function(index) {
		$('#changeSPFormValueId').append('<p><input value="'+$('id:first',this).text()
				+'" name="valueid" class="valueid" type="radio">'
				+$('name:first',this).text()+' ('+$('period:first',this).text()+' дн.)</p>');
	});
	checkemptyStPerCmnt();
	$('#changeSPForm').dialog({width:800,draggable: false});
}
function checkemptyStPerCmnt(){
    if($("#cmnt").val()==''){
        $("#chStPerLink").hide();
        $("#emptyStPerCmnt").show();
    } else {
        $("#chStPerLink").show();
        $("#emptyStPerCmnt").hide();
    }
}
function StPerChOnClick(){
    if($("#cmnt").val()==''){
        alert('Для изменения нормативного срока необходимо заполнить поле комментарий');
        return false;
    }
    $('#changeSPForm').dialog('close');
    $.post('ajax/changeStandardPeriod.do',
    {vid: $('.valueid:checked').val(), mdtaskid:$("#mdtaskid").val(),cmnt:$("#cmnt").val(),days:$("#days").val(),grid:$("#grid").val()},reloadstandardPeriodFrame);
}
function pipelineResult(msg){
	if(msg=='OK'){
		//$('#pipeline_result').html('Секция продуктового менеджера успешно сохранена.');
		$('#pipeline_result').html('');
		changed=false;
	} else {
		$('#pipeline_result').html('Ошибка сохранения: '+msg);
	}
}
function savePipeline(){
	pipeline_fin_target_array=[];
	var ac = (new Date()).getTime();
	$('.pipeline_fin_target').each(function(){
	    pipeline_fin_target_array.push($(this).val());
	});
	var cp = $('#pipeline_close_probability').val();
	if(cp==null){cp="";}
	$.post('ajax/save_pipeline.html',{mdtaskid:$("#mdtaskid").val(),pipeline_status:$('#pipeline_status').val(),plan_date:$('#pipeline_plan_date').val(),
		pipeline_close_probability:cp,pipeline_wal:$('#pipeline_wal').val(),pipeline_hurdle_rate:$('#pipeline_hurdle_rate').val(),
		pipeline_markup:$('#pipeline_markup').val(),pipeline_margin:$('#pipeline_margin').val(),
		pipeline_pc_cash:$('#pipeline_pc_cash').val(),pipeline_pc_res:$('#pipeline_pc_res').val(),pipeline_pc_der:$('#pipeline_pc_der').val(),pipeline_pc_total:$('#pipeline_pc_total').val(),
		pipeline_line_count:$('#pipeline_line_count').val(),pipeline_syndication:$('#pipeline_syndication').is(':checked'),
		pipeline_factor_product_type:$('#pipeline_factor_product_type').val(),pipeline_factor_period:$('#pipeline_factor_period').val(),
        pipeline_pub:$('#pipeline_pub').is(':checked'),pipeline_statusManual:$('#pipeline_statusManual').is(':checked'),pipeline_priority:$('#pipeline_priority').is(':checked'),
		pipeline_new_client:$('#pipeline_new_client').is(':checked'),pipeline_prolongation:$('#pipeline_prolongation').is(':checked'),
		pipeline_hideinreport:$('#pipeline_hideinreport').is(':checked'),
		pipeline_hideinreporttraders:$('#pipeline_hideinreporttraders').is(':checked'),
		pipeline_law:$('#pipeline_law').val(),pipeline_geography:$('#pipeline_geography').val(),pipeline_description:$('#pipeline_description').val(),
		pipeline_cmnt:$('#pipeline_cmnt').val(),pipeline_addition_business:$('#pipeline_addition_business').val(),pipeline_syndication_cmnt:$('#pipeline_syndication_cmnt').val(),
		pipeline_rating:$('#pipeline_rating').val(),pipeline_contractor:$('#pipeline_contractor').val(),pipeline_vtb_contractor:$('#pipeline_vtb_contractor').val(),
		pipeline_trade_desc:$('#pipeline_trade_desc').val(),supply:$('#pipeline_supply').val(),flow_investment:$('#pipeline_flow_investment').val(),
		trade_finance_id:$('#pipeline_trade_finance_id').val(),
		pipeline_fin_target:pipeline_fin_target_array,anticache:ac,close_probability_is_manual:$('#pipeline_close_probability_is_manual').val()},pipelineResult);
}
function reloadstandardPeriodFrame(){
	$('#standardPeriod').html('Идёт обновление секции');
	$('#standardPeriod').load('frame/standardPeriod.jsp?'+$('#md_frame_params').val());
}
function onChangeHurdleRate(){
	try{
		if($('#tasktype').val()=='p'){
			//$('#pipeline_markup').val(toFormattedString(parseNumber($('#original_effrate').val())-parseNumber($('#pipeline_hurdle_rate').val()),'money3digits'));
		}
		//input_autochange(document.getElementById('pipeline_markup'),'money2digits');
	} catch (Err) {
		//console.error(Err.message);
	}
}
function onPrrStart(ans){
	$('#startPrrBtn').prop('disabled', true);
	$('#startPrrBtn').addClass('disabled');
	alert(ans);
}
function asize(){
	$('.autosize').autosize();
}
function getParameterByName(name) {
    name = name.replace(/[\[]/, "\\[").replace(/[\]]/, "\\]");
    var regex = new RegExp("[\\?&]" + name + "=([^&#]*)"),
        results = regex.exec(location.search);
    return results === null ? "" : decodeURIComponent(results[1].replace(/\+/g, " "));
}
function traderConfirm(){
	var cachebuster = Math.round(new Date().getTime() / 1000);
	$('#trader_approve_message').load("ajax/trader_approve.html?id="+$('#mdtaskid').val() + "&cb=" + cachebuster);
}
function refuse_miu2(){//VTBSPO-503
	//$('#save_action').val('Отказать в акцепте');
	$.post('ajax/refuse_miu2.html',{mdtaskid:$("#mdtaskid").val()},function( data ) {
		location.reload();
	});
}
