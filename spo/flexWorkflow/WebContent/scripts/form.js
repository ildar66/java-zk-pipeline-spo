var signDataObj = null;


function signUrl(unid) {
	try {
		mdSignDataObject.initSign(true, false, cryptoIssuers);
	} catch (err) {
		return "err";
	}
	try {
		var fileUrl = window.location.protocol+'//'+window.location.host+'/ProdflexWorkflow/download.do?unid='+unid;
		return mdSignDataObject.certSignURL(fileUrl);
	} catch (err) {
		alert("Ошибка при установке электронной подписи.\nЭлектронная подпись не будет поставлена.\n\n'" + ((err.description == undefined) ? err : err.description) + "', stack '" + err.stack + "'");
		return "err";
	}
}
function signFile() {
	if ($('#url').val()!='' || $('#IS_EXTERNAL').val()==1) {
		return;
	}
	try {
		mdSignDataObject.initSign(true, false, cryptoIssuers);
	} catch (err) {
		return "err";
	}
	try {
		var filePath = $('#attachment').val();
		var sign = mdSignDataObject.certSignFile(filePath);
		$('#sign').val(sign);
	} catch (err) {
		alert("Ошибка при установке электронной подписи.\nЭлектронная подпись не будет поставлена.\n\n'" + ((err.description == undefined) ? err : err.description) + "', stack '" + err.stack + "'");
		return "err";
	}
}
function signDoc() {
	if($('#attachtable').find('input[name*="unid"]:checkbox:checked').size() == 0){
		alert('Ни один документ не выбран');
		return;
	}
    $('#attachtable').find('input[name*="unid"]:checkbox:checked').each(function(index) {
        var unid = $(this).val();
        var sign = signUrl(unid);//вызываем activeX для подписи
        if(sign!="err"){
            $.post('ajax/signAttach.do',{unid: unid, sign:sign},OnAcceptDoc);
        }
    });
}
/**
 * Утвердить документ.
 */
function acceptDoc() {
	if($('#attachtable').find('input[name*="unid"]:checkbox:checked').size()>1){
		alert('Для утверждения необходимо выбрать один документ!');
		return;
	}
	$('#attachtable').find('input[name*="unid"]:checkbox:checked').each(function(index) {
		var unid = $(this).val();
		var sign = signUrl(unid);//вызываем activeX для подписи
		if(sign!="err"){
			$.post('ajax/acceptAttach.do',{unid: unid, sign:sign,mdtask:$('#mdtaskid').val()},OnAcceptDoc);
		}
	});
}
function downloadAttach(unid) {
	var fileUrl = window.location.protocol+'//'+window.location.host+'/ProdflexWorkflow/download.do?unid='+unid;
	document.body.style.cursor='pointer';
	if(checkSignFile(fileUrl, $('#signature'+unid).val())) {
		window.location.replace(fileUrl);
		//window.open(fileUrl, '_self');
	} else {
		//выдаем предупреждение
		if (confirm("Документ не подписан. Всё равно открыть?")) {
			//window.open(fileUrl, '_self');
			window.location.replace(fileUrl);
		}
	}
	document.body.style.cursor='pointer';
}
function on4ccClick(unid, mdtaskid) {
	$.post('ajax/attach4cc.do',{unid:unid, mdtaskid:mdtaskid, cc:$('#file4cc'+unid+mdtaskid).is(':checked')?"1":"0"});
}
/**
 * Удалить документ.
 */
function deleteDoc() {
	if($('#ID_INSTANCE').val() != '' && $('#attachtable').find('input[name*="unid"]:checkbox:checked').size()>0)
		var reason = window.prompt("Укажите причину удаления","");
    if ($('#ID_INSTANCE').val() != '' && reason == null)
        return;
	$('#attachtable').find('input[name*="unid"]:checkbox:checked').each(function(index) {
	    $.post('ajax/deleteAttach.do',{unid: $(this).val(),reason:reason,mdtask:$('#mdtaskid').val()},OnDeleteDoc);
	});
}
function OnAcceptDoc(text) {
	if (text=="OK") {
		refreshDocFrame();
	} else {
		alert(text);
	}
}
function OnDeleteDoc(text) {
	if (text=="OK") {
		refreshDocFrame();
	} else {
	    alert(text);
	}
}
function refreshDocFrameOpenSection() {
	docGroupToggle($('#docgroupid').val());
}
function refreshDocFrame() {
	$('#docs').load('frame/documents.jsp?mdtaskid='+$('#owner').val()+'&pupTaskId='+$('#ownertype').val()+'&mdtask='+$('#mdtask').val()+'&showOnlyNotExpired='+$('#showOnlyNotExpired').val(),
		refreshDocFrameOpenSection);
}
function docGroupToggle(id) {
	if($('#docGroupImg'+id).attr("alt")=="+"){// закрыта, открываем
		$(".attach"+id).show();
		$('#docGroupImg'+id).attr({src: "theme/img/collapse.jpg",alt: "-"});
	} else {// открыта, закрываем
		$(".attach"+id).hide();
		$('#docGroupImg'+id).attr({src: "theme/img/expand.jpg",alt: "+"});
	}
}
/**проверка подписи всех выбранных (флажки) файлов*/
function checkSignSelectedFiles() 	{
	var fileUrl, signature, checkResult;
	var signColl, unidColl;
	try {
		//Get unids collection and other collection with params
		unidColl = document.getElementsByName('unid');
		signColl = document.getElementsByName('signature');
		accSignColl = document.getElementsByName('acceptsignature');
		//isFiles in collection
		if (unidColl) {
			for(var i=0; i<unidColl.length; i++) {
				//file is checked (selected) for verification
				if (unidColl[i].checked) {
					fileUrl = window.location.protocol+'//'+window.location.host+'/ProdflexWorkflow/download.do?unid='+unidColl[i].value;
					signature = signColl[i].value;
					checkResult = checkSignFile(fileUrl, signature);
					
					//Show result of verification for file
					if (checkResult){
						//Correct files
						accSignResult = $('#'+unidColl[i].value+'accepted').size()<1 || checkSignFile(fileUrl, accSignColl[i].value);//валидна или документ не утвержден
						if(accSignResult){
							unidColl[i].parentNode.parentNode.style.backgroundColor="#E7F4DA";
							$('#sgnflg'+unidColl[i].value).text('валидна');
						} else {
							unidColl[i].parentNode.parentNode.style.backgroundColor="#FFDDDD";
							$('#sgnflg'+unidColl[i].value).text('подпись утвердившего не валидна');
						}
					} else {
						//Uncorrect files
						unidColl[i].parentNode.parentNode.style.backgroundColor="#FFDDDD";
						$('#sgnflg'+unidColl[i].value).text('подпись прикрепившего не валидна');
				    }
				}
			}
		}
	}
	catch(err)	{ alert('ERROR in signSelectedFiles(): '+err.description); }
}

/**Данная функция производит верификацию подписи
@param fileUrl - url подписанного файла
@param signature - значение подписи файла
@return (true/false)- результат верификации подписи файла*/
function checkSignFile(fileUrl, signature) {
	try {
		if (signature == '') {
			return false;
		}
		return mdSignDataObject.certVerifySignURL(signature, fileUrl);
	} catch(err) {
		alert("Ошибка при проверке электронной подписи.\n\n'" + ((err.description == undefined) ? err : err.description) + "'\n, stack '" + err.stack + "'");
	}
	return false;
}
function fireBtnFrame(frmID,btnID){
	try {
		var iframe = getNode(null, frmID, 'IFRAME');
		if (!iframe) return false;
		var btn = getNode(iframe.contentWindow.document, btnID);
		if (!btn) return false;
		btn.fireEvent('onclick');
		return false
	} catch (Err){
		alert('error in fireBtnFrame()\n'+Err.description);
		return false;
	}
	return false;
}
function showDlgAttAdd(url, title, prop){
	try{
		var dlg=window.showModalDialog(url,title,prop)
		if (dlg){
			var node=getNode(null,'showAllFrameID','IFRAME')
			var url=node.src
			node.src=''
			node.src=url
		}
		return false;
	} catch(Err) {
		alert('showDlgAttAdd\n'+Err.description)
	}
}
function docEdcUpAns(msg){
	if(msg!='OK'){
		$('#doc_error').html(msg);
		$('#doc_error').show();
	} else {
		$('#doc_error').hide();
		//обновить дату Синхронизировано с ЭДК:
		var currentdate = new Date();
        var minutes = currentdate.getMinutes();
        var hours = currentdate.getHours();
        if (hours   < 10) {hours   = "0"+hours;}
        if (minutes < 10) {minutes = "0"+minutes;}
		$('#edcUpdateDate').text('Синхронизировано с ЭДК: ' + $.datepicker.formatDate('dd.mm.yy', new Date())
            +" "+hours + ":" + minutes);
	}
}
first_edc_auto_update=true;
function docEdcUp(doctype,ownerid,auto){
	if(first_edc_auto_update || auto=='false'){
		$.post('ajax/docEdcUp.html',{doctype: doctype, ownerid:ownerid,auto:auto},docEdcUpAns);
	}
	first_edc_auto_update = false;
}

