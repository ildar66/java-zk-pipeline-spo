function updateRelatedData(attrname) {
	//alert(attrname);
	try {
		//скрыть или показать секцию Уполномоченный орган (на какой КК можем передать)
        if($('#collegial').is(':checked')) {$('.cctr').show();} else {$('.cctr').hide();}
        //как активировать, деактивировать кнопки передать на КК
        if (document.getElementById('btnCC')==null) return;//если нет кнопки, то нечего и решать
		var mayExport2cc = document.getElementById('Акцепт передачи Уполномоченному органу/ лицуt')==null
			||document.getElementById('Акцепт передачи Уполномоченному органу/ лицуt').checked;
        btnCC(mayExport2cc && $('#collegial').is(':checked'));
        //VTBSPO-93
        if(attrname=='Требуется повторное проведение контроля тех. исполнимости' && $('input[id="Требуется повторное проведение контроля тех. исполнимостиt"]').prop("checked")){
        	$('input[id="Требуется экспертиза подразделения Внутреннего контроляt"]').prop("checked",false);
        	$('input[id="Требуется экспертиза подразделения Внутреннего контроляf"]').prop("checked",true);
        }
        if(attrname=='Требуется экспертиза подразделения Внутреннего контроля' && $('input[id="Требуется экспертиза подразделения Внутреннего контроляt"]').prop("checked")){
        	$('input[id="Требуется повторное проведение контроля тех. исполнимостиt"]').prop("checked",false);
        	$('input[id="Требуется повторное проведение контроля тех. исполнимостиf"]').prop("checked",true);
        }
        if($('input[id="Требуется экспертиза подразделения Внутреннего контроляt"]').prop("checked") || $('input[id="Требуется повторное проведение контроля тех. исполнимостиt"]').prop("checked")){
        	btnCC(false);
        }
	} catch (Err) {
		//console.error(Err.message);
	}
}
function btnCC(enabled){
	document.getElementById('btnCC').disabled = !enabled;
	document.getElementById('btnRegister').disabled = enabled;
	if (enabled){ 
		$('#btnRegister').addClass('disabled');
		$('#btnCC').removeClass('disabled');
	} else {
		$('#btnRegister').removeClass('disabled');
		$('#btnCC').addClass('disabled');
	}
}
function conclusionValid() {
    $('#errorMessage').text('');
    var conclusion_valid = true;
    //«Желаемая дата заседания»,
    $('input[name^="planmeetingDate"]').each(function() {
        if($(this).val().length == 0){
            conclusion_valid = false;
            addErrorMsg('Необходимо заполнить поле Желаемая дата заседания');
        }
    });
    $('select[name^="assigneeAuthority"]').each(function(i) {
        var assigneeAuthorityId = $(this).val();
        var assigneeAuthorityName = $(this).find("option[value='"+assigneeAuthorityId+"']").text();
        var errMsg = '';
        if(assigneeAuthorityId.length == 0){
            errMsg = "Необходимо заполнить поле Уполномоченный орган";
            conclusion_valid = false;
        }
        if($('select[name^="CcQuestionType"]')[i].value.length == 0) {
            conclusion_valid = false;
            if (errMsg=='') errMsg = assigneeAuthorityName;
            errMsg += ' (заполните Классификация вопроса';
        }
        if($('select[name^="creditDecisionProject"]')[i].value.length == 0) {
            conclusion_valid = false;
            if (errMsg=='') errMsg = assigneeAuthorityName;
            if (errMsg.indexOf('(') !== -1) errMsg += ', ПКР';
            else errMsg += ' (заполните ПКР';
        }
        if (errMsg.indexOf('(') !== -1) errMsg += ')';
        if (errMsg != '')
            addErrorMsg(errMsg);
    });
	return conclusion_valid;
}