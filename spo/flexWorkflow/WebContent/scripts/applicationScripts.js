function actionDo(isAccept) {

		if (isAccept) {
			processGrid.action = 'task.accept.do';
			processGrid.isAccept.value='1';
		} else {
			processGrid.action = 'task.complete.do';
		}
		submitSignedForm();			
		signSelected();		
		processGrid.submit();

}

function getDateInFormat() {
    var currDate = new Date();
    var year = currDate.getYear();
    var month = currDate.getMonth() + 1;
    if (eval(month) < 10) month = "0" + month;
    var day = currDate.getDate();
    if (eval(day) < 10) day = "0" + day;
    var hour = currDate.getHours() + 1;
    if(eval(hour < 10)) hour = "0" + hour;
    var minutes = currDate.getMinutes();
    if(eval(minutes < 10)) minutes = "0" + minutes;

//    return day + "." + month + "." + year + " " + hour + ":" + minutes;
	return day + "." + month + "." + year;

}

function setall(value) {

    for (var i = 0; i < processGrid.elements.length; i++) {
        var nm = processGrid.elements[i].type;
        if (nm == "checkbox") {
            processGrid.elements[i].checked = value;
        }
    }
}
function versionprint() {

	var collection = document.all('signdata');
	var fieldId;
	var fieldChk;
	var parameters = "";

	var colLen = 1;
	var currObj;
	 var count = 0;

	if (collection.nodeName + ""!= "undefined") {
		currObj = collection;
	} else {
		colLen = collection.length
		currObj = collection[0];
	}

	for (var i = 0; i < colLen; i++){
		fieldChk = getNode(currObj,'check');
		if (getValue(fieldChk)) {
			fieldId = getNode(currObj,'id');
			if (fieldId) {
				parameters += "p" + count + "=";
				parameters += fieldId.value;
				parameters += "&";				
                count++;				
			}
		}
		try {
			currObj = collection[i + 1];
		} catch (e) {}
	}			

    window.location.href = "tasks.print.do?" + parameters;

}

function setOnNextBox(currBox, count) {

    var currCheck = 0;
    var isElementMustCheck = false;

    for (var i = 0; i < processGrid.elements.length; i++) {
        var nm = processGrid.elements[i].type;
        
        if (nm == "checkbox") {
       			
            if (processGrid.elements[i].name == currBox) {
                isElementMustCheck = true;
            }

            if (isElementMustCheck) {
                processGrid.elements[i].checked = !processGrid.elements[i].checked;
                currCheck++;
            }

            if (currCheck >= count) {
                break;
            }
        }
    }
}


function sortList(param, isAccept, isMaySort) {

    if (isMaySort) {
        processGrid.paramSort.value = param;
        processGrid.isAccept.value = isAccept;
        processGrid.method = "POST";
        processGrid.action = 'tasks.sort.do';
        processGrid.submit();
    } else {
        alert("\u0421\u043e\u0440\u0442\u0438\u0440\u043e\u0432\u043a\u0430 \u043d\u0435 \u0432\u043e\u0437\u043c\u043e\u0436\u043d\u0430, \u0437\u0430\u0433\u0440\u0443\u0437\u0438\u0442\u0435 \u0432\u0441\u0435 \u0441\u0442\u0440\u0430\u043d\u0438\u0446\u044b");
    }

}

function filterApply(isAccept) {
    processGrid.method = "POST";
    processGrid.action = 'showTaskList.do?accept=' + isAccept;
    processGrid.submit();
}

function openDoc(docURL) {
    docwin = open(docURL);
}

       
function goback() {
     if (confirm("\u0412\u044b \u0434\u0435\u0439\u0441\u0442\u0432\u0438\u0442\u0435\u043b\u044c\u043d\u043e " + 
     "\u0445\u043e\u0442\u0438\u0442\u0435 \u0432\u043e\u0437\u0432\u0440\u0430\u0442\u0438\u0442\u044c " + 
     + "\u0437\u0430\u0434\u0430\u043d\u0438\u0435 \u043d\u0430 \u043f\u0440\u0435\u0434\u044b\u0434\u0443\u0449\u0438\u0439 \u044d\u0442\u0430\u043f?")) {
     	gobackform.submit();
     }
}

function submitForm(actionTarget){

            document.forms["variables"].action = actionTarget;
            document.forms["variables"].method = "POST";
            document.forms["variables"].submit();
}

function pluginAction(cl){
			variables.isOnlyUpdate.value=false;
            variables.action = "updateVariables.do?class=" + cl;
            variables.method = "POST";
            variables.submit();
}

