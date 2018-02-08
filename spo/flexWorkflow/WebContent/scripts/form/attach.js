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
}