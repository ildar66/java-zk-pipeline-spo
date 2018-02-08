// показывать ли транши или нет.
function showHideTranches(){
	var isLimitIssue = document.getElementById('isLimitIssue');
    var isDebtLimit = document.getElementById('isDebtLimit');
    var limitIssueChk = false;
    var debtLimitChk = false;
    if (isLimitIssue != null) limitIssueChk = isLimitIssue.checked;
	if (isDebtLimit != null) debtLimitChk = isDebtLimit.checked;
    	if (limitIssueChk || debtLimitChk){
			$('#li_frame_trance').show();
			$("li.ng-scope:contains('График использования')").show();
		} else {
			$('#li_frame_trance').hide();
			$("li.ng-scope:contains('График использования')").hide();
		}
}
/** зачем нужна эта функция? */
function syncSums(chosenValue) {
	if (chosenValue == 'limitIssue') {
		if (!(document.getElementById('isDebtLimit').checked)) { 
		   		document.getElementById('limit_sum').value = document.getElementById(chosenValue).value;
		}
	} else {
		document.getElementById('limit_sum').value = document.getElementById(chosenValue).value;
	}
}
/**
* Enable \ disable sums of credit, debtLimit and limitIssue
**/
function enableDisableSums() {
    var limitIssueChk = $('#isLimitIssue').prop('checked');//Кредитная линия с лимитом выдачи
    var debtLimitChk = $('#isDebtLimit').prop('checked');//Кредитная линия с лимитом задолженности
    //скопировать значения
    if(!limitIssueChk && !debtLimitChk && $('#limit_sum').val()==''){$('#limit_sum').val($('#limitIssue').val());}
    if(limitIssueChk && $('#limitIssue').val()==''){$('#limitIssue').val($('#limit_sum').val());}
    //подсветить, скрыть div и очистить скрытые поля
    if(limitIssueChk){$('#limitIssue_div').show();}else{$('#limitIssue_div').hide();$('#limitIssue').val('')}
    if(debtLimitChk){$('#debtLimit_div').show();}else{$('#debtLimit_div').hide();$('#debtLimit').val('')}
    if(limitIssueChk||debtLimitChk){$('#limit_sum_div').hide();$('#limit_sum').val('')}else{$('#limit_sum_div').show();}
    	
	showHideTranches();
}
/**
* Change values determined by product family (when choosing product)
**/
function changeProductValues() {
	showHideProductValues();
	enableDisableCreditFields();
}
function showHideProductValues() {
	var selectedProduct = null;
	var product = null;
	try {
		selectedProduct = document.getElementById("Вид кредитной сделки");
		product = selectedProduct.options[selectedProduct.selectedIndex].value;
	} catch (Err) {}

	try {
		// Гарантии
		if (isProductInFamily(product, "Банковские гарантии"))  enableDisableGuaranteeFields(true);
		else enableDisableGuaranteeFields(false);

		showHideCreditFields();
	} catch (err) {}
	try {
		if(product == "-1"){
			$('#opIrregularSpan').hide();
		} else {
			$('#opIrregularSpan').show();
		}
	} catch (Err) {}
	
}
function OnOpIrregularClick(){
	fieldChanged(this);
	if($('#opIrregular').prop('checked')){$('#product_name_tr').show();}else{$('#product_name_tr').hide();}	
	try {
		selectedProduct = document.getElementById("Вид кредитной сделки");
		productname = selectedProduct.options[selectedProduct.selectedIndex].text;
		$('#product_name').val(productname);
	} catch (Err) {}
}


/**
* Checks whether product is of the given family 
**/
function isProductInFamily(product, family) {
	try {
		var productsOfFamily = new Array();
		productsOfFamily = document.getElementsByName('opPrFamily_' + family);
		if (productsOfFamily != null)
			for(i=0; i<productsOfFamily.length; i++) {
				if (productsOfFamily[i].value == product) return true;
			}
	} catch (Err) {}
	return false;
}

