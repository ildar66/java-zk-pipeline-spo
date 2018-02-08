var exclusionHash = {'stopfactorsClient':'stopfactorsClient', 'stopfactorsSecurity':'stopfactorsSecurity',
		'Тип плавающей части' : 'Тип плавающей части', 'Плавающая часть':'Плавающая часть', 'АРС':'АРС', 'Базовая премия за кредит. риск':'Базовая премия за кредит. риск', 'Коэффициент транзакцион. риска С1':'Коэффициент транзакцион. риска С1', 'Коэффициент транзакцион. риска С2':'Коэффициент транзакцион. риска С2', 'Экономическая маржа':'Экономическая маржа', 'Расчетная ставка':'Расчетная ставка', 'Дата расчета стоимостных условий':'Дата расчета стоимостных условий', 'Прогноз_Тип плавающей части':'Прогноз_Тип плавающей части', 'Прогноз значения ставки':'Прогноз значения ставки',
		'isLimitIssue':'isLimitIssue', 'isDebtLimit':'isDebtLimit',
		'w_main':'w_main', 'w_FullSum':'w_FullSum',
		'guarantee_main':'guarantee_main', 'guarantee_cond':'guarantee_cond', 'd_main':'d_main',
		'main Перераспределение остатков между Сублимитами':'main Перераспределение остатков между Сублимитами',
//		'w_resp':'w_resp',
		'supply_exist_check':'supply_exist_check',
		'd_special_condition':'d_special_condition',
		'Менеджер сделки':'Менеджер сделки',
		'trance_comment':'trance_comment',
		// do not check theese because always are checked on save
		'Сумма лимита выдачи':'Сумма лимита выдачи', 'Сумма лимита задолженности':'Сумма лимита задолженности'
};

var guaranteeTypeHash = {'Контракт':'Контракт','Предмет_гарантии':'Предмет_гарантии','Бенефициар':'Бенефициар'};

var readOnlyExclusions = {'d_SupplyType':'d_SupplyType', 'w_type':'w_type', 'guarantee_type' : 'guarantee_type',
		'Стандартные стоимостные условия':'Стандартные стоимостные условия' };

var flagValidateVariablesOnOperationCompletion = true;

/**
 * check for validity of variables on Save button click
 * @return true, if check is successfull
 */