if (typeof String.prototype.endsWith !== 'function') {
    String.prototype.endsWith = function(suffix) {
        return this.indexOf(suffix, this.length - suffix.length) !== -1;
    };
}

/**
 * Возвращает полный путь одного выбранного файла из complexName, представляющего собой несколько путей файлов через запятую и пробел.
 * Пример URL из IE10: "C:\fakepath\a, C:.txt, C:\fakepath\b.txt, C:\fakepath\c.txt" (3 файла: "C:\fakepath\a, C:.txt", "C:\fakepath\b.txt", "C:\fakepath\c.txt") - резделитель в IE 10 ", ", в путях есть слово "fakepath"
 *s
 * @param complexName содержит несколько путей файлов через запятую и пробел в случае multifile-выбора
 * @param shortName наименование выбранного файла без пути, на которое должно заканчиваться одно из наименований в complexName (мультивыбор может быть внутри одного каталога. Следовательно имена файлов уникальны)
 * @returns полный путь одного выбранного файла
 */
function getOneFullNameByComplexName(complexName, shortName) {
	try {
		var SLASH = "\\";
		var TEST_END_WITH_NAME = SLASH + shortName; // из-за поиска // работает только в IE
		var ADD_POSITION = 2;

		var fullName = null; // содержит полный путь одного выбранного файла

		var prefix = complexName.substring(0, 3);
		var patternStr = "\, " + prefix; // запятая,пробел,любая нецифра,двоеточие,слеш

		var prevPosition = 0;
		var position = 0;

		position = complexName.indexOf(patternStr, position);

		while (position >= 0) {
			var testName = complexName.substring((prevPosition > 0 ? prevPosition + ADD_POSITION : prevPosition), position);

			var ends = testName.endsWith(TEST_END_WITH_NAME);
			if (ends) {
				fullName = testName;
				break;
			}
			prevPosition = position;
			position = complexName.indexOf(patternStr, position+1);
		}
		var testName = complexName.substring((prevPosition > 0 ? prevPosition + ADD_POSITION : prevPosition), complexName.length);
		if (testName.endsWith(TEST_END_WITH_NAME))
			fullName = testName;

		return fullName;
	} catch(err) {
		var errMessage = "js error when getOneFullNameByComplexName '" + ((err.description == undefined) ? err : err.description) + "'";
		alert(errMessage);
		throw errMessage;
	}
}

/**
 * Изменяет title и подсказки на странице, если браузер IE9 или ниже.
 */
function changeHeaders() {
	try {
	    var appVersion = navigator.appVersion;
	    var isOnlyOneFile = (appVersion.indexOf("MSIE 6.") != -1 || appVersion.indexOf("MSIE 7.") != -1 || appVersion.indexOf("MSIE 8.") != -1 || appVersion.indexOf("MSIE 9.") != -1);
	    if (isOnlyOneFile) {
	    	jQuery("#multipleFileHeader").html("Выбор файла:");
	    	jQuery("#multipleFiles").removeAttr("title");
	    }
	} catch (err) {
		alert("js error when changeHeaders '" + ((err.description == undefined) ? err : err.description) + "'");
	}
}

/**
 * Инициализирует компонент загрузки файлов, определяет его поведение на submit каждого конкретного элемента
 */
function addAttachInit() {
  $('#fileupload')
  .bind('fileuploadfail', function (e, data) { // удаление файла из списка
	  urlChanged(null); 
  })
  .bind('fileuploadstop', function (e) {
	  onAjaxStop();
  })
  .bind('fileuploadchange', function (e, data) {
	  try {
		  var obj = new Object();
		  obj.value="abc";
		  urlChanged(obj);
	  } catch(err) {
		  alert("js error when fileuploadchange: '" + ((err.description == undefined) ? err : err.description) + "'");
	  }
  })
  .bind('fileuploadsubmit', 
	  	function (e, data) {
			try {
				var trObj = jQuery(data.context); 
				
				jQuery("#loading").show();
				
				// TODO переделать на начитку по id
				var file_expdate = trObj.find("input[name=file_expdate]").val();
				var title = trObj.find("input[name=title]").val();
				var reason = trObj.find("input[name=reason]").val();
				var type_name = trObj.find("input[name=type_name]").val();
				
				var ownerObj = document.getElementById('owner');
				var owner = (ownerObj != null) ? ownerObj.value : "";
				
				var ownertypeObj = document.getElementById('owner_type');
				var owner_type = (ownertypeObj != null) ? ownertypeObj.value : "";
				
				var typeObj = document.getElementById('type');
				var type = (typeObj != null) ? typeObj.value : "";
				
				var groupObj = document.getElementById('group');
				var group = (groupObj != null) ? groupObj.value : "";
				
				var sign = "";
				var name = "";
				var url = "";
				
				var isOnlyUrl = (trObj.data('data').fileInput == undefined);
				if (!isOnlyUrl) {   // загрузка файла
					name = trObj.data('data').files[0].name; // содержит наименование выбранного файла без пути
					var complexName = jQuery(trObj.data('data').fileInput[0]).attr('origFileName'); // содержит несколько путей через запятую в случае multifile-выбора
					var fullName = getOneFullNameByComplexName(complexName, name); 

					var sign = getSignOfFile(fullName);
					trObj.find("input[name=sign]").first().val(sign); 
					
					// alert("complexName '" + complexName + "',\n shortName '" + name + "',\n fullName '" + fullName + "',\n sign '" + sign + "',\n sign.length '" + (sign != null ? sign.length : null ) + "'");
				} else {   // загрузка url без файла
					url = trObj.find("input[name=url]").val();
					
					// alert("url '" + url + "', name '" + name + "'");
				}
				
				data.formData = {file_expdate : file_expdate, 
						title : title,
                        reason : reason,
						url : url,
						name : name, 
						sign : sign,
						owner : owner,
						owner_type: owner_type,
						type: type,
						group: group,
						type_name: type_name	
				};				
			} catch(err) {
				var errMessage = "js error when fileupload.submit '" + ((err.description == undefined) ? err : err.description) + "'";
				alert(errMessage);
				throw errMessage;
			}
		}).fileupload({
		  	autoUpload : false,
			sequentialUploads: false,
			limitConcurrentUploads: 5,
			formData: {example: 'test'}
		});
}


/**
 * Добавляет url
 */
function addUrlRow() {
	var filesList = ['onlyUrlFileData'];
	$('#fileupload').fileupload('add', 
		{	files: filesList
		});
}

/**
 * Возвращает подпись файла по полному пути файла
 * 
 * @param filePath полный путь файла на файловой системе
 * @returns подпись файла по полному пути файла
 */
function getSignOfFile(filePath) {
	if($('#isEdsRequiredSPO').val() === 'false') {
		return null;
	}
	try {
		mdSignDataObject.initSign(true, false, cryptoIssuers);
	} catch (err) {
		return null;
	}
	try {
		return mdSignDataObject.certSignFile(filePath);
	} catch (err) {
		alert("Ошибка при установке электронной подписи.\nЭлектронная подпись не будет поставлена.\n\n'" + ((err.description == undefined) ? err : err.description) + "', stack '" + err.stack + "'");
		return null;
	}
}

/**
 * Выполняется, когда все ajax-запросы отработали
 */
