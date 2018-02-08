/**
 * JavaScript для секции стоимостные условия.
 */
function addPeriod(){
	fieldChanged();
	var id='percentFact'+getNextId();
	var param = {id:id};
	$( "#newPeriodTemplate" ).tmpl(param).appendTo( "#periods > TBODY" );
	$( "#newPercentFactTemplate" ).tmpl(param).appendTo( "#percentFact" );
	//Добавить расчетные значения 
	//$( "#newPercentRatingTemplate" ).tmpl(param).appendTo( "#percentRating" );
	recalculatePercentRate();
	updateProductWithPeriod();
	dialogHandler();
	onRateTypeFixedChange();
	calendarInit();
}
function delPeriod(id){
	$('.'+id).remove();
	updateProductWithPeriod();
	return false;
}
function updateProductWithPeriod(){
	showHidePriceConditionFields();
}
function showHidePriceConditionFields(){
	//Ставка размещения. Поля Применяется с и Основание. VTBSPO-785
	if($('.FactPercentClass').size()>1){// по периодам. Смотрим фиксированную ставку по каждому периоду
		$(".interest_rate_fixed").each(function(index) {
			var name = $(this).attr('name');
			var cl = name.replace('interest_rate_fixed', '');;
			if($(this).prop('checked')){
				$('.rate4fixedOnlyDiv'+cl).show();
			} else {
				$('.rate4fixedOnlyDiv'+cl).hide();
				//очистить поля
				$('.rate4fixedOnlyDiv'+cl + ' p textarea').val('');
				$('.rate4fixedOnlyDiv'+cl + ' p input').val('');
			}
		});
	} else {// Нет разделения по периодам. Смотрим фиксированную ставку по сделке в целом
		if($('#interest_rate_fixed').prop("checked")){
			$('.rate4fixedOnly').show();
		} else {
			$('.rate4fixedOnly').hide();
			//очистить поля
			$('.rate4fixedOnly p textarea').val('');
			$('.rate4fixedOnly p input').val('');
		}
	}
	if($('.FactPercentClass').size()>1){
		$('#productWithPeriod').html('Срок сделки разделен на периоды');
		$('.periodHeader').show();
		$('.periodOnly').show();
		$('.noneperiodOnly').hide();
		$('#periods').show();
	} else {
		$('#productWithPeriod').html('Срок сделки не разделен на периоды');
		$('.periodHeader').hide();
		$('.periodOnly').hide();//скрываем те поля, которые должны показываться только когда в сделке есть периоды
		$('.noneperiodOnly').show();
		$('#periods').hide();
	}
	/*if(!$("#interest_rate_derivative").prop("checked")){
		$('.floatOnly').hide();
	} else {
		$('.floatOnly').show();
	}*/
	$(".interest_rate_derivative").each(function(index) {
		var name = $(this).attr('name');
		var cl = name.replace('interest_rate_derivative', '');;
		if($(this).prop('checked') && ($('.FactPercentClass').size()>1 || cl=='')){
			$('.floatOnly'+cl).show();
		} else {
			$('.floatOnly'+cl).hide();
		}
	});
	if($('.FactPercentClass').size()>1){
		$('.noneperiodOnly').hide();
	}
}
/** пересчитывает ставки фондирования */
function recalculateFondRate(){
	//менять свое наименование на «Ставка фондирования с амортизацией»
	if($('#amortized_loan').prop("checked")){
		$('.periodFondRateTH').html('Ставка фондирования с амортизацией');
		fondRate=getFondRate('');
		$('.periodFondRate').val(toFormattedString(fondRate,'money'));
		//убрать восклицательные знаки
		$('.periodFondRate').next('input').val('n');
		$('.periodFondRate').next().next('span').hide();
		//транши
		$('input[name=trance_id]').each(function(index) {
			var tranceid=$(this).val();
			fondRate=getFondRate(tranceid);
			$('.trance'+tranceid+'fondRate').val(toFormattedString(fondRate,'money'));
			//убрать восклицательные знаки
			$('.trance'+tranceid+'fondRate').next('input').val('n');
			$('.trance'+tranceid+'fondRate').next().next('span').hide();
		});
	} else {
		$('.periodFondRateTH').html('Ставка фондирования');
		/*var indRate = $("#indRate :selected").text();
		if(indRate==""){indRate=$("#indrateoriginal").val();}
		if(indRate==null){indRate="";}
		var rateTypeFixed=$("#ratetypefixedoriginal").val();
		$.getJSON('ajax/graphPaymentFondRate.html',{date1: $('#proposedDateSigningAgreement').val(),date2:$('#mdtask_date').val(),
			cur:getCurr(),id:'fondrate',rateTypeFixed:rateTypeFixed,indRate:indRate},
			non_amortized_fond_rate);
		//транши
		$('.trid').each(function(index) {
			var tranceid=$(this).val();
			$.getJSON('ajax/graphPaymentFondRate.html',{date1: $('#trancefrom'+tranceid).html(),date2:$('#tranceto'+tranceid).html(),
				cur:getCurr(),id:tranceid,rateTypeFixed:rateTypeFixed,indRate:indRate},
				trance_fond_rate);
		});*/
	}
}
function trance_fond_rate(ans){
	$('#'+ans.id+'trfondrate').val(ans.fondrate);
	//убрать восклицательные знаки
	$('#'+ans.id+'trfondRate').next('input').val('n');
	$('#'+ans.id+'trfondRate').next().next('span').hide();
}
function non_amortized_fond_rate(ans){
	$('.periodFondRate').val(ans.fondrate);
	$('.periodFondRate').next('input').val('n');
	$('.periodFondRate').next().next('span').hide();
}
function getFondRate(tranceId){
	fondRate1 = 0.0;
	fondRate2 = 0.0;
	$('#graphPaymentsTableId'+tranceId+' > tbody > tr').each(function(index) {
		sumPeriod = toNumberFromString($(this).find('td > input.sum').val(),'money') * toNumberFromString($(this).find('td > input.days').val(),'');
		fondRate1 +=sumPeriod*toNumberFromString($(this).find('td > input.fondrate').val(),'money');
		fondRate2 +=sumPeriod;
	});
	fondRate=(fondRate1/fondRate2);
	return fondRate;
}
/** пересчитывает процентные ставки */
function recalculatePercentRate(){
	//Поле присутствует только для плавающей ставки. Устанавливается значением Индикативной ставки из соответствующего 
	//поля в шапке подсекции «Процентная ставка». Не доступно для редактирования.
	$('.rate7').text($("#rate7").val());
	$('.rate8').text($("#rate8").val());
	
	//ставки по периодам
	$('.percentFactID').each(function(index) {
		var id = $(this).val();
		riskStepupFactor="0";
		if(typeof riskStepupFactorHash != 'undefined'){
			try{
				riskStepupFactor = riskStepupFactorHash[$('#'+id+'riskStepupFactor').val()];
				$('#'+id+'riskStepupFactorspan').text(riskStepupFactor);
				$('#'+id+'rating_riskStepupFactorspan').text(riskStepupFactorHash[$('#'+id+'rating_riskStepupFactor').val()]);
			} catch (Err) {}
		}
		var param = {id:id, rate4:$('#'+id+'rate4').val(), rate9:$('#rate9').val(), rate10:$('#rate10').val(),
				riskStepupFactor:riskStepupFactor,
				riskpremium:$('#'+id+'riskpremium').val(),fondrate:$('#'+id+'fondrate').val(),rate3:$('#'+id+'rate3').val(),
				rate5:$('#percentFactrate5').val(), rate6:$('#percentFactrate6').val(), rate7:$('#rate7').val(), rate8:$('#rate8').val(),
				riskpremium_type:$('#riskpremium_type'+id).val(),riskpremium_change:$('#'+id+'riskpremium_change').val()};
		$.post('ajax/recalculatePercentRate.do',param,showNewPercentRate);
	});
	//ставки по траншам
	$('.trid').each(function(index) {
		var id = $(this).val();
		riskStepupFactor="0";
		if(typeof riskStepupFactorHash != 'undefined'){
			try{
				riskStepupFactor = riskStepupFactorHash[$('#'+id+'trriskStepupFactor').val()];
				$('#'+id+'trriskStepupFactorspan').text(riskStepupFactor);
				$('#'+id+'trrating_riskStepupFactorspan').text(riskStepupFactorHash[$('#'+id+'trrating_riskStepupFactor').val()]);
			} catch (Err) {}
		}
		var param = {id:id, rate4:$('#'+id+'trrate4').val(), rate9:$('#'+id+'trrate9').val(), rate10:$('#'+id+'trrate10').val(),
				riskStepupFactor:riskStepupFactor,
				riskpremium:$('#'+id+'trriskpremium').val(),fondrate:$('#'+id+'trfondrate').val(),rate3:$('#'+id+'trrate3').val(),
				rate5:$('#'+id+'trrate5').val(), rate6:$('#'+id+'trrate6').val(), rate7:$('#rate7').val(), rate8:$('#rate8').val(),
				riskpremium_type:$('#trriskpremium_type'+id).val(),riskpremium_change:$('#'+id+'trriskpremium_change').val()};
		$.post('ajax/recalculatePercentRate.do',param,showNewTranceRate);
	});
	
	//onRateTypeFixedChange();
}
function showNewTranceRate(xml){
	var id = $('id:first',xml).text();
	$('#'+id+'calcRate').html($('calcRate:first',xml).text()+" % годовых");
	$('#'+id+'calcRateProtected').html($('calcRateProtected:first',xml).text()+" % годовых");
	$('#'+id+'effRate').html($('effRate:first',xml).text() + " % годовых");
}
function showNewPercentRate(xml){
	var id = $('id:first',xml).text();
	$('#'+id+'calcRateProtected').html($('calcRateProtected:first',xml).text()+" % годовых");
	var effRate = parseFloat($('effRate:first',xml).text().replace(/,/g, '.'));
	$('.effrateCommission').each(function() {//учесть комиссии
		effRate = effRate + parseFloat($(this).val());
	});
	$('#'+id+'effRate').html(Math.round(effRate * 1000)/1000 + " % годовых");
	$('#'+id+'calcRate').html($('calcRate:first',xml).text()+" % годовых");

}
function loadPercentCRM(){
	$('#percentCRM').load('frame/percentCRM.jsp?'+$('#md_frame_params').val());
}
function AddRowToTablePunitiveMeasure() {
try
{
	fieldChanged();
	var tbl = document.getElementById('testShtrafiTableId');
	var body = tbl.getElementsByTagName("TBODY")[0];
	var Rows = body.rows;
	var child = Rows[0];
	var myTR = child.cloneNode(true);
	myTR.style.display = "";
	nextid=getNextId();
	myTR.getElementsByTagName("td").item(0).innerHTML=
		'<div align="left">' +
		'<label>Наименование события (Тип штрафной санкции)<br>' +
		'<textarea id="newPunitiveMeasure'+nextid+'" name="Штрафные санкции" style="width:98%;" onkeyup="fieldChanged(this)" onchange="checkParentAllowed(this);"></textarea>'+
		'<a href="javascript:;" onclick="punitiveMeasureSelectTemplate(\'newPunitiveMeasure'+nextid+'\');">'+
		'<img alt="выбрать из шаблона" src="style/dots.png"></a>' +
		'</label> </div>' + 
		myTR.getElementsByTagName("td").item(0).innerHTML;
  	body.appendChild(myTR);
	var htmlNodeIterator = new HTMLNodeIterator();
	htmlNodeIterator.iterate(makeVisible, myTR, null);
}
catch (Err)
{
	alert(Err.description)
	return false
}
}
$(document).ready(function() {
	updateProductWithPeriod();
	recalculatePercentRate();
	$("a.punitiveMeasureSelectTemplate").fancybox({
		'transitionIn'	:	'elastic',
		'transitionOut'	:	'elastic',
		'speedIn'		:	600, 
		'speedOut'		:	200, 
		'overlayShow'	:	true,
		'zoomOpacity'			: true,
		'zoomSpeedIn'			: 500,
		'zoomSpeedOut'			: 500,
		'hideOnContentClick': false,
		'frameWidth': 800, 
		'frameHeight': 600,
		'showCloseButton': true
	});
});
function onRateTypeFixedChange() {
	showHidePriceConditionFields();
	recalculateFondRate();
}
function setEditedRiskPremium() {
	try {
		document.getElementById('risk_premium_manually_edited').value='yes'; 
		document.getElementById('riskPremiumFact').style.backgroundColor='#FFFFFF';
		document.getElementById('procentFact').style.backgroundColor='#FFFFFF';
	} catch (Err) {}
}