function validate_variablesOnSave(){
	document.getElementById("errorMessage").innerHTML=''
	valid = true;
	try {
		if (!validateTextareaSize()) {valid = false;}
		if (!validateIndrateUniq()) {valid = false;}
		var isLimitIssue = document.getElementById("isLimitIssue");
		var isLimitIssueChk = document.getElementById("isLimitIssue").checked;
		var isDebtLimitChk = document.getElementById('isDebtLimit').checked;
		
		var limitSum = document.getElementById('limit_sum');
		var limitIssue = document.getElementById('limitIssue');
		var debtLimit = document.getElementById('debtLimit');
		
		if ((!isLimitIssueChk) && (!isDebtLimitChk) && (limitSum.value == "")) {
			document.getElementById("errorMessage").style.display = "inline";
			document.getElementById("errorMessage").innerHTML=document.getElementById("errorMessage").innerHTML+
				'Необходимо заполнить поле "Сумма сделки" в секции "Основные параметры сделки"<br /><br />';
			valid = false;	
		}
		if ((isLimitIssueChk) && (limitIssue.value == "")) {
			document.getElementById("errorMessage").style.display = "inline";
			document.getElementById("errorMessage").innerHTML=document.getElementById("errorMessage").innerHTML+
				'Необходимо заполнить поле "Сумма лимита выдачи" в секции "Основные параметры сделки"<br /><br />';
			valid = false;	
		}
		if ((isDebtLimitChk) && (debtLimit.value == "")) {
			document.getElementById("errorMessage").style.display = "inline";
			document.getElementById("errorMessage").innerHTML=document.getElementById("errorMessage").innerHTML+
				'Необходимо заполнить поле "Сумма лимита задолженности" в секции "Основные параметры сделки"<br /><br />';
			valid = false;	
		}
	} catch (Err) {	} 

//	try {
//		var proposedDateSigningAgreement = document.getElementById('proposedDateSigningAgreement');
//		if (proposedDateSigningAgreement.value == "") {
//			document.getElementById("errorMessage").style.display = "inline";
//			document.getElementById("errorMessage").innerHTML=document.getElementById("errorMessage").innerHTML+
//			'Необходимо заполнить поле "Планируемая дата подписания Кредитного соглашения" в секции "Основные параметры сделки"<br /><br />';
//			valid = false;	
//		}	
//	} catch (Err) {	}

	valid = valid && validateOnServer(valid); 
	if(!valid)$('#redirecturl').val($('#redirecturl_original').val());
	enableSubmitButton();
	return valid;
}
function validateTextareaSize() {
	//Основные параметры сделки
	oversizeChk("Комментарий по сроку сделки","Комментарий по сроку сделки в секции основные параметры",2000);
	oversizeChk("Комментарий по сроку использования сделки","Комментарий по сроку использования в секции основные параметры",2000);
	oversizeChk("Описание категории качества","Описание категории качества в секции основные параметры",1024);
	//Процентная ставка
	oversizeChk("factPercentIndCondition","Индивидуальные условия в секции Процентная ставка",2000);
	oversizeChk("rate4desc","Ставка размещения в секции Процентная ставка",2000);
	oversizeChk("supply","Обеспечение по периоду в секции Процентная ставка",2000);
	oversizeChk("indRateReason","Основание индикативной ставки в секции Процентная ставка",1000);
	oversizeChk("rate4_reason","Основание ставки размещения в секции Процентная ставка",1000);
	//Комиссии, вознаграждения
	oversizeChk("Описание Комиссии","Комментарии в секции Комиссии, вознаграждения",1024);
	oversizeChk("Срок оплаты комиссии","Срок оплаты комиссии в секции Комиссии, вознаграждения",1024);
	//Санкции (неустойки, штрафы, пени и т.д.)
	oversizeChk("Штрафные санкции","Наименование санкции (Тип штрафной санкции) в секции Санкции (неустойки, штрафы, пени и т.д.)",4000);
	oversizeChk("fine_value_text","Величина санкции (неустойки, штрафа, пени и т.д.)  в секции Санкции (неустойки, штрафы, пени и т.д.)",4000);
	//другие секции
	oversizeChk("contract","Договоры в секции Договоры",1024);
	oversizeChk("pmn_order","Порядок погашения задолженности в секции График платежей погашение основного долга",4000);
	oversizeChk("pmn_desc","Описание периода оплаты в секции График платежей погашение основного долга",4000);
	//Графики платежей
	oversizeChk("prncp Комментарии к графику погашения ОД","Порядок погашения в секции Погашение основного долга",1024);
	oversizeChk("prncp Комментарии","Комментарии в секции Погашение основного долга",1024);
	oversizeChk("int_pay Комментарии к графику погашения процентов","Порядок погашения процентов в секции График погашения процентов",1024);
	oversizeChk("int_pay Комментарии","Комментарии в секции График погашения процентов",1024);
	//Условия
	oversizeChk("changedConditions","Измененные и дополненные условия  в секции Условия",4000);
	oversizeChk("Условие досрочного погашения","Комментарий в секции Условия",4000);
	oversizeChk("condition1","Отлагательные условия заключения сделки в секции Условия",4000);
	oversizeChk("condition2","Отлагательные условия использования средств в секции Условия",4000);
	oversizeChk("condition9","Отлагательные условия открытия аккредитива, выдачи гарантии в секции Условия",4000);
	oversizeChk("condition8","Дополнительные условия в секции Условия",4000);
	oversizeChk("condition4","Индивидуальные условия в секции Условия",4000);
	oversizeChk("condition7","Стоимостные параметры сделки в секции Условия",4000);
	oversizeChk("condition10","Условия залогового обеспечения в секции Условия",4000);
	oversizeChk("condition6","Критерий и устанавливаемый размер кредитовых оборотов в секции Условия",4000);
	oversizeChk("condition5","Финансовые ковенанты в секции Условия",4000);
	oversizeChk("condition12","Основание для досрочного истребования, приостановления использования в секции Условия",4000);
	oversizeChk("condition13","Очередность погашения в секции Условия",4000);
	//Обеспечение
	oversizeChk("d_zalog_desc","Наименование и характеристики предмета залога в секции Обеспечение",2048);
	oversizeChk("d_orderDescription","Порядок определения рыночной стоимости в секции Обеспечение",2048);
	oversizeChk("d_oppDescription","Описание залоговой сделки в секции Обеспечение",2048);
	oversizeChk("d_cond","Условия страхования в секции Обеспечение",2048);
	oversizeChk("d_special_condition","Индивидуальные условия залоговых сделок в секции Обеспечение",4000);
	oversizeChk("w_add","Дополнительные обязательства по Поручителю в секции Обеспечение",1024);
	oversizeChk("w_special_condition","Индивидуальные условия поручительства в секции Обеспечение",4000);
	oversizeChk("g_special_condition","Индивидуальные условия гарантии в секции Обеспечение",4000);
	//лимит
	oversizeChk("productGroup","Группы видов сделок в секции Основные параметры лимита",4000);
	oversizeChk("main_operationDecisionList_accepted","Принимаются в секции Порядок принятия решения о проведении операций в рамках сублимитов",2020);
	oversizeChk("main_operationDecisionList_specials","Особенности принятия решений в секции Порядок принятия решения о проведении операций в рамках сублимитов",2020);
	//Стоимостные условия
	oversizeChk("priceIndCondition","Индивидуальные условия в секции Стоимостные условия",2000);
	oversizeChk("sublimit_capitalPay","Плата за экономический капитал в секции Стоимостные условия",1024);
	oversizeChk("limit_Сумма - описание комиссии","Описание комиссий в секции Стоимостные условия",1024);
	oversizeChk("pay_int","Порядок уплаты процентов в секции Стоимостные условия",2000);
	oversizeChk("main Forbiddens","Запрещенная цель кредитования",4000);
	return valid;
}
function oversizeChk(fname, ftitle, size){
	$('textarea[name^="'+fname+'"]').each(function( i, val ) {
		if($(this).val().length > size){
			addErrorMsg("Превышен размер текстового поля "+ftitle+" ("+size+" символов). Сократите содержание чтобы сохранить заявку");
			valid=false;
		}
	});
}
function addErrorMsg(msg){
	document.getElementById("errorMessage").style.display = "inline";
	document.getElementById("errorMessage").innerHTML=document.getElementById("errorMessage").innerHTML+msg+'<br /><br />';
}
/**
 * check for validity of variables on Complete Operation button click
 * @return true, if check is successfull
 */