function onAjaxStop() {
    $( document ).ajaxStop(function() {
    	try {
    	   var hasElementToUpload = (jQuery('table#attachTable tbody tr.template-upload').first().size() == 1);
    	   if (hasElementToUpload == false) {
	     	   $( "#loading" ).hide();
	     	  
	     	   var errCount = jQuery("span.error").size();
	     	   if (errCount > 0) {
	     		   jQuery("#errorFileList").show();
	     		   jQuery('#uploadButton').addClass('button_disabled');
	     	   }
	     	   else {
	     		  jQuery("#errorFileList").hide();
	     		  parent.jQuery.fancybox.close();
	     	   }
    	   }
    	} catch(err) {
    		alert("js error when onAjaxStop. err '" + ((err.description == undefined) ? err : err.description) + "'");
    	}
    }); 	
}

/**
 * Показывает текст "Вы не указали ни файл, ни URL. Необходимо выбрать документы и повторить загрузку", если не одного файла или url еще не добавлено
 * 
 * @param obj объект кнопки
 * @returns {Boolean} false
 */
function check_has_no_document(obj) {
	var hasNoDocument = jQuery(obj).hasClass('button_disabled');
	if (hasNoDocument)
		alert('Вы не указали ни файл, ни URL. Необходимо выбрать документы и повторить загрузку');
	else
		jQuery("#hiddenUploadButton").click();
	return false;
}

/**
 * При удалении url-строки или изменении текста в url проверить, есть ли еще заполненные url
 * 
 * @param obj
 */
function urlChanged(obj) {
	try {
		var hasNoDocument = jQuery('#uploadButton').hasClass('button_disabled');
		
		var hasValue = false;
		if (obj != null && obj.value != null && jQuery.trim(obj.value) != "")
			hasValue = true;
		if (hasValue == false)
			hasValue = (jQuery('table#attachTable tbody tr.template-upload p.name').first().size() == 1);
		if (hasValue == false) {
			var hasInputUrl = ( jQuery("table#attachTable tbody tr input[name=url]").first().size() == 1 );
			if (hasInputUrl == true) {
				var countOfNotEmptyInputUrl = jQuery("table#attachTable tbody tr input[name=url]").filter(function () {
										    return (this != null && this.value != '' && jQuery.trim(this.value) != '');
									  }).length;
				hasValue = (countOfNotEmptyInputUrl > 0);
			}
		}
		if (hasValue == true && hasNoDocument == true)
			jQuery('#uploadButton').removeClass('button_disabled');
		else if (hasValue == false && hasNoDocument == false)
			jQuery('#uploadButton').addClass('button_disabled');
	} catch(err) {
		alert("js error when urlChanged: '" + ((err.description == undefined) ? err : err.description) + "'");
	}
}


function reset_url_value(obj) {
	try {
		var trObj = jQuery(obj).closest('tr');
		trObj.find('input[name=url]').val('');
		trObj.find('p.name').removeClass('name');
	} catch(err) {
		alert("js error when reset_url_value: '" + ((err.description == undefined) ? err : err.description) + "'");
	}
}function updateRelatedData(attrname) {
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
}function changeStartDep(){
	inputid=document.getElementById('supplyid').value;
	newdepid=document.getElementById(inputid).value;
	var tr=document.getElementById(inputid).parentElement.parentElement.parentElement
	var td = tr.getElementsByTagName("td").item(1)
	td.innerHTML=''
		+'<table id="idTablesManager'+newdepid+'" class="add">'
		+'<colgroup /><colgroup width="10px"/><tbody><tr style="display:none">'
		+'<td><input type="hidden" id="manager'+newdepid+'" name="manager'+newdepid+'" value=""></td>'
		+'<td class="delchk"><input type="checkbox" name="idTablesManager'+newdepid+'Chk" /></td>'
		+'</tr></tbody><tfoot><tr><td colspan=2 class="add">'
		+'<button onclick="AddManager(\''+newdepid+'\'); return false;" class="add"></button>'
		+'<button onclick="DelTableRow(\'idTablesManager'+newdepid+'\', \'idTablesManager'+newdepid+'Chk\'); return false;" class="del"></button></td>'
		+'</tr></tfoot></table>'
}
function AddStartDep() {
	$('#anewentity').attr('href','popup_departments.jsp?onlyInitialDep=true&javascript=AddStartDep2()');
	fancyClassSubscribe();
	document.getElementById('supplyid').value='newentity';
	$('#anewentity').trigger('click');
}
function AddStartDep2() {
	nextid=getNextId();
	var stag='<a id="atag'+nextid+'" class="fancy" href="popup_departments.jsp?onlyInitialDep=true&javascript=changeStartDep()" onClick="document.getElementById(\'supplyid\').value=\''+
	nextid+'\'"><span id="sp'+nextid+'">'+$("#spnewentity").html()+'</span>'+
	'<input type="hidden" id="'+nextid+'" name="Инициирующее подразделение другое" value="'+$("#newentity").val()+'"></a>';
	var managerTbl = '<table id="idTablesManager'+$("#newentity").val()+'" class="add">';
	managerTbl += '<colgroup /><colgroup width="10px"/><tbody><tr style="display:none">'+
				'<td><input type="hidden" id="manager'+$("#newentity").val()+'" name="manager'+$("#newentity").val()+'" value=""></td>'+
				'<td class="delchk"><input type="checkbox" name="idTablesManager'+$("#newentity").val()+'Chk" /></td>'+
				'</tr></tbody><tfoot><tr><td colspan="2" class="add"><button onclick="AddManager(\''+$("#newentity").val()+'\'); return false;" class="add"></button>'+
				'<button onclick="DelTableRow(\'idTablesManager'+$("#newentity").val()+'\', \'idTablesManager'+$("#newentity").val()+'Chk\'); return false;" class="del"></button></td>'+
				'</tr></tfoot></table>';
	var s ='<tr><td>'+stag+'</td><td>'+managerTbl+'</td><td class="delchk"><input type="checkbox" name="idTablesStartDepChk" /></td></tr>';
	$("#idTablesStartDep > TBODY").append(s);
	fancyClassSubscribe();
}
function AddPlace() {
	$('#anewentity').attr('href','popup_departments.jsp?onlyExecDep=true&javascript=AddPlace2()');
	fancyClassSubscribe();
	document.getElementById('supplyid').value='newentity';
	$('#anewentity').trigger('click');
}
function AddPlace2() {
	nextid=getNextId();
	var stag='<a id="atag'+nextid+'" class="fancy" href="popup_departments.jsp?onlyExecDep=true" onClick="document.getElementById(\'supplyid\').value=\''+
	nextid+'\'"><span id="sp'+nextid+'">'+$("#spnewentity").html()+'</span>'+
	'<input type="hidden" id="'+nextid+'" name="Place" value="'+$("#newentity").val()+'"></a>';
	var s ='<tr><td>'+stag+'</td><td class="delchk"><input type="checkbox" name="idTablesPlaceChk" /></td></tr>';
	$("#idTablesPlace > TBODY").append(s);
	fancyClassSubscribe();
}
function AddManager(tableid) {
	var departmentid=document.getElementById('maindepartment').value;
	if(tableid!='')departmentid=tableid;
	window.open('popup_users.jsp?reportmode=true&formName=variables&fieldNames=userId|userFIO&department='+departmentid+'&onMySelect=AddManager2(\''+tableid+'\')', 'org','top=100, left=100, width=800, height=710');
}
function AddManager2(tableid) {
	nextid=getNextId();
	departmentid=document.getElementById('maindepartment').value
	if (tableid!='')departmentid=tableid
	var stag='<span id="sp'+nextid+'">'+$("#userFIO").val()+'</span>'+
		'<input type="hidden" id="'+nextid+'" name="manager'+tableid+'" value="'+$("#userId").val()+'">'
	var s ='<tr><td>'+stag+'</td><td class="delchk"><input type="checkbox" name="idTablesManager'+tableid+'Chk" /></td></tr>';
	$("#idTablesManager"+tableid+" > TBODY").append(s);
	fancyClassSubscribe();
}
/**
 * Функция переключает раскрытие/схлопывание секции в форме заявки.
 * @param mdtaskid - id заявки
 * @param name - кодовое имя секции. По нему ищем id html-элементов
 */
