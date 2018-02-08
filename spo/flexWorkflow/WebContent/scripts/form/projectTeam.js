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