function validate_variables(){
	try{
		if (!flagValidateVariablesOnOperationCompletion) return true;
		
		// проверим сначала заполнение полей из родительского лимита  
		valid = true;
		try {
			// нужно ли проверять? на том ли этапе?
			if (document.getElementById("checkParentStage").value == "true") {
				valid = checkParentAllowedFilled();
				if (!valid) {
					document.getElementById("errorMessage").style.display = "inline";
					document.getElementById("errorMessage").innerHTML='Параметры не соответствуют родительскому Лимиту. Необходимо либо привести параметры к значениям, указанным в Лимите (Сублимите), либо установить признак "индивидуальные условия"<br /><br />';
					return valid;  // false
				}
			}
		} catch (Err) {	}
		
		document.getElementById("errorMessage").innerHTML=''
		valid = true;
		valid = checkRequiredFields();
		
		var ctarray = document.getElementsByName("ContractorTypeValidateDiv");
		for (var i=0;i<ctarray.length;i++){
			nobody_checked = true;
			objDiv = ctarray[i].parentElement;
			var input_array = objDiv.getElementsByTagName("input");
			for (var j=0;j<input_array.length;j++){
				if(input_array[j].checked==true){
					nobody_checked=false;
				}
			}
			if(nobody_checked){
				document.getElementById("errorMessage").style.display = "inline";
				document.getElementById("errorMessage").innerHTML=document.getElementById("errorMessage").innerHTML+
				'Необходимо указать тип контрагента<br /><br />';
				valid=false;
				break;
			}
		}
//		var ctarray = document.getElementsByName("Категория качества ссуды");
//		for (var i=0;i<ctarray.length;i++){
//			var objSel = ctarray[i];
//			if (objSel.selectedIndex==0){
//				document.getElementById("errorMessage").style.display = "inline";
//				document.getElementById("errorMessage").innerHTML=document.getElementById("errorMessage").innerHTML+
//				'Необходимо заполнить поле "Категория качества ссуды: не ниже"<br /><br />';
//				valid=false;
//			}
//			
//		}
		return valid;
	} catch(err){
		return true;
    }
}