function frameToggle(params,name){
	if($('#section_'+name+'_img').attr("alt")=="+"){// закрыта, открываем
		frameOpen(params,name);
	} else {// открыта, закрываем
		$('#section_'+name+'_tbody').attr("style","display:none;");
		$('#section_'+name+'_img').attr({src: "style/toOpen.gif",alt: "+"});
		$.cookie('section_'+name+$('#idTask').val()+'_'+$('#mdtaskid').val(), 0, { expires : 10 });
	}
}
function frameOpen(params,name){
	$('#section_'+name+'_img').attr({src: "style/toClose.gif",alt: "-"});
	if($('#section_'+name+'_td').html()==""){// нужно грузить секцию аяксом
		reloadFrame(params,name);
	} else {// секция уже была загружена, просто отображаем её
		$('#section_'+name+'_tbody').show();
		$('#section_'+name+'_tbody').attr("style","");
	}
	$.cookie('section_'+name+$('#idTask').val()+'_'+$('#mdtaskid').val(), 1, { expires : 10 });
}
function reloadFrame(params,name){
	var page='frame/'+name+'.jsp';
	if(name.indexOf('pipeline')!=-1){page='frame/'+name+'.html';}
	$('#section_'+name+'_td').load(page+'?'+params,function () {
		$('#section_'+name+'_tbody').show();
		$('#section_'+name+'_tbody').attr("style","");
		try{
			fancyClassSubscribe();
		} catch (e) {
			//ignore
		}
	});
}
/**
 * Функция переключает раскрытие/схлопывание секции в форме сравнения
 * @param params - параметры
 * @param name - кодовое имя секции. По нему ищем id html-элементов
 */
function frameToggleCompare(params,name){
	if($('#section_'+name+'_img').attr("alt")=="+"){// закрыта, открываем
		$('#section_'+name+'_img').attr({src: "style/toClose.gif",alt: "-"});
		if($('#section_'+name+'_td').html()==""){// нужно грузить секцию аяксом
			reloadFrameCompare(params,name);
		} else {// секция уже была загружена, просто отображаем её
			$('#section_'+name+'_tbody').show();
			$('#section_'+name+'_tbody').attr("style","");
		}
		$.cookie('section_'+name+$('#objectType').val()+'_'+$('#ids').val(), 1, { expires : 10 });
	} else {// открыта, закрываем
		$('#section_'+name+'_tbody').attr("style","display:none;");
		$('#section_'+name+'_img').attr({src: "style/toOpen.gif",alt: "+"});
		$.cookie('section_'+name+$('#objectType').val()+'_'+$('#ids').val(), 0, { expires : 10 });
	}
}
function reloadFrameCompare(params,name){
	var page='compare/frameCompare.jsp';
	$('#section_'+name+'_td').load(page+'?'+params,function () {
		$('#section_'+name+'_tbody').show();
		$('#section_'+name+'_tbody').attr("style","");
		try{
			fancyClassSubscribe();
		} catch (e) {
			//ignore
		}
	});
}
/**
 * Загрузить неподгруженные секции вручную. 
 */
function loadFramesManually() {
	var ajaxSections = ["department","supply","conclusion","priceCondition"];//асинхронно подгружаемые секции
	var allSectionLoaded = true;
	for(var i=0; i<ajaxSections.length; i++) {
		if($('#section_'+ajaxSections[i]+'_td').html()=="") {
			allSectionLoaded = false;
			$('#section_'+ajaxSections[i]+'_td').load('frame/'+ajaxSections[i]+'.jsp?' + $('#md_frame_params').val(), 
					{},loadFramesManually);
			break;
		}
	}
	if (allSectionLoaded) {
		unfade();
		return submitDataCallback();
	}
}

function fundPane(mdtaskid){
	if($('#fundImg'+mdtaskid).attr("alt")=="+"){
		$('#fund'+mdtaskid).load('ajax/fundList.do?mdtaskid='+mdtaskid,function () {
			$('#fundImg'+mdtaskid).attr({src: "theme/img/collapse.jpg",alt: "-"});
			$('#fund'+mdtaskid).parent().attr("colspan","10");
			$('#fund'+mdtaskid).parent().parent().show();
			$('#fund'+mdtaskid).parent().parent().attr("style","");
		});
	} else {
		$('#fund'+mdtaskid).parent().parent().attr("style","display:none;");
		$('#fund'+mdtaskid).empty();
		$('#fundImg'+mdtaskid).attr({src: "theme/img/expand.jpg",alt: "+"});
	}
}
function showCedTr(mdtaskid){
	$('#ced'+mdtaskid).load('ajax/cedList.html?mdtaskid='+mdtaskid,function () {
		$('#ced'+mdtaskid).parent().attr("colspan","10");
		$('#ced'+mdtaskid).parent().parent().show();
		$('#ced'+mdtaskid).parent().parent().attr("style","");
	});
}
function n6Pane(mdtaskid){
	if($('#n6Img'+mdtaskid).attr("alt")=="+"){
		$('#n6'+mdtaskid).load('ajax/n6List.do?mdtaskid='+mdtaskid,function () {
			$('#n6Img'+mdtaskid).attr({src: "theme/img/collapse.jpg",alt: "-"});
			$('#n6'+mdtaskid).parent().attr("colspan","10");
			$('#n6'+mdtaskid).parent().parent().show();
			$('#n6'+mdtaskid).parent().parent().attr("style","");
		});
	} else {
		$('#n6'+mdtaskid).parent().parent().attr("style","display:none;");
		$('#n6'+mdtaskid).empty();
		$('#n6Img'+mdtaskid).attr({src: "theme/img/expand.jpg",alt: "+"});
	}
}
function fundFrame(mdtaskid){
	if($('#fundImg').attr("alt")=="+"){
		$('#fundFrameDiv').load('ajax/fundList.do?mdtaskid='+mdtaskid,function () {
			$('#fundImg').attr({src: "style/toClose.gif",alt: "-"});
			$('#fundFrameDiv').parent().parent().parent().show();
			$('#fundFrameDiv').parent().parent().parent().attr("style","");
		});
		$.cookie('section_fund'+$('#idTask').val()+'_'+$('#mdtaskid').val(), 1, { expires : 10 });
	} else {
		$('#fundFrameDiv').parent().parent().parent().attr("style","display:none;");
		$('#fundFrameDiv').empty();
		$('#fundImg').attr({src: "style/toOpen.gif",alt: "+"});
		$.cookie('section_fund'+$('#idTask').val()+'_'+$('#mdtaskid').val(), 0, { expires : 10 });
	}
}
function n6Frame(mdtaskid){
	if($('#n6Img').attr("alt")=="+"){
		$('#n6FrameDiv').load('ajax/n6List.do?mdtaskid='+mdtaskid,function () {
			$('#n6Img').attr({src: "style/toClose.gif",alt: "-"});
			$('#n6FrameDiv').parent().parent().parent().show();
			$('#n6FrameDiv').parent().parent().parent().attr("style","");
		});
		$.cookie('section_n6'+$('#idTask').val()+'_'+$('#mdtaskid').val(), 1, { expires : 10 });
	} else {
		$('#n6FrameDiv').parent().parent().parent().attr("style","display:none;");
		$('#n6FrameDiv').empty();
		$('#n6Img').attr({src: "style/toOpen.gif",alt: "+"});
		$.cookie('section_n6'+$('#idTask').val()+'_'+$('#mdtaskid').val(), 0, { expires : 10 });
	}
}
/**
 * Восстанавливает состояние секций (открыта, свёрнута)
 */
function restoreSection(){
	var sections =['Контрагенты','inLimit','limitParam','priceConditionLimit','contract','graph',
	               'условия','supply','conclusion','priceCondition','oppParam',
	               'Комментарии','expertus','documents','standardPeriod','projectTeam','department',
	               'stopfactor','expAgree','fund','n6','stop_factors_3','stop_factors_2','stop_factors_1',
	               'sendRequest','histRequest','promissory_note','Гарантии','Поручительство','Залоги',
	               'Прочие условия','Условия досрочного погашения','fineList','commission',
	               'percentStavka','pipeline'];
	for (var i = 0; i < sections.length; i++) {
		var c = $.cookie('section_'+sections[i]+$('#idTask').val()+'_'+$('#mdtaskid').val());
		if(c==null && (sections[i]=='Контрагенты' || sections[i]=='limitParam'|| sections[i]=='oppParam')){c='1';}
		if(c=='1'){
			$('#section_'+sections[i]+' > thead').click();
		}
	}
	window.setTimeout(restoreSubSection, 4000);
}
/**
 * Подсекции, для которых родительская секций подгружается аяксом. Их гружу после паузы.
 * Лучше по событию, конечно. Но релиз нужно сделать сегодня, а есть еще задачи. Как всегда.
 */