function  selectOption(selectComp, val){
	for (var i=0; i < selectComp.length; i++) {
	
       	if (selectComp[i].value==val) {
      		selectComp.options[i].selected=true;
       		break;
       	}
    }

}
dialogArray = new Array();	//дочерние окна
function openDialog(hrefStr, name, prop){
	var wnd = window.open(hrefStr, name, prop);
	dialogArray[dialogArray.length]=wnd;
	wnd.focus();
	return false;
}
function showSearch() {
	document.getElementById('fullSearchForm').style.display = (document.getElementById('fullSearchForm').style.display == 'none' ? '' : 'none');
}
function assignUserSelectChange(idTask){
	var user = $('#idUser'+idTask+' option:selected').val();
	if(user == '') {
		$('#btnAssign'+idTask).hide();
	} else {
		$('#btnAssign'+idTask).show();
	} 
}
function assignUserOnClick(idTask) {
	if ($('#idUser'+idTask+' :last').val()=='load') {//если еще не подгрузили список
		$.post('ajax/user4assign.do',{taskid:idTask},setUser4Assign);
	}
}
function setUser4Assign(xml) {
	var s = '<option value="">выбрать исполнителя</option>';
	$('user',xml).each(function(index) {
		s += '<option value="'+$('id:first',this).text()+'">'+$('name:first',this).text()+'</option>';
	});
	$('#idUser'+$('taskid:first',xml).text()).html(s);
	//для IE еще раз эмулировать клик чтобы открыть список. Не работает эмуляция.
	if(navigator.userAgent.toLowerCase().indexOf("msie") != -1) { }
}
function assignLink(idTask,typeList){
	var user = $('#idUser'+idTask+' option:selected').val(); 
	if(user == ''){alert('Не выбран сотрудник!'); return false;}  
	window.location='assign.user.do?idTask='+idTask+'&returnTo='+typeList+'&idUser=' + user;
}
function addDecisionStage(ptid,grid){
	var wnd = window.open("popup_stages.jsp?ptid="+ptid+"&grid="+grid, "decision", "top=100, left=100, width=800, height=610, scrollbars=yes, resizable=yes");
    dialogArray[dialogArray.length]=wnd;
    wnd.focus();
    return false;
}
function addStandardPeriodGroup(){
	nextval = nextval +1;
	var newtr = $("#newStandardPeriodGroupTemplate").tmpl( {id:nextval});
	newtr.appendTo( "#main > TBODY" );
}
function addStage(ptid,grid){
	var wnd = window.open("popup_stages.jsp?addstage=1&ptid="+ptid+"&grid="+grid, "decision", "top=100, left=100, width=800, height=610, scrollbars=yes, resizable=yes");
    dialogArray[dialogArray.length]=wnd;
    wnd.focus();
    return false;
}
var nextval = 900000000;
function addValue(grid){
	nextval = nextval +1;
	var tr ='<tr id="tr_'+nextval+'"><td><input type="hidden" name="value_'+grid+'" value="'+nextval+'"><input type="text" value="1" name="period_value_'+grid+'"><br />' +
			'<a href="javascript:;" onclick="$(\'#tr_'+nextval+'\').remove();">исключить</a></td>'+
			'<td><textarea cols="30" rows="6" name="name_value_'+grid+'"></textarea></td>'+
			'<td align="center"><input type="checkbox" name="readonly_'+nextval+'" value="y"></td></tr>';
	$('#standardPeriodValueTable'+grid).append(tr);
}
function validateStandartPeriod(){
	var hasEmptyCriteria = false;
	$('#main > tbody > tr').each(function() {
	    var id = this.id.substring(7);
	    if($('#standardPeriodValueTable'+id+' > tbody > tr').size()>2){
	        $('textarea[name=name_value_'+id+']').each(function() {
	            if(this.value==''){
	                hasEmptyCriteria = true;
	            }
	        });
	    }
	});
	if(hasEmptyCriteria){
		alert('Количество значений нормативных сроков для этапа должно соответствовать количеству критериев дифференциации (при наличии нормативных сроков от двух и более).');
		return false;
	} else {
		$("#edited").val("y");
		return true;
	}
}
function validateRouteProcess(){
	return true;
}
function routeAdd(ptid){
	var wnd = window.open("popup_stages.jsp?ptid="+ptid+"&grid=0", "decision", "top=100, left=100, width=800, height=610, scrollbars=yes, resizable=yes");
    dialogArray[dialogArray.length]=wnd;
    wnd.focus();
    return false;
}
function routeChangeDefDep(routeid){
	var wnd = window.open("ajax/departmentList.html?onlyInitialDep=false&routeid="+routeid, "decision", "top=100, left=100, width=800, height=610, scrollbars=yes, resizable=yes");
	dialogArray[dialogArray.length]=wnd;
	wnd.focus();
	return false;
}
function routeAddInitDep(routeid){
	var wnd = window.open("ajax/departmentList.html?onlyInitialDep=true&routeid="+routeid, "decision", "top=100, left=100, width=800, height=610, scrollbars=yes, resizable=yes");
    dialogArray[dialogArray.length]=wnd;
    wnd.focus();
    return false;
}