/**
 * Validate data on server
 * @param valid whether all the beforehand filled data are valid 
 */
function validateOnServer(valid) {
	var xml = getValidationData();
	var validateValuesMap = parseValidationData(xml);
	// проверяем правильность заполнения дат
	if (validateValuesMap.opportunityDateErrorMsg != "") {
		try {
			var insertLine = validateValuesMap.opportunityDateErrorMsg.replace(/END_OF_LINE/g,'<br /><br />');
			document.getElementById("errorMessage").style.display = "inline";
			document.getElementById("errorMessage").innerHTML = 
				document.getElementById("errorMessage").innerHTML + insertLine;
			return false;
		} catch (Err) {	}
	}
	// проверяем суммы лимита (сублимита) и сделки	
	validateValuesMap.upperLimitSumRoubles = $('upperLimitSumRoubles:first',xml).text();
	validateValuesMap.childrenSumsRoubles =  $('childrenSumsRoubles:first',xml).text();
	validateValuesMap.thisSumRoubles = 		 $('thisSumRoubles:first',xml).text();
	
	var upperLimitSumRoubles = toNumberFromString(validateValuesMap.upperLimitSumRoubles, 'money');
	var childrenSumsRoubles = toNumberFromString(validateValuesMap.childrenSumsRoubles, 'money');
	var thisSumRoubles = toNumberFromString(validateValuesMap.thisSumRoubles, 'money');
	var oldThisSumsRoubles = toNumberFromString(validateValuesMap.oldThisSumsRoubles, 'money');
	
	if (validateValuesMap.isOpportunity == 'true') {
		var newChildrenSum = childrenSumsRoubles - oldThisSumsRoubles + thisSumRoubles;
		var limitSumExceeded = upperLimitSumRoubles - newChildrenSum;	
		if (limitSumExceeded < 0 ) {
			if (valid)
				if (confirm("Сумма сделок " + toFormattedString(newChildrenSum,'money') + " руб., проводящихся  в рамках (суб)лимита, превышает его сумму " 
											+ toFormattedString(upperLimitSumRoubles,'money') + " руб. Всё равно сохранить?")) return true; 
				else return false;
			else {
				alert("Сумма сделок " + toFormattedString(newChildrenSum,'money') + " руб., проводящихся  в рамках (суб)лимита, превышает его сумму " 
						+ toFormattedString(upperLimitSumRoubles,'money') + " руб. Исправьте, пожалуйста, имеющиеся в форме ошибки");
				return false;
			}
		}
	} else {
		// посмотрим сумму для находящихся под этим лимиом (сублимитом) сделок
		var limitSumExceeded = thisSumRoubles - childrenSumsRoubles;
		if (limitSumExceeded < 0 ) {
			if (valid) 
				if (confirm("Сумма сделок " + toFormattedString(childrenSumsRoubles,'money') + " руб., проводящихся  в рамках (суб)лимита, превышает его сумму " 
											+ toFormattedString(thisSumRoubles,'money') + " руб. Всё равно сохранить?")) return true; 
				else return false;
			else {
				alert("Сумма сделок " + toFormattedString(childrenSumsRoubles,'money') + " руб., проводящихся  в рамках (суб)лимита, превышает его сумму " 
						+ toFormattedString(thisSumRoubles,'money') + " руб. Исправьте, пожалуйста, имеющиеся в форме ошибки");
				return false;
			} 
		}
	}
	return true;
}

/**
 * get validataion data, making request to server (to get the newest data, to avoid problems with concurrency)
 * @return xml that includes all needed data
 */