function restoreSubSection(){
	var sections =['sendRequest','histRequest','promissory_note','Гарантии','Поручительство','Залоги'];
	for (var i = 0; i < sections.length; i++) {
		var c = $.cookie('section_'+sections[i]+$('#idTask').val()+'_'+$('#mdtaskid').val());
		if(c==null && (sections[i]=='Контрагенты' || sections[i]=='limitParam'|| sections[i]=='oppParam')){c='1';}
		if(c=='1'){
			$('#section_'+sections[i]+' > thead').click();
		}
	}
}
function doSection(id){
	try{
		var node, image, disp;
		node=getNode(null, 'section_'+id, 'TABLE');
		if (!node) return false;
		image=getNode(node, 'imgSection', 'IMG');
		node=node.tBodies[0];
		disp=(node.style.display=='none');
		if (image){
			image.src='style/'+((disp) ? 'toClose' : 'toOpen')+'.gif';
		}
		node.style.display=(disp) ? 'block' : 'none';
		$.cookie('section_'+id+$('#idTask').val()+'_'+$('#mdtaskid').val(), disp?1:0, { expires : 10 });
	} catch (Err){
		alert(Err.description);
	}
}

/**
 * Разблокирует кнопки по окончании загрузки формы заявки. Убирает панель с надписью 'Загружается'. 
 */
function unfade() {
	document.getElementById('fader').style.display='none';
	document.getElementById('controlPanel').style.display='block';
	var nodes=document.getElementsByTagName('select')
	for(var i=0; i<nodes.length; i++){
		nodes[i].style.visibility='visible';
	}
}

/**
 * Блокирует кнопки при начале загрузки формы заявки или при нажатии на кнопку 'Завершить операцию'. 
 * Показывает панель с надписью 'Загружается'.
 */
function fade() {
	document.getElementById('fader').style.display='';
	document.getElementById('controlPanel').style.display='none';
//	var nodes=document.getElementsByTagName('select')
//	for(var i=0; i<nodes.length; i++){
//		nodes[i].style.visibility='visible';
//	}
}
/********************************************************************************************************************/
/*      переформатирование входных значений												                            */
/*   'money' (3 знака после запятой)-> '123456 789.34 5 4 34' или '123 456789,345 434'	-> '123 456 789,345'        */
/*   'money2digits' (2 знака после запятой) -> '123456 789.34 5 4 34' или '123 456789,345 434'	-> '123 456 789,35' */
/*   'money1digits' (1 знак после запятой) -> '123456 789.34 5 4 34' или '123 456789,345 434'	-> '123 456 789,3'  */