function setEditedProcent() {
	try {
		document.getElementById('procent_manually_edited').value='yes'; 
		document.getElementById('riskPremiumFact').style.backgroundColor='#FFFFFF';
		document.getElementById('procentFact').style.backgroundColor='#FFFFFF';
	} catch (Err) {}
}

function checkErrors(myfield) {
	var value = myfield.value;
	if ( value < 1 || value > 31 ) {
		myfield.value = '';
		alert('Введенное значение представляет собой календарный день месяца и должно быть в пределах от 1 до 31 включительно');
	}  
}
/**
 * Добавляет новую строчку к штрафным санкциям.
 */
function insertPunitiveMeasureTR(guid,template_name){
	fieldChanged();
	$("#newPunitiveMeasureTemplate").tmpl({nextid:getNextId(),guid:guid,template_name:template_name}).appendTo( "#testShtrafiTableId"+guid+" > TBODY" );
}
function insertCommissionTableTR(){
	fieldChanged();
	$("#newCommissionTableTemplate").tmpl({nextid:getNextId()}).appendTo( "#idCommissionTable > TBODY" );
}
function punitiveMeasureSelectTemplate(id,template_name){
	$('#punitiveMeasureTemplate').val(id);
}
function onPunitiveMeasureSelectTemplateClick(name, desc, pmid, sum, cur){
	$('#'+$('#punitiveMeasureTemplate').val()).val(name);
	$('#Desc'+$('#punitiveMeasureTemplate').val()).text(desc);
	$('#val'+$('#punitiveMeasureTemplate').val()).val(sum);
	$('#cur'+$('#punitiveMeasureTemplate').val()).val(cur);
	$('#idDict'+$('#punitiveMeasureTemplate').val()).val(pmid);
	$('#d'+$('#punitiveMeasureTemplate').val()).val(desc);
	//TODO sum, cur
	if(desc==''){
		//$('#Desc'+$('#punitiveMeasureTemplate').val()).hide();
		$('.'+$('#punitiveMeasureTemplate').val()).show();
	} else {
		$('#Desc'+$('#punitiveMeasureTemplate').val()).show();
		//$('.'+$('#punitiveMeasureTemplate').val()).hide();
	}
}
function riskpremiumTypeTemplateClick(name,type,id,percentID){
	$('#riskpremium_type_name'+percentID).html(name);
	$('#riskpremium_type'+percentID).val(id);
	if(type=='увеличенная' || type=='уменьшенная'){
		$('#riskpremium_change_tr'+percentID).show();
	} else {
		$('#riskpremium_change_tr'+percentID).hide();
		$('#'+percentID+'riskpremium_change').val('');
	}
	if(type=='увеличенная'){
		$('#riskpremium_change_name'+percentID).html('Величина увеличения');
	}
	if(type=='уменьшенная'){
		$('#riskpremium_change_name'+percentID).html('Величина уменьшения');
	}
	recalculatePercentRate();
}
function add_rate_ind_rate(id,nextid){
	if(id==''){//для сделки в целом
		$('.effRateTr2').before('<tr align="left"  class="noneperiodOnly"><th>Надбавка к плавающей ставке <span id="rate_ind_rate_span'+nextid+'"></span></th><td><input class="money" '+
		' name="rate_ind_rate" onblur="input_autochange(this,\'money\')" > % годовых</td></tr>');
		$('#indrateTable tbody tr:last-child td:first-child select').attr("onchange",
			$('#indrateTable tbody tr:last-child td:first-child select').attr("onchange")+";$('#rate_ind_rate_span"+nextid+"').text($('option:selected',this).text())");
		$('#indrateTable tbody tr:last-child td:first-child select').after('<button class="del" ' +
		'onclick="$(\'#rate_ind_rate_span'+nextid+'\').parent().parent().remove();$(this).parent().remove();indratesSync();return false"></button>');
	} else {//для периода
		$('#'+id+'effRateTr').before('<tr align="left"  class="periodOnly"><th>Надбавка к плавающей ставке <span id="rate_ind_rate_span'+nextid+'"></span></th><td><input class="money" '+
		' name="rate_ind_rate_'+id+'" onblur="input_autochange(this,\'money\')" > % годовых</td></tr>');
		$('#indrateTable'+id+' tbody tr:last-child td:first-child select').attr("onchange",
			$('#indrateTable'+id+' tbody tr:last-child td:first-child select').attr("onchange")+";$('#rate_ind_rate_span"+nextid+"').text($('option:selected',this).text())");
		$('#indrateTable'+id+' tbody tr:last-child td:first-child select').after('<button class="del" ' +
		'onclick="$(\'#rate_ind_rate_span'+nextid+'\').parent().parent().remove();$(this).parent().remove();indratesSync();return false"></button>');
	}
	onRateTypeFixedChange();
}
function indratesSync(){
	if($('select[name=indRate]').length>1 && $('#interest_rate_derivative').prop("checked")){
		//добавить недостающее в периоды
		$('select[name=indRate]').each(function() {
			var id_indrate = $(this).val();
			var name = $("option:selected", this).text();
			if(id_indrate!=''){
				$('select[name^="indRatepercentFact"]').each(function() {//по всем индикативным ставкам периода
					if( $("option[value='"+id_indrate+"']", this).length == 0){//если нет опции
						$(this).append('<option value="'+id_indrate+'">'+name+'</option>');
					}
				});
			}
		});
		//удалить лишнее из периодов
		$('select[name=indRate] option').each(function() {
			var id_indrate = $(this).val();
			if($('select[name="indRate"] option:selected[value="'+id_indrate+'"]').length==0)
				$("select[name^='indRatepercentFact'] option[value='"+id_indrate+"']").remove();
		});
	} else {//случай для полного справочника
		//добавить недостающее в периоды
		$('select[name=indRate] option').each(function() {
			var id_indrate = $(this).val();
			var name = $(this).text();
			if(id_indrate!=''){
				$('select[name^="indRatepercentFact"]').each(function() {//по всем индикативным ставкам периода
					if( $("option[value='"+id_indrate+"']", this).length == 0){//если нет опции
						$(this).append('<option value="'+id_indrate+'">'+name+'</option>');
					}
				});
			}
		});
	}
}
function calcCommission(){
	$('#idCommissionTable > tbody > tr > td > table > tbody > tr > td > label > span').each(function() {
		$.getJSON('ajax/calc_commission.html',{value: $('#'+$(this).attr('id')).parent().parent().find('input').val(),
				curr: $('#'+$(this).attr('id')).parent().parent().find('select[name="Валюта Комиссии"]').val(),
				comissionType: $('#'+$(this).attr('id')).parent().parent().find('select[name="Наименование комиссии"]').val(),
				id:$(this).attr('id'),procent_order:$('#'+$(this).attr('id')).parent().parent().find('select[name="Порядок уплаты процентов Комиссии"]').val(),
				mdtaskid:$('#mdtaskid').val()},
			calcCommissionAns);
	});
}
function calcCommissionAns(ans){
	$('#'+ans.id).html(ans.val + " <input type='hidden' class='effrateCommission' value='"+ans.eff+"'>");
	//пересчитать эффективную ставку
	recalculatePercentRate()
}