function getValidationData() {
	//получим параметры даты valid_to
	mdtask_period = 0; mdtask_date = "";
	use_period = 0; use_date = "";
	try { mdtask_date = document.getElementById("mdtask_date").value; } catch (Err) {}
	try { mdtask_period = document.getElementById("mdtask_period").value; } catch (Err) {}
	try { use_date = document.getElementById("termOfUse").value; } catch (Err) {}
	try { use_period = document.getElementById("op_use_period").value; } catch (Err) {}

	var paramMap = new Object();
	paramMap.idTask = $('#taskId_param').val();
	paramMap.limit_sum = $('#limit_sum').val();
	paramMap.currency =  $('#currency_Sum').val();
	paramMap.valid_to_param = mdtask_date;
	paramMap.mdtask_period = mdtask_period;
	paramMap.use_date = use_date;
	paramMap.use_period = use_period;
	
	var strReturn = "";
	$.ajax({
	      url: "ajax/validateOnSave.do",
	      type: "POST",
	      data: paramMap,
	      async: false,
	      success: function(xml){strReturn = xml;}
   });
   return strReturn;
}

/**
 * Parse form validation data, stored in xml (look getValidationData())    
 * @return composite object (map in the current implementation)
 */
function parseValidationData(xml) {
	var validateValuesMap = new Object();
	validateValuesMap.upperLimitSumRoubles =    $('upperLimitSumRoubles:first',xml).text();
	validateValuesMap.childrenSumsRoubles =     $('childrenSumsRoubles:first',xml).text();
	validateValuesMap.thisSumRoubles = 		    $('thisSumRoubles:first',xml).text();
	validateValuesMap.oldThisSumsRoubles = 	    $('oldThisSumsRoubles:first',xml).text();
	validateValuesMap.isOpportunity = 		    $('opportunity:first',xml).text();
	validateValuesMap.opportunityDateErrorMsg = $('opportunityDateErrorMsg:first',xml).text();
	return validateValuesMap;
}

/**
 * check for the filling of required fields on form save 
 * @return whether the check is passed or not
 * adds messages, saying, which required fields are not filled
 */
function checkRequiredFields() {
	globalFilled = true;
	for (pt in attributesHash) {
		if (!exclusion(pt)) { 
			try {
				globalFilled = checkField(pt, true) && globalFilled;
			} catch (err) { alert("error: " + err.description);}
		}
	}
	// should add check for errorMessageLength, because some of the exclusion() fields adds messages, but return true
	return globalFilled && (document.getElementById("errorMessage").innerHTML == '');
}

/**
 * Checks whether field is filled
 * @param p field name to check 
 * @param showMsg whether generate message, if not passed check, or not
 * @return true, if filled or shouldn't be checked (read-only or class 'nonverified'), false, if not filled
 */