/*   'digitsSpaces' -> '123456 789.34 5 4 34' или '123 456789,345 434'	-> '123 456 789'                     		*/
/*   'date'  -- dd/mm/yyyy, dd,mm,yyyy, dd.mm.yyyy  -> dd.mm.yyyy (with check for validity like 00.13.3009)  		*/
/*   'digits' -- not quite clear. '123456789.345434' или '123456789,345434'	-> '123456789.345434'            		*/
/*   'number' -- not quite clear. '1234567890'  -> '1234567890'                                              		*/
/********************************************************************************************************************/
function input_autochange(myfield, field_type) {
	//alert("myfield.id : " + myfield.id);
	fieldChanged();//устанавливаю флаг изменения поля
	//alert("myfield.value before: " + myfield.value);
	//alert("field_type:" + field_type);
	
	if ((field_type == 'money') || (field_type == 'money1digits') || (field_type == 'money2digits') || (field_type == 'money3digits') 
			|| (field_type == 'digitsSpaces') || (field_type == 'money2digitsOrInt'))  {
		var newnumber = toNumber(myfield, field_type);
		//alert('newnumber:' + newnumber);
		if (newnumber == null) return;
		myfield.value = toFormattedString(newnumber, field_type);
		//alert('myfield.value after:' + myfield.value);
	} else {
		var number_regexp=/^\d*/;
		var digits_regexp=/(^(\d+\s*)*\.\d*)|(\d+\s*)*/;
		var date_regexp = new RegExp("^((0[1-9]|[12][0-9]|3[01]){1}\.(0[1-9]|1[012]){1}\.((19|20)\\d\\d){1})$");
		//change all ',' and '/' to '.' for digits and date
		myfield.value=myfield.value.replace(/\//g,'.').replace(/\,/g,'.');
		//check types
		if (field_type == 'digits') myfield.value = digits_regexp.exec(myfield.value)[0];
	    if (field_type == 'number') myfield.value = number_regexp.exec(myfield.value)[0];
	    if (field_type == 'date') 
	    	if (date_regexp.test(myfield.value) == false) myfield.value = '';
	}
	outputvalue = myfield.value;
	//if (WAR_CONDITIONS == false) 
	//alert ('output: ' + outputvalue);
}

/**
 * Converts input value (as a value, not as an object) to number
 * Returns number if conversion is OK.
 * Returns null, if  conversion is not OK.
 * @param number number, not a field
 */
function toNumberFromString(number, field_type) {
	var numberObject = new Object();
	numberObject.value = number;
	return toNumber(numberObject, field_type);
}

/**
 * Converts input value (as object) to number
 * Returns number if conversion is OK.
 * Returns null, if  conversion is not OK.
 */
function toNumber(myfield, field_type) {
	/********************************************************************************************/
	/* Часть I. Преобразуем строку '123 456 789.345 434' или '123 456 789,345 434' в число      */
	/********************************************************************************************/
	
	//change all '.' and to ','
	//var NotFormatted0 = myfield.value.replace(/ /g,"");
	//var NotFormatted0 = (myfield.value).replace(/^\s*|\s*$/g,'');
	//var NotFormatted0 = (myfield.value).replace(/\s/g, "");
	// убираем пробелы и меняем запятую на точку
	var NotFormatted0 = removeWhiteSpace(myfield.value);
	if (NotFormatted0 == '') {
		myfield.value = '';
		return null;
	}
	var notFormatted = NotFormatted0.replace(/,/g,".");

	// не число!!!
	if (isNaN(notFormatted)) {
		myfield.value = ''
		return null; 
	}
	
	/********************************************************************************************/
	/* Часть II. Округляем число до трех знаков после запятой или до целого							        */
	/********************************************************************************************/
	// округляем число до трех знаков после запятой для типа 'money' и до целого числа для других типов данных. 
	coef = 1;  // для целых чисел
	if (field_type == 'money') coef = 100;  
	if (field_type == 'money3digits') coef = 1000;
	if (field_type == 'money2digits') coef = 100;
	if (field_type == 'money2digitsOrInt') coef = 100;
	if (field_type == 'money1digits') coef = 10;
	newnumber = Math.round(notFormatted*coef)/coef;
	/*
	// Запретим ввод отрицательных значений для типов money и digitsSpaces
	if (newnumber < 0) {
		myfield.value = ''
		return null
	}
	*/
	return newnumber;
}

/**
 * Converts number into String acording to format.
 * @param number top convert to
 * @param field_type type of formatting
 * @return formatted string
 */
function toFormattedString(number, field_type) {
	var WAR_CONDITIONS = false;   // if true, no conversion is performed
	
	/********************************************************************************************/
	/* Часть III. Обратное преобразование в строку: '123 456 789,35' или '123 456 789'          */
	/********************************************************************************************/
	newnumberFormatted = number;		
	// Обратное преобразование: меняем запятые на пробелы (разделитель разрядов) и точку на запятую (дробная часть)
	if (field_type=='money')
		if (WAR_CONDITIONS == false) newnumberFormatted = formatNumber(number, '0,000.00');
	if (field_type=='money3digits')
		if (WAR_CONDITIONS == false) newnumberFormatted = formatNumber(number, '0,000.000');
	if (field_type=='money2digits')
		if (WAR_CONDITIONS == false) newnumberFormatted = formatNumber(number, '0,000.00');
	if (field_type=='money2digitsOrInt'){
		if (WAR_CONDITIONS == false) newnumberFormatted = formatNumber(number, '0,000.00');
		//if(newnumberFormatted.endsWith('.00'))
		newnumberFormatted=newnumberFormatted.replace('\.00','');
	}
	if (field_type=='money1digits')
		if (WAR_CONDITIONS == false) newnumberFormatted = formatNumber(number, '0,000.0');
	
	if (field_type=='digitsSpaces')
		if (WAR_CONDITIONS == false) newnumberFormatted = formatNumber(number, '0,000');
	
	if (WAR_CONDITIONS == true) formattedValue = newnumberFormatted;
	else formattedValue = newnumberFormatted.replace(/,/g," ").replace(/\./gi,",");
	return formattedValue;
}

function removeWhiteSpace(input) {
	var output = "";
	for (var i = 0; i < input.length; i++) {
		//alert(i + ", " + input.charCodeAt(i))
		if (! ((input.charCodeAt(i) == 32) || (input.charCodeAt(i) == 160))) {
			output += input.charAt(i);
		}
	}
	return output;
}

/**
 * Formats the number according to the ‘format’ string; adherses to the american
 * number standard where a comma is inserted after every 3 digits. note: there
 * should be only 1 contiguous number in the format, where a number consists of
 * digits, period, and commas any other characters can be wrapped around this
 * number, including ‘$’, ‘%’, or text examples (123456.789): ‘0 - (123456) show
 * only digits, no precision ‘0.00 - (123456.78) show only digits, 2 precision
 * ‘0.0000 - (123456.7890) show only digits, 4 precision ‘0,000 - (123,456) show
 * comma and digits, no precision ‘0,000.00 - (123,456.78) show comma and
 * digits, 2 precision ‘0,0.00 - (123,456.78) shortcut method, show comma and
 * digits, 2 precision
 * 
 * @method format
 * @param format
 *            {string} the way you would like to format this text
 * @return {string} the formatted number
 * @public
 */
function formatNumber(number, format) {
	if (number == null) return '';
	if (number == '') return '';
	if (typeof format != 'string') return '';

	var hasComma = (format.indexOf(',') > -1);
	// strip non numeric
	var	psplit = format.replace(/[^0-9-.]/g,"");
	psplit = psplit.split('.');
	var	that = number;
	// compute precision
	if (1 < psplit.length) {
		// fix number precision
		that = that.toFixed(psplit[1].length);
	}
	// error: too many periods
	else if (2 < psplit.length) {
		throw('NumberFormatException: invalid format, formats should have no more than 1 period: ' + format);
	}
	// remove precision
	else {
		that = that.toFixed(0);
	}
	// get the string now that precision is correct
	var fnum = that.toString();
	
 
	// format has comma, then compute commas
	if (hasComma) {
		// remove precision for computation
		psplit = fnum.split('.');
 
		var cnum = psplit[0],
			parr = [],
			j = cnum.length,
			m = Math.floor(j / 3),
			n = cnum.length % 3 || 3; // n cannot be ZERO or causes infinite
										// loop
		// break the number into chunks of 3 digits; first chunk may be less
		// than 3
		for (var i = 0; i < j; i += n) {
			if (i != 0) {n = 3;}
			parr[parr.length] = cnum.substr(i, n);
			m -= 1;
		}
 
		// put chunks back together, separated by comma
		fnum = parr.join(',');
 
		// add the precision back in
		if (psplit[1]) {fnum += '.' + psplit[1];}
	}
	// replace the number portion of the format with fnum
	return fnum; // format.replace(/[d,?.?]+/, fnum);
};
/**
 * Iterate through HTMLNode in DOMTree, and apply task() function
 */
function HTMLNodeIterator()
{
	//task:function, node:HTML Node, extraParam: extta param passed to task function
	this.iterate = function iterate(task, node, extraParam)
	{
		task(node, extraParam);
		if (node.childNodes.length > 0) 
			for(var x = 0; x < node.childNodes.length; x++)
					this.iterate(task, node.childNodes[x], extraParam);
	}
}

// show or hide all elements in the table with the given className
function showHideElementsInTable(tableId, className) {
	try
	{
   		var tbl = document.getElementById(tableId);
	    var body = tbl.getElementsByTagName("TBODY")[0];
	    // show or hide all elements with the given className
	  	var htmlNodeIterator = new HTMLNodeIterator();
		htmlNodeIterator.iterate(showOrHideElement, body, className);
	}
	catch (Err)
	{
		alert(Err.description)
		return false
	}
} 

//makes element visible by Changing class: removes 'nonverified', adds 'verifying'
function showOrHideElement(node, className) {
	try {
		if (hasClass(node, className)){
			try {
				// show field
				styleDisplay = node.style.display;
				if (styleDisplay == 'none')  {
			    	node.style.display = '';
			    }
			    // hide field
				if (styleDisplay == '')  {
					node.style.display = 'none';	
				}
			} catch (Err ) {}
		}
	} catch (Err ) {}
}

function AddRowToTable(tableId, clearFunc) {
	fieldChanged()
	var script = "DeleteFile"
	try
	{
   		var tbl = document.getElementById(tableId);
	    var body = tbl.getElementsByTagName("TBODY")[0];
		var Rows = body.rows
		var child = Rows[0]
		var myTR = child.cloneNode(true);
		myTR.style.display = "";
	  	body.appendChild(myTR);
	  	// reread after adding
	  	myTR = body.childNodes[body.childNodes.length - 1];
	  	// make all elements of a new added elements visible 
	  	//(for validation purposes. No check performs for invisible elements like first rows, that are copied)
	  	var htmlNodeIterator = new HTMLNodeIterator();
		htmlNodeIterator.iterate(makeVisible, myTR, null);
	}
	catch (Err)
	{
		alert(Err.description)
		return false
	}
}

// makes element visible by Changing class: removes 'nonverified', adds 'verifying'
// extraparam -- not used
function makeVisible(node, extraparam) {
	try {
		if (hasClass(node, 'nonverified')){
			removeClass(node,'nonverified');
			addClass(node, 'verifying');
		}
	} catch (Err ) {}
}

function hasClass(ele,cls) {
	return ele.className.match(new RegExp('(\\s|^)'+cls+'(\\s|$)'));
}
function addClass(ele,cls) {
	if (!this.hasClass(ele,cls)) ele.className += " "+cls;
}
function removeClass(ele,cls) {
	if (hasClass(ele,cls)) {
		var reg = new RegExp('(\\s|^)'+cls+'(\\s|$)');
		ele.className=ele.className.replace(reg,' ');
	}
}
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
var selectedTargetId;
var selectedHiddenTargetId;

$(pipelineHandler);
$(document).ajaxComplete(pipelineHandler);

function onPipelineStatusManualChange() {
    if($('#mdTask_pipeline_status_manual').is(':checked'))
		$('#mdTask_pipeline_statuses_change').show();
    else
		$('#mdTask_pipeline_statuses_change').hide();
}
/**
 * Обработчик секции "Секция ПМ". Поиск секции по атрибуту <code>content="pipeline"</code>.
 * На необходимые поля вешаются обработчики-валидаторы.
 */
function pipelineHandler() {
    onPipelineStatusManualChange();
	var pipeline = $(document).find("div[content='pm_section'][pipelineHandle!='on']");
	if (pipeline.length > 0)
		pipeline.attr("pipelineHandle", "on");
	else 
		return;
	
	var $pipeline = $(pipeline);

	//обработка возвращаемых значений диалогов
	$pipeline.find(".dialogActivator").each(function() {
		defineDialogTarget($(this));
	});

	//добавление цели финансирования
	$pipeline.find("#mdTask_pipeline_financingObjectives_addLink").each(function() {
		$(this).on("click", function() {
			addFinancingObjective(this);
		});
	})
	
	//удаление цели финансирования
	$pipeline.find("a[name='mdTask_pipeline_financingObjectives_removeLink']").each(function() {
		$(this).on("click", function() {
			removeFinancingObjective(this);
		});
	})
	
	//обработка диалогов
	$pipeline.find("div.sectionPmDialog a").each(function() {
		$(this).on("click", function() {
			me = $(this);
			setTargetFromDialog(selectedTargetId, me.text(), selectedHiddenTargetId, me.attr("returnValue"));
		});
	});
	
	//обработка отображения плавающей ставки
	$pipeline.find("input[name='mdTask_fixedRate']").each(function() {
		var me = $(this);
		baseRateHandler(me);
		me.on("click", function() {
			baseRateHandler(me);
		});
	});
	
	//обработка изменения полей
	$pipeline.find("input[type!='hidden'], textarea, select").each(function() {
		$(this).on("change", function() {
			fieldChanged(this);
		});
	});
	
	//обработка полей с календарем
	$pipeline.find("input[valueType='date']").each(function() {
		$(this).on("focus", function() {
			displayCalendarWrapper($(this).attr("id"), '', false);
		});
	});
	
	//обработка формата поля
	$pipeline.find("input[valueType]").each(function() {
		$(this).on("blur", function() {
			input_autochange(this, $(this).attr("valueType"));
		});
	});
	dialogHandler();
}

/**
 * Добавляет из шаблона поле для записи новой цели финансирования.
 * @param addLink объект ссылки добавления новой цели финансирования
 */
function addFinancingObjective(addLink) {
	var parent = $(addLink).parent();
	var template = $.trim(parent.find("#mdTask_pipeline_financingObjectives_template").html());
	
	var fo = $(parent).append(template);
	var $fo = $(fo);
	var $textarea;
	$fo.find("textarea").each(function() {
		$textarea = $(this);
		$textarea.attr("name", "mdTask_pipeline_financingObjectives");
		$textarea.uniqueId();
		$textarea.on("change", function () {
			fieldChanged(this);
		});
	});
	$fo.find("a.mdTask_pipeline_financingObjectives_select").each(function() {
		$(this).on("click", function() {
			$('#selectedID').val($textarea.attr("id"));
			$('#mdTask_pipeline_financingObjectives').dialog({draggable:false, modal:true, width:800});
		});
	});
	$fo.find("a[name='mdTask_pipeline_financingObjectives_removeLink']").each(function() {
		$(this).on("click", function() {
			removeFinancingObjective(this);
		});
	});

	$(hideFinancingObjectivesEmptyFieldDiv).hide();
	dialogHandler();
}

function hideFinOdjectiveEmptyField() {
	$("#mdTask_pipeline_financingObjectives_addLink").trigger("click");
	$("#hideFinancingObjectivesEmptyFieldDiv").hide();
}

/**
 * Удалаяет из шаблона поле для записи цели финансирования.
 * @param removeLink объект ссылки удаления цели финансирования
 */
function removeFinancingObjective(removeLink) {
	$(removeLink).parent().remove();
}

/**
 * Установка выбранного значения в целевой элемент с идентификатором.
 * @param targetId идентификатор целевого элемента
 * @param value значение
 * @param hiddenTargetId идентификатор скрытого целевого элемента (используется для передачи идентификатора)
 * @param returnValue значение для записи в скрытый целевой элемент
 */
function setTargetFromDialog(targetId, value, hiddenTargetId, returnValue) {
	if (hiddenTargetId != null && hiddenTargetId != "") {
		var hiddenTarget = $("#" + hiddenTargetId);
		$(hiddenTarget).attr("value", returnValue);
	}
	
	var target = $("#" + targetId);
	var targetTag = $(target).prop("tagName").toLowerCase();
	if (targetTag == "textarea") {
		$(target).text(value);
	} else if (targetTag == "input") {
		$(target).attr("value", value);
	}
}

/**
 * Обработчик изменений поля ставки.
 * @param baseRateGroup jquery объект поля ставки
 */
function baseRateHandler(baseRateGroup) {
	var baseRateSection = $("#mdTask_baseRate_section");
	baseRateSection.show();
	/*if (baseRateGroup.attr("id") == "mdTask_fixedRate_true" && baseRateGroup.attr("checked"))
		baseRateSection.hide();
	else if (baseRateGroup.attr("id") == "mdTask_fixedRate_false" && baseRateGroup.attr("checked"))
		baseRateSection.show();
	else
		baseRateSection.hide();*/
}

/**
 * Определяет идентификаторы для записи значения выбранного в диалоге.
 * @param $dialogActivator jquery объект активатора диалога
 */
function defineDialogTarget($dialogActivator) {
	$dialogActivator.on("click", function() {
		selectedTargetId = $dialogActivator.attr("targetId");
		selectedHiddenTargetId = $dialogActivator.attr("hiddenTargetId");
	});
}
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
function AddExpertTeam(expname) {
	$('#expname').val(expname);
	window.open('expertTeamList.html?mdtaskid='+$("#mdtaskid").val()+'&expname='+expname, 
			'org','top=100, left=100, width=800, height=710,scrollbars=1,resizable=1');
	return false;//don't submit
}
function AddExpertTeam2() {
	//alert($("#etuserId").val());
	$.post('ajax/addExpertTeam.do',{id: $("#etuserId").val(), mdtaskid:$("#mdtaskid").val(),expname:$('#expname').val()},AddExpertTeam3);
	window.setTimeout(AddExpertTeam3, 3000);
}
function reloadProjectTeam(){
	$('#projectteam').load('frame/projectTeam.jsp?'+$('#md_frame_params').val());
}
function reloadExpertTeam(){
	$('#expertus').load('frame/expertus.jsp?'+$('#md_frame_params').val());
}
function AddExpertTeam3(xml) {
	reloadExpertTeam();
}
function delExpertTeam(id,expname){
	$('#expname').val(expname);
	$.post('ajax/delExpertTeam.do',{id: id, mdtaskid:$("#mdtaskid").val(),expname:$('#expname').val()});
	reloadExpertTeam();
	window.setTimeout(AddExpertTeam3, 3000);
}

function AddProjectTeam(section) {
	$('#section').val(section);
	window.open('popup_users.jsp?reportmode=true&projectTeamMode=true&formName=variables&fieldNames=ptuserId|ptuserFIO&onMySelect=AddProjectTeam2()&mdtaskid='+$("#mdtaskid").val()+'&section='+section, 
			'org','top=100, left=100, width=800, height=710');
	return false;//don't submit
}
function AddProjectTeam2() {
	//проверяем не добавили ли мы его раньше
	if($('.'+$("#section").val()+'user'+$("#ptuserId").val()).size()>0){
		return;
	}
	var s ='<tr id="'+$("#section").val()+'user'+$("#ptuserId").val()+'"><td>'+
	    $("#ptuserFIO").val()+'</td><td></td><td></td><td></td></tr>';
	$("#idTablesProjectTeam"+$("#section").val()+" > TBODY").append(s);
	$.post('ajax/newProjectTeam.do',{id: $("#ptuserId").val(), mdtaskid:$("#mdtaskid").val(),section:$("#section").val()},AddProjectTeam3);
}
function AddProjectTeam3(xml) {
	reloadProjectTeam();
	fancyClassSubscribe();
}
function delProjectTeam(id,section){
	$('.'+section+'user'+id).remove();
	$.post('ajax/delProjectTeam.do',{id: id, mdtaskid:$("#mdtaskid").val(), section:section});
}
function AssignProjectTeam(who, role) {
	try{
		$.post('ajax/AssignProjectTeam.do',{user: who, mdtaskid:$("#mdtaskid").val(),role:role},AddProjectTeam3);
		AddProjectTeam3();
		window.setTimeout(AddProjectTeam3, 3000);
    } catch(e) {
	  //do nothing
	}
}
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
function AddSupplyGarant(addPerson) {
	fieldChanged();
	var param = {
			id:$('#CRMID').val(),
			name:$('#selectedName').val()
	};
	$( "#newGarantTemplate" ).tmpl(param).appendTo( "#idTablesSupplyGar > TBODY" );
	var orgtd_template_id = "GarantTemplateOrg";
	if(addPerson=='y') { orgtd_template_id = "GarantTemplatePerson";}
	$('#orgtd'+$('#CRMID').val()).html( $( "#"+orgtd_template_id ).tmpl( param ) );
	fancyClassSubscribe();
	
	dialogHandler();
	calendarInit();
}
function d_changeIssuer(id){
	fieldChanged();
	$('#d_issuer'+id).val($('#CRMID').val());
	$('#d_issuer_span'+id).html('<a href="clientInfo.html?id='+$('#CRMID').val()+
			'&mdtask='+ $('#mdtaskid').val() + '" target="_blank">'+$('#selectedName').val()+'</a>');
}
function insertSupplyDepositTR(xml){
	fieldChanged();
	var rating = $('rating:first > value:first',xml).text();
	if (rating!='') rating = 'Рейтинг '+rating;
	var nextid=getNextId();
	var param = {
			nextid:nextid, 
			crmid:$('#CRMID').val(),
			orgname:$('#selectedName').val(),
			rating:rating
	};
	$( "#newDepositTemplate" ).tmpl(param).appendTo( "#idTablesSupplyD > TBODY" );
	var orgtd_template_id = "DepositorTemplateOrg";
	try{//делаю так сложно ради IE6. IE6 не поддерживает сравнения объекта со строкой
        if (xml=='person'){
        	orgtd_template_id = "DepositorTemplatePerson";
        }
    } catch (rr)
    {
    	orgtd_template_id = "DepositorTemplateOrg";
    }
	$('#orgtd'+nextid).html( $( "#"+orgtd_template_id ).tmpl( param ) );
	fancyClassSubscribe();
	calendarInit();
}
function insertWarrantyTr(xml) {
	fieldChanged();
	var nextid = getNextId();
    var personmode='n';
    try{//делаю так сложно ради IE6. IE6 не поддерживает сравнения объекта со строкой
        if (xml=='person'){
            personmode='y';
        }
    } catch (rr)
    {
        personmode='n';
    }
    if (personmode=='y'){
        org='<a href=PersonInfo.jsp?id='+
        $("#CRMID").val()+
        ' target=_blank>'+$("#selectedName").val()+'</a>'+
        '<input type="hidden" name="w_contractor"/>'+
        '<input type="hidden" name="w_person" value="'+$("#CRMID").val()+'"/>';
    } else {
        var rating = $('rating:first > value:first',xml).text();
        if (rating != '') rating = '<span class="rating">Рейтинг поручителя '+rating+'</span>';
        org='<a href=clientInfo.html?id='+
        $("#CRMID").val()+
        ' target=_blank>'+$("#selectedName").val()+'</a> '+rating+
        '<input type="hidden" name="w_contractor" value="'+$("#CRMID").val()+'"/>'+
        '<input type="hidden" name="w_person"/>';
    }
    org=org+'<input type="hidden" name="w_guid" value="'+nextid+'"/>';

	var param = {
			id:$('#CRMID').val(),
			name:$('#selectedName').val(),
			guid:nextid
	};
	$( "#newWarrantyTemplate" ).tmpl(param).appendTo( "#idTablesWarranty > TBODY" );
	$('#orgtd'+$('#CRMID').val()).html( org );
	fancyClassSubscribe();
	calendarInit();
}
function AddPromissoryNote() {
	fieldChanged();
	var nextid=getNextId();
	var param = {nextid:nextid};
	$( "#newPromissoryNoteTemplate" ).tmpl(param).appendTo( "#idTable_promissory_note > TBODY" );
	calendarInit();
}
function g_FullSum_change(id) {
	fieldChanged();
	if($('#g_FullSum'+id).prop("checked")){
		$('#g_sum'+id).hide();
		$('#guarantee_sum'+id).val("0.00");
	} else {
		$('#g_sum'+id).show();
	}
}
function w_FullSum_change(guid) {
	fieldChanged();
    if (document.getElementById('w_FullSum'+guid).checked){
    	document.getElementById('w_sum'+guid).style.display = "none";
    	document.getElementById('w_resp'+guid+'a').checked=true;
    }else{
    	document.getElementById('w_sum'+guid).style.display = "";
    }
}
function openDialogAddSupply(addPerson,supply_code) {
	$('#supply_code').val(supply_code);
	var script = 'AddSupplyGarant';
	if(supply_code=='w')script = 'AddWarranty';
	if(supply_code=='d')script = 'AddDeposit';
	var href='popup_org.jsp'; 
	if(addPerson=='y') {
		href='popup_persons.jsp';
		script = script+'(\'y\')'
	} else {
		script = 'openDialogAddSupplyKz()'
	}
	href += '?formName=variables&ek=only&fieldNames=selectedID|selectedName|CRMID&onMySelect='+script +'&first=first';
	window.open(href, 'orgek','top=100, left=100, width=800, height=710');
}
function openDialogAddSupplyKz(){
	$.post('ajax/orgname.html',{id: $('#CRMID').val()}, openDialogAddSupplyKz3);
}
function openDialogAddSupplyKz3(data){
	$('#selectedName').val(data);
	var supply_code = $('#supply_code').val();
	if(supply_code=='w'){
		AddWarranty('n');
	}
	if(supply_code=='d'){
		AddDeposit('n');
	}
	if(supply_code=='g'){
		AddSupplyGarant('n');
	}
}
function AddWarranty(addPerson) {
	fieldChanged();
	if(addPerson=='y') {
		insertWarrantyTr('person');
	} else {
		$.post(
		    'ajax/rating.do',{org: $('#CRMID').val()},
		    insertWarrantyTr
		);
	}
	
	dialogHandler();
}
function AddDeposit(addPerson) {
	fieldChanged();
	if(addPerson=='y') {
		insertSupplyDepositTR('person');
	} else {
		$.post(
				  'ajax/rating.do',{org: $('#CRMID').val()},
				  insertSupplyDepositTR
			);
	}
	
	dialogHandler();
}
function AddWithdraw(id) {
    fieldChanged();
    var nextid=getNextId();
    $( "#newWithdrawTemplate" ).tmpl( {unid:nextid,trid:id} ).appendTo( "#withdraw_table"+id+" > TBODY" );
    trance_period_format_change();
	calendarInit();
}
function tranceFlagControl(){
	if($('#trance_hard_graph').is(':checked')){
		$('#trance_limit_use_div').hide();
		$('#trance_limit_excess_div').hide();
	} else {
		$('#trance_limit_use_div').show();
		$('#trance_limit_excess_div').show();
	}
}
function trance_period_format_change(){
	var format = $('#trance_period_format').val();
	$('.trance_all_format').hide();
	$('.trance_'+format).show();
	validateWithdrawPeriod();
	calendarInit();
}
function validateWithdrawPeriod(){
	//Периоды предоставления не могут пересекаться
	$('#validateWithdrawPeriodError').hide();
	var format = $('#trance_period_format').val();
	var dates = [];
	$('.trance_year > input').each(function(i){
		var d ={};
		d.year = $(this).val();
		d.quarter = $(this).parent().parent().find('.trance_quarter > select').val();
		d.month = $(this).parent().parent().find('.trance_month option:selected').text();
		d.hyear = $(this).parent().parent().find('.trance_hyear > select').val();
		d.from = $(this).parent().parent().find('.trance_date > input[name=withdraw_from]').val();
		d.to = $(this).parent().parent().find('.trance_date > input[name=withdraw_to]').val();
		dates.push(d);
	});
	for(var i=0; i<dates.length; i++) {
		for(var j=i+1; j<dates.length; j++) {
			if(format == 'quarter' && dates[i].year == dates[j].year && dates[i].quarter == dates[j].quarter){
				$('#validateWithdrawPeriodError').show();
				$('#validateWithdrawPeriodError').text('Периоды предоставления не могут пересекаться: '+dates[i].quarter+' квартал '+dates[i].year+' года');
			}
			if(format == 'hyear' && dates[i].year == dates[j].year && dates[i].hyear == dates[j].hyear){
				$('#validateWithdrawPeriodError').show();
				$('#validateWithdrawPeriodError').text('Периоды предоставления не могут пересекаться: '+dates[i].hyear+' полугодие '+dates[i].year+' года');
			}
			if(format == 'year' && dates[i].year == dates[j].year){
				$('#validateWithdrawPeriodError').show();
				$('#validateWithdrawPeriodError').text('Периоды предоставления не могут пересекаться: '+dates[i].year+' год');
			}
			if(format == 'month' && dates[i].year == dates[j].year && dates[i].month == dates[j].month){
				$('#validateWithdrawPeriodError').show();
				$('#validateWithdrawPeriodError').text('Периоды предоставления не могут пересекаться: '+dates[i].month+' '+dates[i].year+' года');
			}
			if(format == 'date'){
				$.post('ajax/isCrossDate.html',{from1:dates[i].from,from2:dates[j].from,to1:dates[i].to,to2:dates[j].to},
						function(data){
					        if(data!='OK'){
					        	$('#validateWithdrawPeriodError').show();
					        	$('#validateWithdrawPeriodError').text(data);
					        }
					    });
			}
			
		}
	}
}var exclusionHash = {'stopfactorsClient':'stopfactorsClient', 'stopfactorsSecurity':'stopfactorsSecurity',
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
