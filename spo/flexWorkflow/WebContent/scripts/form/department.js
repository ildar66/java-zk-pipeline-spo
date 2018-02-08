function changeStartDep(){
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