function checkField (p, showMsg) {
	alertEnabled = false;
	resFilled = true;
	filled = true;
	isCheckBox = false; checkBoxChecked = false; checkBoxShouldBeChecked = false;
	isRadio = false; radioChecked = false; radioShouldBeChecked = false;
	var found = document.getElementsByName(p);
	if (found.length > 0) {  // found. If not, don't check at all
		// all values (always treat as array) should be filled 
		try {
			if ((found[0].tagName.toLowerCase() == "input") && (found[0].type.toLowerCase() == "checkbox")) isCheckBox = true;
			if ((found[0].tagName.toLowerCase() == "input") && (found[0].type.toLowerCase() == "radio")) isRadio = true;
		} catch (Err) {}

		i = 0;
		while (filled && (i<found.length)) {
			var foundElement = found[i];
			curFilled = false;
			if (isCheckBox) {
				try { if (foundElement.checked) checkBoxChecked = true; } catch (Err ) {}
				// check visible checkboxes
				try {
					if(!(/nonverified/.test(foundElement.className))) checkBoxShouldBeChecked = true; 
				} catch (Err) {alert(Err.description);}
			} else if (isRadio) {
				try { if (foundElement.checked) radioChecked = true; } catch (Err ) {}
				// check visible radio buttons
				try {
					if(!(/nonverified/.test(foundElement.className))) radioShouldBeChecked = true; 
				} catch (Err) {alert(Err.description);}
			} else {
				// do not check the invisible elements
				try {
					if(/nonverified/.test(foundElement.className)) {
						if (alertEnabled) alert('NOT VISIBLE ' + p + ':' + attributesHash[p]);
						curFilled = true;
					}	
				} catch (Err) {}
				
				if (!(p in readOnlyExclusions)) {
					// when readOnly, don't need to check
					// do not check for readOnly flag for exclusions (can't find better solution thus far)
					try {
						if(foundElement.readonly == true) {
							if (alertEnabled) alert('READONLY ' + p + ':' + attributesHash[p]);
							curFilled = true;
						}	
					} catch (Err) {}
					try {
						if(foundElement.readOnly == true) {
							if (alertEnabled) alert('READONLY ' + p + ':' + attributesHash[p]);
							curFilled = true;
						}	
					} catch (Err) {}
				}
				if((foundElement.value != '') && (foundElement.value != ' ') && (foundElement.value != '-1')) {
					curFilled = true;
					if (alertEnabled) alert('FILLED ' + p + ':' + attributesHash[p]);
				}
				// one of the elements with this name is not filled!
				if (!curFilled) filled = false;
			}
			i++;
		} // end while
	} else {
		// not found. absent
		filled = true;  
		if (alertEnabled) alert('ABSENT ' + p + ':' + attributesHash[p]);
	}
	if (alertEnabled) {
		alert('checkBoxChecked:' + checkBoxChecked);
		alert('checkBoxShouldBeChecked:' + checkBoxShouldBeChecked);
		alert('radioChecked:' + radioChecked);
		alert('radioShouldBeChecked:' + radioShouldBeChecked);
	}
	if (isCheckBox && (!checkBoxChecked) && (checkBoxShouldBeChecked)) {				
		if (alertEnabled) alert('NOT FILLED CHECKBOX ' + p + ':' + attributesHash[p]);
		resFilled = false;
		if (showMsg) {
			document.getElementById("errorMessage").style.display = "inline";
			document.getElementById("errorMessage").innerHTML=document.getElementById("errorMessage").innerHTML+
				'Не установлен ни один из флажков "' + attributesHash[p] + '"<br /><br />';
		}
	}				 
	if (isRadio && (!radioChecked) && (radioShouldBeChecked)) {				
		if (alertEnabled) alert('NOT FILLED RADIO ' + p + ':' + attributesHash[p]);
		resFilled = false;
		if (showMsg) {
			document.getElementById("errorMessage").style.display = "inline";
			document.getElementById("errorMessage").innerHTML=document.getElementById("errorMessage").innerHTML+
				'Не выбрана ни одна радиокнопка "' + attributesHash[p] + '"<br /><br />';
		}
	}
	if((!isCheckBox) && (!isRadio) && (!filled)) {
		if (alertEnabled) alert('NOT FILLED ' + p + ':' + attributesHash[p]);
		resFilled = false;
		if (showMsg) {
			document.getElementById("errorMessage").style.display = "inline";
			document.getElementById("errorMessage").innerHTML=document.getElementById("errorMessage").innerHTML+
				'Не заполнено поле "' + attributesHash[p] + '"<br /><br />';
		}
	}
	return resFilled;
}

/**
 * If the fields in this version shouldn't be checked (even if they are treated as required ones) 
 * @return true if the field should not be checked, false if should
 */
function exclusion(name) {
	if (name in exclusionHash) return true;
	// check  type of guaranteeType
	if (name in guaranteeTypeHash) {
	  try {
		  if (document.getElementById("isGuarantee").value == 'YES') return false; // should check
	  } catch (Err) {}
	  return true;
	}
	
	// dates of validity of opportunity. At least one should be filled
	if ((name == 'Дата действия сделки') ||(name == 'Срок действия сделки')) {
		res = checkField('Срок действия сделки', false) || checkField('Дата действия сделки', false);
		if (!res && (name == 'Дата действия сделки')) {
			// выведем только один раз, для даты действия. Иначе будет задвоение информации.
			document.getElementById("errorMessage").style.display = "inline";
			document.getElementById("errorMessage").innerHTML=document.getElementById("errorMessage").innerHTML+
				'Должно быть заполнено одно из полей: "' + attributesHash['Срок действия сделки'] + '" или "' + attributesHash['Дата действия сделки'] + '"<br /><br />';
		}
		// сообщение о том, что не заполнено, уже выведено. Незачем повторяться.
		return true;
	}

	// dates of usage of opportunity. At least one should be filled
	if ((name == 'Дата использования сделки') ||(name == 'Период использования сделки')) {
		res = checkField('Период использования сделки', false) || checkField('Дата использования сделки', false);
		if ((!res) && (name == 'Дата использования сделки')) {
			// выведем только один раз, для даты использования. Иначе будет задвоение информации.
			document.getElementById("errorMessage").style.display = "inline";
			document.getElementById("errorMessage").innerHTML=document.getElementById("errorMessage").innerHTML+
				'Должно быть заполнено одно из полей: "' + attributesHash['Период использования сделки'] + '" или "' + attributesHash['Дата использования сделки'] + '"<br /><br />';
		}
		// сообщение о том, что не заполнено, уже выведено. Незачем повторяться.
		return true;
	}
	
	if(name == 'exchangerate') {
		try {
			if (document.getElementById("exchangeratediv").style.display == 'none') return true;
		} catch (Err) {}
		return false;
	}
	
	// для секции проверить, что заполняется, только если стоит 'Коллегиальный'
	if(name == 'assigneeAuthority') {
		try {
			//alert('check assigneeAuthority!');
			//alert('collegial checked: ' + document.getElementById("collegial").checked);
			if (document.getElementById("collegial").checked) return false;			
		} catch (Err) {}
		return true;
	}
	if(name == 'CcQuestionType') {
		try {
			if (document.getElementById("collegial").checked) return false;
		} catch (Err) {}
		return true;
	}
	return false;	
}	