function enableDisableGuaranteeFields(isGuaranteeChk) {
	if(!isGuaranteeChk) {
		// do not show fields 
		document.getElementById('guaranteeFieldsContract').style.display = 'none';
		document.getElementById('guaranteeFieldsWarrantyItem').style.display = 'none';
		document.getElementById('guaranteeFieldsBeneficiary').style.display = 'none';
		document.getElementById('guaranteeFieldsBeneficiaryOGRN').style.display = 'none';
		if (document.getElementsByName('Контракт')[0] !== undefined)
			document.getElementsByName('Контракт')[0].value = '';
		if (document.getElementsByName('Бенефициар')[0] !== undefined)
			document.getElementsByName('Бенефициар')[0].value = '';
		if (document.getElementsByName('Предмет_гарантии')[0] !== undefined)
			document.getElementsByName('Предмет_гарантии')[0].value = '';
		document.getElementById('isGuarantee').value = 'NO';				
	} else {
		// show fields
		document.getElementById('guaranteeFieldsContract').style.display = '';
		document.getElementById('guaranteeFieldsWarrantyItem').style.display = '';
		document.getElementById('guaranteeFieldsBeneficiary').style.display = '';
		document.getElementById('guaranteeFieldsBeneficiaryOGRN').style.display = '';
		document.getElementById('isGuarantee').value = 'YES'; 
	}
}

function enableDisableCreditFields() {
	if ($("select[id='Вид кредитной сделки']").size() > 0)
		selectedProductId = $("[id='Вид кредитной сделки']").val();
	else
		selectedProductId = $("#currentProductId").val();
	//пустое = -1, для него тоже шлём запрос чтобы логика была в одном месте
	$.getJSON('ajax/actualid.html',{productid: selectedProductId},
			function(ans){
		if(ans.editDebt){$('#isDebtLimit').prop('checked', false);}
		if(ans.editLimit){$('#isLimitIssue').prop('checked', false);}
		enableDisableSums();
	});
}
function showHideCreditFields() {
	if ($("select[id='Вид кредитной сделки']").size() > 0)
		selectedProductId = $("[id='Вид кредитной сделки']").val();
	else
		selectedProductId = $("#currentProductId").val();
	//пустое = -1, для него тоже шлём запрос чтобы логика была в одном месте
	$.getJSON('ajax/actualid.html',{productid: selectedProductId},
		function(ans){
		    if(ans.showDebt){$('#opPrmIsDebtLimit').show();}else{$('#opPrmIsDebtLimit').hide();$('#isDebtLimit').prop('checked', false);}
		    if(ans.showLimit){$('#opPrmIsLimitIssue').show();}else{$('#opPrmIsLimitIssue').hide();$('#isLimitIssue').prop('checked', false);}
		    if(ans.editDebt){$('#isDebtLimit').prop("disabled", false);$('#isDebtLimitRO').val('n');}else{$('#isDebtLimit').prop('checked', true);$('#isDebtLimit').prop("disabled", true);$('#isDebtLimitRO').val('y');}
		    if(ans.editLimit){$('#isLimitIssue').prop("disabled", false);$('#isLimitIssueRO').val('n');}else{$('#isLimitIssue').prop('checked', true);$('#isLimitIssue').prop("disabled", true);$('#isLimitIssueRO').val('y');}
		    enableDisableSums();
		});
}

function changeRelatedData(creditDeal) {
	idHidden = creditDeal.id + '_hidden';
	if (creditDeal.checked) {
		// make available to users
		// поменяем значние hidden-поле
		document.getElementById(idHidden).value=creditDeal.value;
	} else {
		// disable to users
		// поменяем значние hidden-поле
		document.getElementById(idHidden).value='';
	}
}

function clearDates(id1, id2) {
	wasInput = document.getElementById(id1);
	if ((wasInput != null) && (wasInput.value != '')) {
		document.getElementById(id2).value='';				
	}
}

function syncCurrency(chosenValue) {
	var currencySum = document.getElementById('currency_Sum');
	var currencyLimitIssue = document.getElementById('currency_LimitIssue'); 
	var currencyDebtLimit = document.getElementById('currency_DebtLimit');
	
	if (currencySum != null) currencySum.selectedIndex = chosenValue;
	if (currencyLimitIssue != null) currencyLimitIssue.selectedIndex = chosenValue;
	if (currencyDebtLimit != null)  currencyDebtLimit.selectedIndex = chosenValue;
}
/* parse Date in russian format */
function parseDateRUS(str) {
	//  0123456789
	// "31.12.2010"				
	substr = str.substring(0,2);
	dd = parseInt(substr, 10);
	substr = str.substring(3,5);
	mm = parseInt(substr, 10);
	substr = str.substring(6,10); 
	yyyy = parseInt(substr, 10);
	mydate = new Date(yyyy, mm - 1, dd);
	return mydate;
}

/* converts date to russian format */
function convertToRussianFormat(date) {
	str = "";
	dd = date.getDate();
	if (dd <10) str += "0" + dd + ".";
	else  str += dd + ".";
	mm = date.getMonth() + 1;
	if (mm <10) str += "0" + mm + ".";
	else  str += mm + ".";
	str += date.getFullYear();
	return str;
} 