function isInAllowedHash(name, value) {
	found = false;
//	alert("isInAllowedHash:" + name + "_" + value)
	// если есть _ANY, значит, допустимо любое значение (в родительском лимите оно просто не задано)
	for(var i = 0; i<checkParentAllowedArray.length; i++)
		if (checkParentAllowedArray[i] == name + "_ANY") {
			return true;
		}
	
	for(var i = 0; i<checkParentAllowedArray.length; i++)
		if (checkParentAllowedArray[i] == name + "_" + value) {
			return true;
		}
	return false;		
}


function disallowParent(htmltag, flag) {
	try {
		htmltag.style.backgroundColor="#E9967A";
		addClass(htmltag, "parentDisallowed");
	} catch (Err) {} 
//	if (flag)
//		alert('Параметры не соответствуют родительскому Лимиту. Необходимо либо привести параметры к значениям в Лимите (Сублимите), либо установить признак «индивидуальные условия»');
} 

function allowParent(htmltag) {
	try {
		removeClass(htmltag, "parentDisallowed");
		htmltag.style.backgroundColor="#FFFFFF";
	} catch (Err) {}
}

function addClass(o, c){
    try {
    	var re = new RegExp("(^|\\s)" + c + "(\\s|$)", "g")
    	if (re.test(o.className)) return
    	o.className = (o.className + " " + c).replace(/\s+/g, " ").replace(/(^ | $)/g, "")
	} catch (Err) {}
}
  
function removeClass(o, c){
    try {
    	var re = new RegExp("(^|\\s)" + c + "(\\s|$)", "g")
    	o.className = o.className.replace(re, "$1").replace(/\s+/g, " ").replace(/(^ | $)/g, "")
	} catch (Err) {}
}

/* check whether the value is allowed for input for this variable (according to filling in the parent limit)  */
function checkParentAllowed(htmltag) {
	// Проверить на наличие вышестоящего лимита
	var inLimit = document.getElementById("productInLimit");
	if ((inLimit != null) && (inLimit.checked == false)) return;
	// Проверить заполнение флага Индивидуальные условия
	var indcondition = document.getElementById("indcondition");
	if ((indcondition == null) || (indcondition.checked == true)) return;

	/* проверим целевое назначение */
	if (htmltag.name == "target")
		if (htmltag.value != null)
			if (!isInAllowedHash("target", htmltag.value) ) {
				if (htmltag.checked) disallowParent(htmltag, true);
				else allowParent(htmltag);
			}
	// проверим валюты
	if (htmltag.name == "main_currencyList")
		if (htmltag.value != null)
			if (!isInAllowedHash("main_currencyList", htmltag.value) ) {
				if (htmltag.checked) disallowParent(htmltag, true);
				else allowParent(htmltag);
			}
	// Категория сделки – проектное финансирование
	if (htmltag.name == "main_projectFin_chk")
		if (!isInAllowedHash("main_projectFin_chk", htmltag.checked)) disallowParent(htmltag, true);
		else allowParent(htmltag);	
	
	// Вид продукта (сделки)
	if (htmltag.name == "Вид кредитной сделки") {
		if (!isInAllowedHash("Вид кредитной сделки", htmltag.value)) {
			disallowParent(document.getElementById('span_credit_opp_type'), true);
		} else allowParent(document.getElementById('span_credit_opp_type'));
	}
}

/**
 * Проверяем, позволены ли введенные значения для сделки, наследованные от родительского лимита
 * @return true, если допустимы, false - если имеются недопустимые значения
 */
function checkParentAllowedFilled(){
	// только для сделки!
	if ($('#tasktype').val()!= "Сделка") { return true; }
	
	// readOnly. 
	try {
		if (document.getElementById("readOnlyOpportunityParam").value == 'true') { return true; }
	} catch (Err) {}
	
	// Проверить на наличие вышестоящего лимита
	var inLimit = document.getElementById("productInLimit");
	bInLimit = true;
	if ((inLimit != null) && (inLimit.checked == false)) bInLimit = false;

	// Проверить заполнение флага Индивидуальные условия
	var indcondition = document.getElementById("indcondition");
	bIndcondition = true;
	if ((indcondition != null) && (indcondition.checked == false)) bIndcondition = false;

	var allPermitted = true;

	// проверим целевое назначение
	var targets = document.getElementsByName("target");
	for(i = 0; i < targets.length; i++) {
		var elem = targets[i];
		if (elem.value != null)
			if (elem.checked && (!isInAllowedHash("target", elem.value)) && (!bIndcondition && bInLimit)) {
				allPermitted = false;
				// заодно раскрасим
				disallowParent(elem, false);
			} else allowParent(elem);
	}

	// проверим валюты
	var currencyList = document.getElementsByName("main_currencyList");
	for(i = 0; i < currencyList.length; i++) {
		var elem = currencyList[i];
		if (elem.value != null)
			if (elem.checked && (!isInAllowedHash("main_currencyList", elem.value)) && (!bIndcondition && bInLimit)) {
				allPermitted = false;
				// заодно раскрасим
				disallowParent(elem, false);
			} else allowParent(elem);
	}
	
	// проверим Проектное финансирование
	try {
		var main_projectFin = document.getElementById("main_projectFin_chk");
		if ((!isInAllowedHash("main_projectFin_chk", main_projectFin.checked)) && (!bIndcondition && bInLimit)) {
			allPermitted = false;
			// заодно раскрасим
			disallowParent(main_projectFin, false);
		} else allowParent(main_projectFin);
	} catch (Err) {	}
	
	// Вид продукта (сделки)
	try {
		var productType = document.getElementById("Вид кредитной сделки");
		if ((!isInAllowedHash("Вид кредитной сделки", productType.value)) && (!bIndcondition && bInLimit)) {
			allPermitted = false;
			// заодно раскрасим
			disallowParent(document.getElementById('span_credit_opp_type'), false);
		} else allowParent(document.getElementById('span_credit_opp_type'));
	} catch (Err) {	}
	
	// игнорируем значения полей
	if (bIndcondition) return true;
	if (!bInLimit) return true;
	
	return allPermitted;
}
function validateIndrateUniq() {//Значения индикативных ставок в рамках одного периода уникальны
	var indrateUniq = true;
	$('.percentFactID').each(function() {
		var id_period = $(this).val();
		for(var i=0;i<$('select[name=indRate'+id_period+']').size();i++)
			for(var j=i+1;j<$('select[name=indRate'+id_period+']').size();j++)
				if($('select[name=indRate'+id_period+']')[i].value == $('select[name=indRate'+id_period+']')[j].value
				    && $('select[name=indRate'+id_period+']')[i].value!='')
					indrateUniq = false;
	});
	if(!indrateUniq)
		addErrorMsg("Значения индикативных ставок в рамках одного периода должны быть уникальны");
	var indrateProductUniq = true;
	for(var i=0;i<$('select[name=indRate]').size();i++)
		for(var j=i+1;j<$('select[name=indRate]').size();j++)
			if($('select[name=indRate]')[i].value == $('select[name=indRate]')[j].value
				&& $('select[name=indRate]')[i].value!='')
				indrateProductUniq = false;
	if(!indrateProductUniq)
		addErrorMsg("Значения индикативных ставок по сделке в целом должны быть уникальны");
	return indrateUniq && indrateProductUniq;
}
